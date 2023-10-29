package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import net.uniquepixels.playerqueue.PlayerQueue;
import net.uniquepixels.playerqueue.queue.server.ServerHandler;
import net.uniquepixels.playerqueue.queue.server.ServerTask;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.schemafields.TextField;

import java.util.*;

public class QueueController {
    private final PlayerQueue pluginInstance;
    private final ProxyServer proxyServer;
    private final JedisPooled jedis;
    private final ServerHandler serverHandler;
    private final Map<ServerTask, List<Queue>> runningQueues = new HashMap<>();

    public QueueController(PlayerQueue pluginInstance, ProxyServer proxyServer, JedisPooled jedis, ServerHandler serverHandler) {
        this.pluginInstance = pluginInstance;
        this.proxyServer = proxyServer;
        this.jedis = jedis;
        this.serverHandler = serverHandler;

        if (jedis.ftSearch("idx:queuePlayers") != null)
            return;

        jedis.ftCreate("idx:queuePlayers",
                FTCreateParams.createParams()
                        .on(IndexDataType.JSON)
                        .addPrefix("queuePlayers:"),
                TextField.of("$.uuid").as("uuid"),
                TextField.of("$.queueID").as("queueID"),
                TextField.of("$.serverTask").as("serverTask"));


    }

    public boolean isPlayerInQueue(UUID uuid) {

        Query query = new Query(uuid.toString());

        List<Document> documents = jedis.ftSearch("idx:queuePlayers", query.returnFields("queueId"))
                .getDocuments();

        return !documents.isEmpty();
    }

    public void addPlayersToQueue(ServerTask task, List<Player> players) {

        List<Queue> queues = this.runningQueues.get(task);

        if (queues != null)
            // add player to queue if a slot is free
            for (Queue queue : queues) {

                if (!queue.canQueueHandlePlayers(players.size()))
                    continue;

                addToRedis(players, queue, task);

                return;
            }

        // TODO - add tasks with player limit to database - connect db with queue creation
        Queue queue = new Queue(this.serverHandler, task, pluginInstance, proxyServer, 1, 1);

        if (queues == null) {
            queues = new ArrayList<>();
            queues.add(queue);
            runningQueues.put(task, queues);
        } else
            queues.add(queue);


        addToRedis(players, queue, task);
    }

    private void addToRedis(List<Player> players, Queue queue, ServerTask task) {
        players.forEach(player -> {

            jedis.jsonSetWithEscape("queuePlayers:" + player.getUsername(), new QueuePlayer(player.getUniqueId().toString(), queue.getQueueId().toString(), task.getTaskName()));

            queue.addPlayerToQueue(player);

        });
    }

    public QueuePlayer findPlayer(UUID player) {

        Query query = new Query(player.toString());

        Iterator<Map.Entry<String, Object>> iterator = jedis.ftSearch("idx:queuePlayers", query.returnFields("queueId", "serverTask"))
                .getDocuments().get(0).getProperties().iterator();

        Object rawQueueId = iterator.next().getValue();
        Object rawServerTask = iterator.next().getValue();

        if (!(rawQueueId instanceof String))
            return null;

        if (!(rawServerTask instanceof String))
            return null;

        return new QueuePlayer(player.toString(), (String) rawQueueId, ((String) rawServerTask));
    }

    public void removePlayersFromQueue(ServerTask task, List<Player> players) {

        val queues = runningQueues.get(task);

        for (Player player : players) {

            QueuePlayer queuePlayer = this.findPlayer(player.getUniqueId());

            if (queuePlayer == null)
                continue;

            queues.stream().filter(queue -> queue.getQueueId() == queuePlayer.queueId()).forEach(queue -> queue.removePlayerFromQueue(player));
        }

    }


}
