package net.uniquepixels.playerqueuepaper.npc.listener;

import net.uniquepixels.playerqueuepaper.npc.NPCConfiguration;
import net.uniquepixels.playerqueuepaper.npc.NPCManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCPlayerJoinListener implements Listener {

    private final NPCManager npcManager;

    public NPCPlayerJoinListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //val player = ((CraftPlayer) event.getPlayer()).getHandle();
        //val pipeline = player.connection.connection.channel.pipeline();
        //pipeline.addLast(new NettyQueueIncomingPacketHandler());
        //pipeline.addAfter("decoder", "QueuePacketInjector", new NettyQueueIncomingPacketHandler());


        this.npcManager.getAllConfigurationIds().forEachRemaining(objectId -> {
            NPCConfiguration configuration = this.npcManager.getConfiguration(objectId);
            assert configuration != null;
            this.npcManager.spawnNPC(event.getPlayer(), configuration);
        });

    }

}
