package com.example.griefprevention3d.ui;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.claims.Claim;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Handles the visualization of claims using particles.
 */
public class VisualizationManager {
    /**
     * Shows the boundaries of a claim to a player using particles.
     *
     * @param player The player to show the visualization to.
     * @param claim  The claim to visualize.
     */
    public void showClaimBoundaries(Player player, Claim claim) {
        if (claim == null) return;
        showClaimBoundaries(player, claim.getCorner1(), claim.getCorner2());
    }

    public void showClaimBoundaries(Player player, Location corner1, Location corner2) {
        if (corner1 == null || corner2 == null) return;

        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        player.spawnParticle(Particle.valueOf(GriefPrevention3D.getPlugin(GriefPrevention3D.class).getConfig().getString("visualization.particle")), x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
