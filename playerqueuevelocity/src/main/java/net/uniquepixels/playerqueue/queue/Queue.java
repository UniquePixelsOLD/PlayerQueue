package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceCreateResult;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTask;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Queue implements QueueReference {

    private final ServiceTaskProvider provider;
    private final ServiceTask parent;
    private final List<Player> queuePlayers;
    private final Map<String, BossBar> bossBarMap = Map.of("en", BossBar.bossBar(Component.text("EN_BAR"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS),
            "de", BossBar.bossBar(Component.text("EN_BAR"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
    @Getter
    private final UUID queueId;
    private ServiceCreateResult result;
    private int minPlayers;
    private int maxPlayers;

    public Queue(ServiceRegistry registry, ServiceTask parent, Object pluginInstance, ProxyServer server, int minPlayers, int maxPlayers) {
        this.queueId = UUID.randomUUID();
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        queuePlayers = new ArrayList<>();
        this.provider = registry.firstProvider(ServiceTaskProvider.class);
        this.parent = parent;

        val factory = registry.firstProvider(CloudServiceFactory.class);
        startServer(factory);
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

    private void startServer(CloudServiceFactory factory) {
        val lobbyConfig = ServiceConfiguration.builder(parent).build();
        result = factory.createCloudService(lobbyConfig);
    }

    @Override
    public ServiceInfoSnapshot snapShot() {
        return result.serviceInfo();
    }

    @Override
    public ServiceTask task() {
        return parent;
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
