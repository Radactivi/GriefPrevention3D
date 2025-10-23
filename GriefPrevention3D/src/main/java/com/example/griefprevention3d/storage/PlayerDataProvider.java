package com.example.griefprevention3d.storage;

import com.example.griefprevention3d.GriefPrevention3D;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataProvider {

    private final File playerDataFile;
    private final Gson gson;
    private Map<UUID, Integer> claimBlocks = new HashMap<>();

    public PlayerDataProvider(GriefPrevention3D plugin) {
        this.playerDataFile = new File(plugin.getDataFolder(), "playerdata.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadPlayerData();
    }

    public void loadPlayerData() {
        if (!playerDataFile.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(playerDataFile)) {
            Type type = new TypeToken<Map<UUID, Integer>>() {}.getType();
            claimBlocks = gson.fromJson(reader, type);
            if (claimBlocks == null) {
                claimBlocks = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerData() {
        try (FileWriter writer = new FileWriter(playerDataFile)) {
            gson.toJson(claimBlocks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClaimBlocks(UUID playerID) {
        return claimBlocks.getOrDefault(playerID, GriefPrevention3D.getPlugin(GriefPrevention3D.class).getConfig().getInt("new-player-claim-blocks"));
    }

    public void setClaimBlocks(UUID playerID, int amount) {
        claimBlocks.put(playerID, amount);
    }
}