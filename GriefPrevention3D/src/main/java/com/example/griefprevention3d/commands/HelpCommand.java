package com.example.griefprevention3d.commands;

import org.bukkit.command.CommandSender;

public class HelpCommand {

    public static void sendHelp(CommandSender sender) {
        sender.sendMessage("--- GriefPrevention3D Help ---");
        sender.sendMessage("/claim create - Creates a new claim.");
        sender.sendMessage("/claim abandon - Deletes the claim you are standing in.");
        sender.sendMessage("/claim trust <player> <level> - Trusts a player in your claim.");
        sender.sendMessage("/claim untrust <player> - Untrusts a player in your claim.");
        sender.sendMessage("/claim subdivide - Toggles subdivide mode.");
        sender.sendMessage("/claim blocks - Shows your claim block balance.");
        sender.sendMessage("/claim giveblocks <player> <amount> - Gives claim blocks to a player. (Admin only)");

    }
}
