package net.uniquepixels.playerqueue.queue.listening;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.uniquepixels.playerqueue.queue.QueueController;
import net.uniquepixels.playerqueue.queue.server.ServerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QueueChannelListener {
    private final ProxyServer proxyServer;
    private final QueueController controller;

    public QueueChannelListener(ProxyServer proxyServer, QueueController controller) {
        this.proxyServer = proxyServer;
        this.controller = controller;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {

        if (!event.getIdentifier().getId().equals("minecraft:gamequeue"))
            return;

        ByteArrayDataInput dataStream = event.dataAsDataStream();

        String channelId = dataStream.readUTF();

        switch (channelId.toLowerCase()) {
            case "player:add" -> {

                String cloudNetTaskName = dataStream.readUTF();
                List<Player> playerList = convertPlayerList(dataStream.readUTF());

                controller.addPlayersToQueue(new ServerTask(cloudNetTaskName), playerList);
            }
            case "player:remove" -> {

                String cloudNetTaskName = dataStream.readUTF();
                List<Player> playerList = convertPlayerList(dataStream.readUTF());

                controller.removePlayersFromQueue(new ServerTask(cloudNetTaskName), playerList);
            }
        }

    }

    private List<Player> convertPlayerList(String list) {

        List<Player> playerList = new ArrayList<>();

        for (String s : list.split(",")) {
            String rawUUID = s.trim().replace("[", "").replace("]", "");

            Optional<Player> player = proxyServer.getPlayer(UUID.fromString(rawUUID));

            if (player.isEmpty()) {
                System.out.println("Player with id (" + rawUUID + ") is empty!");
                continue;
            }

            playerList.add(player.get());

        }

        return playerList;
    }

}
