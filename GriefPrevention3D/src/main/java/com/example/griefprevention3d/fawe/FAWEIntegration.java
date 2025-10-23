package com.example.griefprevention3d.fawe;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.claims.Claim;
import com.example.griefprevention3d.claims.ClaimManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Handles integration with FastAsyncWorldEdit for saving and loading claim contents.
 */
public class FAWEIntegration {

    private final File schematicsDir;

    public FAWEIntegration(GriefPrevention3D plugin) {
        this.schematicsDir = new File(plugin.getDataFolder(), "schematics");
        if (!schematicsDir.exists()) {
            schematicsDir.mkdirs();
        }
    }

    /**
     * Saves all claims as FAWE schematics.
     *
     * @param claimManager The ClaimManager containing the claims to save.
     */
    public void saveAllClaims(ClaimManager claimManager) {
        for (Claim claim : claimManager.getAllClaims()) {
            try {
                File schematicFile = new File(schematicsDir, claim.getClaimID() + ".schem");
                World world = BukkitAdapter.adapt(claim.getCorner1().getWorld());
                BlockVector3 pos1 = BukkitAdapter.asBlockVector(claim.getCorner1());
                BlockVector3 pos2 = BukkitAdapter.asBlockVector(claim.getCorner2());
                CuboidRegion region = new CuboidRegion(world, pos1, pos2);
                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                clipboard.setOrigin(region.getMinimumPoint());

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, clipboard.getOrigin());
                    copy.setCopyingEntities(false);
                    Operations.complete(copy);
                }

                try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
                    writer.write(clipboard);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads all claims from FAWE schematics.
     *
     * @param claimManager The ClaimManager to load the claims into.
     */
    public CompletableFuture<Void> loadAllClaims(ClaimManager claimManager) {
        GriefPrevention3D plugin = GriefPrevention3D.getPlugin(GriefPrevention3D.class);
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Claim claim : claimManager.getAllClaims()) {
                plugin.getLogger().info("Loading claim: " + claim.getClaimID());
                try {
                    File schematicFile = new File(schematicsDir, claim.getClaimID() + ".schem");
                    if (schematicFile.exists()) {
                        plugin.getLogger().info("Schematic file found: " + schematicFile.getPath());
                        World world = BukkitAdapter.adapt(claim.getCorner1().getWorld());
                        ClipboardReader reader = ClipboardFormats.findByFile(schematicFile).getReader(new FileInputStream(schematicFile));
                        Clipboard clipboard = reader.read();

                        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                            BlockVector3 min = BlockVector3.at(
                                    Math.min(claim.getCorner1().getX(), claim.getCorner2().getX()),
                                    Math.min(claim.getCorner1().getY(), claim.getCorner2().getY()),
                                    Math.min(claim.getCorner1().getZ(), claim.getCorner2().getZ())
                            );
                            plugin.getLogger().info("Pasting schematic to: " + min.toString());
                            clipboard.paste(editSession, min, false);
                            editSession.flushSession();
                            plugin.getLogger().info("Paste operation completed for claim: " + claim.getClaimID());
                        }
                    } else {
                        plugin.getLogger().warning("Schematic file not found for claim: " + claim.getClaimID());
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("Error loading claim " + claim.getClaimID() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            future.complete(null);
        });
        return future;
    }
}