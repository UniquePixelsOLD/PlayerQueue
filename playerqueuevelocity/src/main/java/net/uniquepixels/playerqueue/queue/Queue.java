package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.uniquepixels.playerqueue.PlayerQueue;
import net.uniquepixels.playerqueue.queue.server.ServerData;
import net.uniquepixels.playerqueue.queue.server.ServerHandler;
import net.uniquepixels.playerqueue.queue.server.ServerTask;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestNewCloudServer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Queue implements QueueReference {

    private ServerData data = null;
    private final List<Player> queuePlayers;
    private final Map<String, BossBar> bossBarMap = Map.of("en", BossBar.bossBar(Component.text("EN_BAR"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS),
            "de", BossBar.bossBar(Component.text("EN_BAR"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
    @Getter
    private final UUID queueId;
    private final int minPlayers;
    private final int maxPlayers;

    public Queue(ServerHandler serverHandler, ServerTask parent, PlayerQueue pluginInstance, ProxyServer server, int minPlayers, int maxPlayers) {
        this.queueId = UUID.randomUUID();
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        queuePlayers = new ArrayList<>();

        server.getScheduler().buildTask(pluginInstance, () -> init(serverHandler, parent, pluginInstance, server)).delay(1, TimeUnit.SECONDS);
    }

    private void init(ServerHandler serverHandler, ServerTask parent, PlayerQueue pluginInstance, ProxyServer server) {
        try {
            val future = serverHandler.requestNewServer(new RequestNewCloudServer(parent.getTaskName()));

            if (future.isCancelled())

                data = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        startBossBarUpdater(pluginInstance, server);
    }

    private void startBossBarUpdater(Object pluginInstance, ProxyServer server) {

        server.getScheduler().buildTask(pluginInstance, this::sendBossBarsToPlayer).repeat(Duration.ofSeconds(2));

    }

    public boolean canQueueHandlePlayers(int playerSize) {
        return queuePlayers.size() >= minPlayers && queuePlayers.size() + playerSize <= maxPlayers;
    }

    public void addPlayerToQueue(Player player) {
        queuePlayers.add(player);

        player.showBossBar(bossBarMap.get(0));
    }

    public void removePlayerFromQueue(Player player) {
        queuePlayers.remove(player);

        player.hideBossBar(bossBarMap.get(0));

    }

    @Override
    public ServerData data() {
        return this.data;
    }

    @Override
    public Map<String, BossBar> languageBossBars() {
        return bossBarMap;
    }

    @Override
    public void sendBossBarsToPlayer() {

        for (String language : bossBarMap.keySet()) {

            bossBarMap.get(language)
                    .name(Component.text("Players: " + queuePlayers.size() + " / " + minPlayers));

        }

    }
}
