package net.uniquepixels.playerqueue.queue.listening;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.uniquepixels.playerqueue.queue.QueueController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class QueueChannelListener {

    private final ProxyServer proxyServer;
    private final QueueController controller;

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {

        if (!event.getIdentifier().getId().equals("uniquepixels:queue"))
            return;

        val dataStream = event.dataAsDataStream();
        val channelId = dataStream.readUTF();

        switch (channelId.toLowerCase()) {
            case "player:add" -> {

                val cloudNetTaskName = dataStream.readUTF();
                val playerList = convertPlayerList(dataStream.readUTF());

                controller.addPlayersToQueue(serviceTaskProvider.serviceTask(cloudNetTaskName), playerList);
            }
            case "player:remove" -> {

                val cloudNetTaskName = dataStream.readUTF();
                val playerList = convertPlayerList(dataStream.readUTF());

                controller.removePlayersFromQueue(serviceTaskProvider.serviceTask(cloudNetTaskName), playerList);
            }
        }

    }

    private List<Player> convertPlayerList(String list) {

        val playerList = new ArrayList<Player>();

        for (String s : list.split(",")) {
            val rawUUID = s.trim().replace("(", "").replace(")", "");

            val player = proxyServer.getPlayer(UUID.fromString(rawUUID));

            if (player.isEmpty()) {
                System.out.println("Player with id (" + rawUUID + ") is empty!");
                continue;
            }

            playerList.add(player.get());

        }

        return playerList;
    }

}
