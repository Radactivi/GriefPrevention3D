package com.example.griefprevention3d.storage;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.UUID;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name("world").value(value.getWorld().getUID().toString());
        out.name("x").value(value.getX());
        out.name("y").value(value.getY());
        out.name("z").value(value.getZ());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        in.beginObject();
        UUID worldUID = null;
        double x = 0, y = 0, z = 0;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "world":
                    worldUID = UUID.fromString(in.nextString());
                    break;
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "z":
                    z = in.nextDouble();
                    break;
            }
        }
        in.endObject();
        World world = Bukkit.getWorld(worldUID);
        return new Location(world, x, y, z);
    }
}
