package net.uniquepixels.playerqueuepaper;

import lombok.val;
import net.uniquepixels.coreapi.database.MongoDatabase;
import net.uniquepixels.playerqueuepaper.npc.NPCConfiguration;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

public class PlayerQueuePaper extends JavaPlugin {

    private MongoDatabase database;

    @Override
    public void onEnable() {
        this.database = new MongoDatabase("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        val npcManager = new NPCManager(this.database);

        npcManager.saveConfiguration(new NPCConfiguration(new ObjectId(new Date()), "NPC", Bukkit.getWorld("world").getUID().toString(),
                "1f9905df-3e25-4852-b7b8-92e1e61e67ac"));

    }
}
