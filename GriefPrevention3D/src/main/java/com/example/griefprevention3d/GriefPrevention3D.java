package com.example.griefprevention3d;

import com.example.griefprevention3d.claims.ClaimBlockManager;
import com.example.griefprevention3d.claims.ClaimManager;
import com.example.griefprevention3d.commands.ClaimCommand;
import com.example.griefprevention3d.fawe.FAWEIntegration;
import com.example.griefprevention3d.listeners.ClaimListener;
import com.example.griefprevention3d.listeners.ClaimToolListener;
import com.example.griefprevention3d.listeners.ClaimVisualizerListener;
import com.example.griefprevention3d.storage.PlayerDataProvider;
import com.example.griefprevention3d.storage.StorageManager;
import com.example.griefprevention3d.ui.VisualizationManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class GriefPrevention3D extends JavaPlugin {

    private ClaimManager claimManager;
    private StorageManager storageManager;
    private FAWEIntegration faweIntegration;
    private VisualizationManager visualizationManager;
    private PlayerDataProvider playerDataProvider;
    private ClaimBlockManager claimBlockManager;

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();

        // Initialize managers
        claimManager = new ClaimManager();
        storageManager = new StorageManager(this);
        faweIntegration = new FAWEIntegration(this);
        visualizationManager = new VisualizationManager();
        playerDataProvider = new PlayerDataProvider(this);
        claimBlockManager = new ClaimBlockManager(this, playerDataProvider);

        // Load claims from storage
        storageManager.loadClaims(claimManager);

        // Start accrual task
        claimBlockManager.startAccrualTask();

        // Register command
        ClaimToolListener claimToolListener = new ClaimToolListener(visualizationManager);
        this.getCommand("claim").setExecutor(new ClaimCommand(this, claimManager, claimToolListener, faweIntegration, claimBlockManager));

        // Register listeners
        getServer().getPluginManager().registerEvents(new ClaimListener(claimManager), this);
        getServer().getPluginManager().registerEvents(claimToolListener, this);
        getServer().getPluginManager().registerEvents(new ClaimVisualizerListener(claimManager, visualizationManager), this);

        getLogger().info("GriefPrevention3D has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save claims to storage
        storageManager.saveClaims(claimManager);
        // Save player data
        playerDataProvider.savePlayerData();

        getLogger().info("GriefPrevention3D has been disabled!");
    }

    // Getters for managers
    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public FAWEIntegration getFaweIntegration() {
        return faweIntegration;
    }

    public VisualizationManager getVisualizationManager() {
        return visualizationManager;
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }
}
