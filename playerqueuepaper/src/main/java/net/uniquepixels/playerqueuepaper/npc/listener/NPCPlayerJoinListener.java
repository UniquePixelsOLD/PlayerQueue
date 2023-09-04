package net.uniquepixels.playerqueuepaper.npc.listener;

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class NPCPlayerJoinListener implements Listener {

    private final NPCManager npcManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //val player = ((CraftPlayer) event.getPlayer()).getHandle();
        //val pipeline = player.connection.connection.channel.pipeline();
        //pipeline.addLast(new NettyQueueIncomingPacketHandler());
        //pipeline.addAfter("decoder", "QueuePacketInjector", new NettyQueueIncomingPacketHandler());


        this.npcManager.getAllConfigurationIds().forEachRemaining(objectId -> {
            val configuration = this.npcManager.getConfiguration(objectId);
            this.npcManager.spawnNPC(event.getPlayer(), configuration);
        });

    }

}
