package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceTask;
import lombok.val;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.schemafields.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QueueController {

    private final ServiceRegistry serviceRegistry;
    private final Object pluginInstance;
    private final ProxyServer proxyServer;
    private final JedisPooled jedis;
    private Map<ServiceTask, List<Queue>> runningQueues = new HashMap<>();

    public QueueController(ServiceRegistry serviceRegistry, Object pluginInstance, ProxyServer proxyServer, JedisPooled jedis) {
        this.serviceRegistry = serviceRegistry;
        this.pluginInstance = pluginInstance;
        this.proxyServer = proxyServer;
        this.jedis = jedis;

        jedis.ftCreate("idx:queuePlayers",
                FTCreateParams.createParams()
                        .on(IndexDataType.JSON)
                        .addPrefix("queuePlayers:"),
                TextField.of("$.uuid").as("uuid"),
                TextField.of("$.queueID").as("queueID"));


    }

    public void addPlayersToQueue(ServiceTask task, List<Player> players) {

        val queues = runningQueues.get(task);

        // add player to queue if a slot is free
        for (Queue queue : queues) {

            if (!queue.canQueueHandlePlayers(players.size()))
                continue;

            players.forEach(player -> {

                jedis.jsonSetWithEscape("queuePlayers:" + player.getUsername(), new QueuePlayer(player.getUniqueId().toString(), queue.getQueueId().toString()));

                queue.addPlayerToQueue(player);

            });

            return;
        }

        // TODO - add tasks with player limit to database - connect db with queue creation
        val queue = new Queue(serviceRegistry, task, pluginInstance, proxyServer, 1, 1);
        val queueId = queue.getQueueId();
        queues.add(queue);

    }

    private QueuePlayer findPlayer(UUID player) {

        val query = new Query(player.toString());

        val rawQueueId = jedis.ftSearch("idx:queuePlayers", query.returnFields("queueId"))
                .getDocuments().get(0).getProperties().iterator().next().getValue();

        System.out.println(rawQueueId);

        if (!(rawQueueId instanceof String))
            return null;

        return new QueuePlayer(player.toString(), (String) rawQueueId);
    }

    public void removePlayersFromQueue(ServiceTask task, List<Player> players) {

        val queues = runningQueues.get(task);

        for (Player player : players) {

            val queuePlayer = findPlayer(player.getUniqueId());

            if (queuePlayer == null)
                continue;

            queues.stream().filter(queue -> queue.getQueueId() == queuePlayer.queueId()).forEach(queue -> queue.removePlayerFromQueue(player));
        }

    }


}
