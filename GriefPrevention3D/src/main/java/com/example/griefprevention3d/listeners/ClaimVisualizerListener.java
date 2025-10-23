package com.example.griefprevention3d.listeners;

import com.example.griefprevention3d.claims.Claim;
import com.example.griefprevention3d.claims.ClaimManager;
import com.example.griefprevention3d.ui.VisualizationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClaimVisualizerListener implements Listener {

    private final ClaimManager claimManager;
    private final VisualizationManager visualizationManager;

    public ClaimVisualizerListener(ClaimManager claimManager, VisualizationManager visualizationManager) {
        this.claimManager = claimManager;
        this.visualizationManager = visualizationManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.STICK && event.getAction().isRightClick() && event.getClickedBlock() != null) {
            if (!player.hasPermission("griefprevention3d.visualize")) {
                return;
            }
            Claim claim = claimManager.getClaimAt(event.getClickedBlock().getLocation());
            if (claim != null) {
                visualizationManager.showClaimBoundaries(player, claim);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(ChatColor.GREEN + "This claim belongs to " + Bukkit.getOfflinePlayer(claim.getOwner()).getName());
            }
        }
    }
}