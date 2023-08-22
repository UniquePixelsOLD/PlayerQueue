package net.uniquepixels.playerqueuepaper.npc;

import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

public record NPCConfiguration(ObjectId _id, String displayName, String world, String fromTexture) {

    public World getWorld() {
        return Bukkit.getWorld(UUID.fromString(world));
    }
}
