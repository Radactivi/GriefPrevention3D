package com.example.griefprevention3d.claims;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a 3D claim.
 */
public class Claim {
    private final UUID claimID;
    private final UUID owner;
    private Location corner1;
    private Location corner2;
    private final Map<UUID, TrustLevel> trustedPlayers = new HashMap<>();
    private final List<Claim> children = new ArrayList<>();
    private Claim parent;

    public enum TrustLevel {
        ACCESS, CONTAINER, BUILD, MANAGER
    }

    public Claim(UUID owner, Location corner1, Location corner2) {
        this.claimID = UUID.randomUUID();
        this.owner = owner;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public UUID getClaimID() {
        return claimID;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public Map<UUID, TrustLevel> getTrustedPlayers() {
        return trustedPlayers;
    }

    public List<Claim> getChildren() {
        return children;
    }

    public Claim getParent() {
        return parent;
    }

    public void setParent(Claim parent) {
        this.parent = parent;
    }

    public boolean isInside(Location location) {
        double x1 = Math.min(corner1.getX(), corner2.getX());
        double y1 = Math.min(corner1.getY(), corner2.getY());
        double z1 = Math.min(corner1.getZ(), corner2.getZ());
        double x2 = Math.max(corner1.getX(), corner2.getX());
        double y2 = Math.max(corner1.getY(), corner2.getY());
        double z2 = Math.max(corner1.getZ(), corner2.getZ());

        return location.getX() >= x1 && location.getX() <= x2 &&
               location.getY() >= y1 && location.getY() <= y2 &&
               location.getZ() >= z1 && location.getZ() <= z2;
    }

    public boolean hasPermission(UUID player, TrustLevel level) {
        if (owner.equals(player)) {
            return true;
        }
        TrustLevel playerTrustLevel = trustedPlayers.get(player);
        if (playerTrustLevel != null) {
            return playerTrustLevel.ordinal() >= level.ordinal();
        }
        if (parent != null) {
            return parent.hasPermission(player, level);
        }
        return false;
    }
}
