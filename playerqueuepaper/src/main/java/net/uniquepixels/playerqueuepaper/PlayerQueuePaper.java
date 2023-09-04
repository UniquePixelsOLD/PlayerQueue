package net.uniquepixels.playerqueuepaper;

import lombok.val;
import net.uniquepixels.coreapi.database.MongoDatabase;
import net.uniquepixels.playerqueuepaper.npc.NPCDataType;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import net.uniquepixels.playerqueuepaper.npc.commands.QueueNPCCommand;
import net.uniquepixels.playerqueuepaper.npc.listener.NPCPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQueuePaper extends JavaPlugin {

    private final NPCDataType npcDataType = new NPCDataType();
    private MongoDatabase database;

    @Override
    public void onEnable() {
        this.database = new MongoDatabase("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        val npcManager = new NPCManager(this.database, this, npcDataType);

        getCommand("queuenpc").setExecutor(new QueueNPCCommand(npcManager));

        val pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new NPCPlayerJoinListener(npcManager), this);
        pluginManager.registerEvents(npcManager, this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "uniquepixels:queue");
    }
}
