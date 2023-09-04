package net.uniquepixels.playerqueuepaper.npc.commands;

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.uniquepixels.playerqueuepaper.npc.NPCConfiguration;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import org.bson.types.ObjectId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@RequiredArgsConstructor
public class QueueNPCCommand implements CommandExecutor {

    private final NPCManager npcManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(Component.text("no player"));

            return true;
        }

        if (!player.hasPermission("playerqueue.npc.command")) {
            player.sendMessage(Component.text("no perm"));
            return true;
        }

        // displayname
        // texture

        if (args.length != 1) {

            player.sendMessage(Component.text("Nutze: /queuenpc <name>"));

            return true;
        }

        val location = player.getLocation();

        NPCConfiguration configuration = new NPCConfiguration(new ObjectId(new Date(System.currentTimeMillis())), args[0], "game",
                location.getWorld().getUID().toString(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
        this.npcManager.saveConfiguration(configuration);

        player.sendMessage(Component.text("Saved!"));

        npcManager.spawnNPC(player, configuration);

        return true;
    }
}
