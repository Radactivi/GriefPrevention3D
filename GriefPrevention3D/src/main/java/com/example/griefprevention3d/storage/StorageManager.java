package com.example.griefprevention3d.storage;

import com.example.griefprevention3d.GriefPrevention3D;
import com.example.griefprevention3d.claims.Claim;
import com.example.griefprevention3d.claims.ClaimManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Handles saving and loading of claims to/from a storage system (e.g., JSON, database).
 */
public class StorageManager {

    private final File claimsFile;
    private final Gson gson;

    public StorageManager(GriefPrevention3D plugin) {
        this.claimsFile = new File(plugin.getDataFolder(), "claims.json");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Loads all claims from storage.
     *
     * @param claimManager The ClaimManager to load the claims into.
     */
    public void loadClaims(ClaimManager claimManager) {
        if (!claimsFile.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(claimsFile)) {
            Type type = new TypeToken<List<Claim>>() {}.getType();
            List<Claim> claims = gson.fromJson(reader, type);
            if (claims != null) {
                for (Claim claim : claims) {
                    claimManager.addClaim(claim);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves all claims to storage.
     *
     * @param claimManager The ClaimManager to save the claims from.
     */
    public void saveClaims(ClaimManager claimManager) {
        try (FileWriter writer = new FileWriter(claimsFile)) {
            gson.toJson(claimManager.getAllClaims(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}