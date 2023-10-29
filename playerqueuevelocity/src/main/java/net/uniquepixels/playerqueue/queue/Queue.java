package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.uniquepixels.playerqueue.PlayerQueue;
import net.uniquepixels.playerqueue.queue.server.ServerData;
import net.uniquepixels.playerqueue.queue.server.ServerHandler;
import net.uniquepixels.playerqueue.queue.server.ServerStatus;
import net.uniquepixels.playerqueue.queue.server.ServerTask;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestNewCloudServer;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestServerStatus;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Queue implements QueueReference {

    private final List<Player> queuePlayers;
    private final Map<String, QueueBar> queueBarMap = new HashMap<>();
    private final UUID queueId;
    private final int minPlayers;
    private final int maxPlayers;
    private ServerData data = null;
    public Queue(ServerHandler serverHandler, ServerTask parent, PlayerQueue pluginInstance, ProxyServer server, int minPlayers, int maxPlayers) {
        this.queueId = UUID.randomUUID();
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        queuePlayers = new ArrayList<>();

        this.queueBarMap.put("en", new QueueBar());
        this.queueBarMap.put("de", new QueueBar());

        init(serverHandler, parent);
        server.getScheduler().buildTask(pluginInstance, () -> getStatus(serverHandler)).repeat(10, TimeUnit.SECONDS);
    }

    public UUID getQueueId() {
        return queueId;
    }

    private void init(ServerHandler serverHandler, ServerTask parent) {
        try {
            CompletableFuture<ServerData> future = serverHandler.requestNewServer(new RequestNewCloudServer(parent.getTaskName()));

            if (future.isCancelled())

                data = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void getStatus(ServerHandler serverHandler) {
        CompletableFuture<ServerStatus> statusFromServer = serverHandler.getStatusFromServer(new RequestServerStatus(this.data.getServerName(), this.data.getTask().getTaskName()));
    }

    public boolean canQueueHandlePlayers(int playerSize) {
        return queuePlayers.size() >= minPlayers && queuePlayers.size() + playerSize <= maxPlayers;
    }

    public void addPlayerToQueue(Player player) {
        queuePlayers.add(player);
        this.queueBarMap.get("en").addPlayer(player);
    }

    public void removePlayerFromQueue(Player player) {
        queuePlayers.remove(player);
        this.queueBarMap.get("en").removePlayer(player);
    }

    @Override
    public ServerData data() {
        return this.data;
    }

    @Override
    public Map<String, QueueBar> languageBossBars() {
        return this.queueBarMap;
    }
}
