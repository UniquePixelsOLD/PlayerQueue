package net.uniquepixels.playerqueuepaper.npc;

import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public record NPCConfiguration(ObjectId _id, String displayName, String destinationTask, String world,
                               double x, double y, double z, float yaw, float pitch) {

    public Location getLocation() {
        return new Location(Bukkit.getWorld(UUID.fromString(world)),
                x,
                y,
                z,
                yaw,
                pitch);
    }
}
