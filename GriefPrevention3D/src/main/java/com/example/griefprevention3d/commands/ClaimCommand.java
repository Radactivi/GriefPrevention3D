package com.example.griefprevention3d.commands;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.claims.Claim;
import com.example.griefprevention3d.claims.ClaimManager;
import com.example.griefprevention3d.listeners.ClaimToolListener;
import com.example.griefprevention3d.fawe.FAWEIntegration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the /claim command.
 */
import com.example.griefprevention3d.claims.ClaimBlockManager;

// ...

public class ClaimCommand implements CommandExecutor {

    private final GriefPrevention3D plugin;
    private final ClaimManager claimManager;
    private final ClaimToolListener claimToolListener;
    private final FAWEIntegration faweIntegration;
    private final ClaimBlockManager claimBlockManager;
    private final Map<UUID, Boolean> subdivideMode = new HashMap<>();

    public ClaimCommand(GriefPrevention3D plugin, ClaimManager claimManager, ClaimToolListener claimToolListener, FAWEIntegration faweIntegration, ClaimBlockManager claimBlockManager) {
        this.plugin = plugin;
        this.claimManager = claimManager;
        this.claimToolListener = claimToolListener;
        this.faweIntegration = faweIntegration;
        this.claimBlockManager = claimBlockManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            HelpCommand.sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("griefprevention3d.claim.create")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to create claims.");
                return true;
            }
            Location corner1 = claimToolListener.getCorner1(player);
            Location corner2 = claimToolListener.getCorner2(player);

            if (corner1 == null || corner2 == null) {
                player.sendMessage(ChatColor.RED + "You must set both corners of your claim with a golden shovel first.");
                return true;
            }

            Claim claim = new Claim(player.getUniqueId(), corner1, corner2);
            int claimVolume = claimBlockManager.getClaimVolume(claim);

            if (!claimBlockManager.hasEnoughClaimBlocks(player, claimVolume)) {
                player.sendMessage(ChatColor.RED + "You do not have enough claim blocks to create this claim.");
                return true;
            }

            if (subdivideMode.getOrDefault(player.getUniqueId(), false)) {
                Claim parentClaim = claimManager.getClaimAt(corner1);
                if (parentClaim == null || !parentClaim.equals(claimManager.getClaimAt(corner2))) {
                    player.sendMessage(ChatColor.RED + "Both corners of a subclaim must be inside the same parent claim.");
                    return true;
                }
                if (!parentClaim.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can only create subclaims in your own claims.");
                    return true;
                }
                claim.setParent(parentClaim);
                parentClaim.getChildren().add(claim);
                claimManager.addClaim(claim);
                player.sendMessage(ChatColor.GREEN + "Subclaim created!");
            } else {
                claimManager.addClaim(claim);
                player.sendMessage(ChatColor.GREEN + "Claim created!");
            }

