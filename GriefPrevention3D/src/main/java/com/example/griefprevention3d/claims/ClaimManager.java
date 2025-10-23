package com.example.griefprevention3d.claims;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages all claims on the server.
 */
public class ClaimManager {
    private final List<Claim> claims = new ArrayList<>();

    /**
     * Creates a new claim.
     *
     * @param owner   The player who owns the claim.
     * @param corner1 The first corner of the claim.
     * @param corner2 The second corner of the claim.
     * @return The new claim.
     */
    public Claim createClaim(Player owner, Location corner1, Location corner2) {
        Claim claim = new Claim(owner.getUniqueId(), corner1, corner2);
        claims.add(claim);
        return claim;
    }

    /**
     * Deletes a claim.
     *
     * @param claim The claim to delete.
     */
    public void deleteClaim(Claim claim) {
        claims.remove(claim);
    }

    /**
     * Gets the claim at a specific location.
     *
     * @param location The location to check.
     * @return The claim at the location, or null if there is no claim.
     */
    public Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (claim.isInside(location)) {
                return claim;
            }
        }
        return null;
    }

    /**
     * Gets all claims owned by a player.
     *
     * @param player The player to get the claims for.
     * @return A list of claims owned by the player.
     */
    public List<Claim> getPlayerClaims(Player player) {
        return claims.stream()
                .filter(claim -> claim.getOwner().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all claims.
     *
     * @return A list of all claims.
     */
    public List<Claim> getAllClaims() {
        return new ArrayList<>(claims);
    }

    public void addClaim(Claim claim) {
        claims.add(claim);
    }
}