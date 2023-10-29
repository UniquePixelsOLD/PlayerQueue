package net.uniquepixels.playerqueuepaper;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import net.uniquepixels.coreapi.database.MongoDatabase;
import net.uniquepixels.playerqueuepaper.npc.NPCDataType;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import net.uniquepixels.playerqueuepaper.npc.commands.QueueNPCCommand;
import net.uniquepixels.playerqueuepaper.npc.listener.NPCPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.ResourceBundle;

public class PlayerQueuePaper extends JavaPlugin {

    private final NPCDataType npcDataType = new NPCDataType();
    private MongoDatabase database;

    @Override
    public void onEnable() {
        this.database = new MongoDatabase("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        TranslationRegistry registry = TranslationRegistry.create(Key.key("uniquepixels:translation"));

        ResourceBundle bundle = ResourceBundle.getBundle("translations_EN", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.ENGLISH, bundle, true);
        GlobalTranslator.translator().addSource(registry);

        NPCManager npcManager = new NPCManager(this.database, this, npcDataType);

        getCommand("queuenpc").setExecutor(new QueueNPCCommand(npcManager));

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new NPCPlayerJoinListener(npcManager), this);
        pluginManager.registerEvents(npcManager, this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "minecraft:gamequeue");
    }
}
