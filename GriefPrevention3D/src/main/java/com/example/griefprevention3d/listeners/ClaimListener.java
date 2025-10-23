package com.example.griefprevention3d.listeners;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.claims.Claim;
import com.example.griefprevention3d.claims.ClaimManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens for player events and protects claims.
 */
public class ClaimListener implements Listener {
    private final ClaimManager claimManager;

    public ClaimListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

// ...

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Claim claim = claimManager.getClaimAt(event.getBlock().getLocation());
        if (claim != null && !claim.hasPermission(player.getUniqueId(), Claim.TrustLevel.BUILD)) {
            event.setCancelled(true);
            player.sendMessage(GriefPrevention3D.getPlugin(GriefPrevention3D.class).getMessage("messages.no-permission-break"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Claim claim = claimManager.getClaimAt(event.getBlock().getLocation());
        if (claim != null && !claim.hasPermission(player.getUniqueId(), Claim.TrustLevel.BUILD)) {
            event.setCancelled(true);
            player.sendMessage(GriefPrevention3D.getPlugin(GriefPrevention3D.class).getMessage("messages.no-permission-place"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;

        Claim claim = claimManager.getClaimAt(event.getClickedBlock().getLocation());
        if (claim == null) return;

        if (event.getClickedBlock().getType().isInteractable()) {
            if (!claim.hasPermission(player.getUniqueId(), Claim.TrustLevel.ACCESS)) {
                event.setCancelled(true);
                player.sendMessage(GriefPrevention3D.getPlugin(GriefPrevention3D.class).getMessage("messages.no-permission-interact"));
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> claimManager.getClaimAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (org.bukkit.block.Block block : event.getBlocks()) {
            Claim claim = claimManager.getClaimAt(block.getRelative(event.getDirection()).getLocation());
            if (claim != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (org.bukkit.block.Block block : event.getBlocks()) {
            Claim claim = claimManager.getClaimAt(block.getLocation());
            if (claim != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Claim fromClaim = claimManager.getClaimAt(event.getBlock().getLocation());
        Claim toClaim = claimManager.getClaimAt(event.getToBlock().getLocation());
        if (fromClaim == null && toClaim != null) {
            event.setCancelled(true);
        }
    }
}