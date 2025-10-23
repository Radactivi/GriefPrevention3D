package com.example.griefprevention3d.claims;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.storage.PlayerDataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimBlockManager {

    private final GriefPrevention3D plugin;
    private final PlayerDataProvider playerDataProvider;
    private final int accrualAmount;

    public ClaimBlockManager(GriefPrevention3D plugin, PlayerDataProvider playerDataProvider) {
        this.plugin = plugin;
        this.playerDataProvider = playerDataProvider;
        this.accrualAmount = plugin.getConfig().getInt("claim-block-accrual.amount");
    }

    public int getMaxClaimBlocks(Player player) {
        int maxBlocks = plugin.getConfig().getInt("max-claim-blocks-by-permission.default");

        for (String permissionKey : plugin.getConfig().getConfigurationSection("max-claim-blocks-by-permission").getKeys(false)) {
            if (permissionKey.equals("default")) continue;

            if (player.hasPermission(permissionKey)) {
                int amount = plugin.getConfig().getInt("max-claim-blocks-by-permission." + permissionKey);
                if (amount > maxBlocks) {
                    maxBlocks = amount;
                }
            }
        }
        return maxBlocks;
    }

    public void startAccrualTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int currentBlocks = playerDataProvider.getClaimBlocks(player.getUniqueId());
                int maxBlocks = getMaxClaimBlocks(player);
                if (currentBlocks < maxBlocks) {
                    int newAmount = Math.min(currentBlocks + accrualAmount, maxBlocks);
                    playerDataProvider.setClaimBlocks(player.getUniqueId(), newAmount);
                }
            }
        }, 20 * 3600, 20 * 3600); // Run every hour
    }

    public boolean hasEnoughClaimBlocks(Player player, int amount) {
        return playerDataProvider.getClaimBlocks(player.getUniqueId()) >= amount;
    }

    public int getClaimBlocks(UUID playerID) {
        return playerDataProvider.getClaimBlocks(playerID);
    }

    public void addClaimBlocks(UUID playerID, int amount) {
        int currentBlocks = playerDataProvider.getClaimBlocks(playerID);
        playerDataProvider.setClaimBlocks(playerID, currentBlocks + amount);
    }

    public void removeClaimBlocks(UUID playerID, int amount) {
        int currentBlocks = playerDataProvider.getClaimBlocks(playerID);
        playerDataProvider.setClaimBlocks(playerID, currentBlocks - amount);
    }

    public int getClaimVolume(Claim claim) {
        return (int) (Math.abs(claim.getCorner1().getX() - claim.getCorner2().getX()) + 1) *
               (int) (Math.abs(claim.getCorner1().getY() - claim.getCorner2().getY()) + 1) *
               (int) (Math.abs(claim.getCorner1().getZ() - claim.getCorner2().getZ()) + 1);
    }
}
