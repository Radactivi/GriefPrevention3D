package com.example.griefprevention3d.listeners;

import com.example.griefprevention3d.ui.VisualizationManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimToolListener implements Listener {

    private final Map<UUID, Location> corner1 = new HashMap<>();
    private final Map<UUID, Location> corner2 = new HashMap<>();
    private final VisualizationManager visualizationManager;

    public ClaimToolListener(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_SHOVEL) {
            if (event.getAction().isRightClick()) {
                if(event.getClickedBlock() == null) return;
                Location loc = event.getClickedBlock().getLocation();
                if (!corner1.containsKey(player.getUniqueId()) || corner2.containsKey(player.getUniqueId())) {
                    corner1.put(player.getUniqueId(), loc);
                    corner2.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "First corner set.");
                } else {
                    corner2.put(player.getUniqueId(), loc);
                    player.sendMessage(ChatColor.GREEN + "Second corner set. Type /claim create to create your claim.");
                    visualizationManager.showClaimBoundaries(player, corner1.get(player.getUniqueId()), corner2.get(player.getUniqueId()));
                }
                event.setCancelled(true);
            }
        }
    }

    public Location getCorner1(Player player) {
        return corner1.get(player.getUniqueId());
    }

    public Location getCorner2(Player player) {
        return corner2.get(player.getUniqueId());
    }

    public void clearCorners(Player player) {
        corner1.remove(player.getUniqueId());
        corner2.remove(player.getUniqueId());
    }
}
