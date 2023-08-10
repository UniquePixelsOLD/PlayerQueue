package net.uniquepixels.playerqueuepaper;

import com.github.retrooper.packetevents.protocol.npc.NPC;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import eu.cloudnetservice.modules.bridge.platform.bukkit.BukkitBridgePlugin;
import eu.cloudnetservice.modules.bridge.platform.bukkit.BukkitPlayerManagementListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
@PlatformPlugin(platform = "bukkit", name = "PlayerQueuePaper", version = "1.0.0")
public class PlayerQueuePaper implements PlatformEntrypoint, CommandExecutor {

    private final JavaPlugin plugin;
    private final ModuleHelper moduleHelper;
    private final PluginManager pluginManager;
    private final ServiceRegistry serviceRegistry;
    private final BukkitBridgePlugin bridgeManagement;
    private final BukkitPlayerManagementListener playerListener;

    @Inject
    public PlayerQueuePaper(JavaPlugin plugin, ModuleHelper moduleHelper, PluginManager pluginManager, ServiceRegistry serviceRegistry, BukkitBridgePlugin bridgeManagement, BukkitPlayerManagementListener playerListener) {
        this.plugin = plugin;
        this.moduleHelper = moduleHelper;
        this.pluginManager = pluginManager;
        this.serviceRegistry = serviceRegistry;
        this.bridgeManagement = bridgeManagement;
        this.playerListener = playerListener;
    }

    @Override
    public void onLoad() {

        plugin.getCommand("test").setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return true;

        val profile = new UserProfile(UUID.randomUUID(), "Testname");
        val world = Bukkit.getWorld("world");
        val npc = new NPC(profile, world.spawnEntity(world.getSpawnLocation(), EntityType.PLAYER).getEntityId());
        npc.spawn(player);

        return true;
    }
}