            claimBlockManager.removeClaimBlocks(player.getUniqueId(), claimVolume);
            claimToolListener.clearCorners(player);

        } else if (args[0].equalsIgnoreCase("abandon")) {
            if (!player.hasPermission("griefprevention3d.claim.abandon")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to abandon claims.");
                return true;
            }
            Claim claim = claimManager.getClaimAt(player.getLocation());
            if (claim == null) {
                player.sendMessage(ChatColor.RED + "You are not standing in a claim.");
                return true;
            }

            if (!claim.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You do not own this claim.");
                return true;
            }

            int claimVolume = claimBlockManager.getClaimVolume(claim);
            claimBlockManager.addClaimBlocks(player.getUniqueId(), claimVolume);
            claimManager.deleteClaim(claim);
            player.sendMessage(ChatColor.GREEN + "Claim abandoned and " + claimVolume + " claim blocks refunded.");
        } else if (args[0].equalsIgnoreCase("trust")) {
            if (!player.hasPermission("griefprevention3d.claim.trust")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to trust players.");
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /claim trust <player> <level>");
                return true;
            }

            Claim claim = claimManager.getClaimAt(player.getLocation());
            if (claim == null) {
                player.sendMessage(ChatColor.RED + "You are not standing in a claim.");
                return true;
            }

            if (!claim.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You do not own this claim.");
                return true;
            }

            Player trustedPlayer = Bukkit.getPlayer(args[1]);
            if (trustedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (trustedPlayer.equals(player)) {
                player.sendMessage(ChatColor.RED + "You cannot trust yourself.");
                return true;
            }

            try {
                Claim.TrustLevel level = Claim.TrustLevel.valueOf(args[2].toUpperCase());
                claim.getTrustedPlayers().put(trustedPlayer.getUniqueId(), level);
                player.sendMessage(ChatColor.GREEN + "Player " + trustedPlayer.getName() + " has been trusted with level " + level.name());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid trust level. Valid levels are: ACCESS, CONTAINER, BUILD, MANAGER");
            }

        } else if (args[0].equalsIgnoreCase("untrust")) {
            if (!player.hasPermission("griefprevention3d.claim.untrust")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to untrust players.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /claim untrust <player>");
                return true;
            }

            Claim claim = claimManager.getClaimAt(player.getLocation());
            if (claim == null) {
                player.sendMessage(ChatColor.RED + "You are not standing in a claim.");
                return true;
            }

            if (!claim.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You do not own this claim.");
                return true;
            }

            Player untrustedPlayer = Bukkit.getPlayer(args[1]);
            if (untrustedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            claim.getTrustedPlayers().remove(untrustedPlayer.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Player " + untrustedPlayer.getName() + " has been untrusted.");
        } else if (args[0].equalsIgnoreCase("subdivide")) {
            if (!player.hasPermission("griefprevention3d.claim.subdivide")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to subdivide claims.");
                return true;
            }
            subdivideMode.put(player.getUniqueId(), !subdivideMode.getOrDefault(player.getUniqueId(), false));
            if (subdivideMode.get(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "Subdivide mode enabled. Use the golden shovel to create a subclaim.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Subdivide mode disabled.");
            }
        } else if (args[0].equalsIgnoreCase("giveblocks")) {
            if (!player.hasPermission("griefprevention3d.admin.giveblocks")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /claim giveblocks <player> <amount>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            try {
                int amount = Integer.parseInt(args[2]);
                claimBlockManager.addClaimBlocks(target.getUniqueId(), amount);
                player.sendMessage(ChatColor.GREEN + "Gave " + amount + " claim blocks to " + target.getName());
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount.");
            }

        } else if (args[0].equalsIgnoreCase("saveall")) {
            if (!player.hasPermission("griefprevention3d.admin.saveall")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            faweIntegration.saveAllClaims(claimManager);
            player.sendMessage(ChatColor.GREEN + "All claims have been saved.");
        } else if (args[0].equalsIgnoreCase("loadall")) {
            if (!player.hasPermission("griefprevention3d.admin.loadall")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            player.sendMessage(ChatColor.GREEN + "Loading all claims...");
            plugin.getLogger().info("Starting /claim loadall command...");
            faweIntegration.loadAllClaims(claimManager).thenRun(() -> {
                player.sendMessage(ChatColor.GREEN + "All claims have been loaded.");
                plugin.getLogger().info("Finished /claim loadall command.");
            });
        } else if (args[0].equalsIgnoreCase("blocks")) {
            if (!player.hasPermission("griefprevention3d.claim.blocks")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to check your claim blocks.");
                return true;
            }
            int currentBlocks = claimBlockManager.getClaimBlocks(player.getUniqueId());
            int maxBlocks = claimBlockManager.getMaxClaimBlocks(player);
            player.sendMessage(ChatColor.GREEN + "You have " + currentBlocks + "/" + maxBlocks + " claim blocks.");
        }

        return true;
    }
}
