package net.uniquepixels.playerqueue;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.val;
import net.uniquepixels.playerqueue.queue.QueueController;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelIdentifier;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelListener;
import net.uniquepixels.playerqueue.queue.server.ServerHandler;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPooled;


@Plugin(id = "playerqueuevelocity", name = "PlayerQueue (Velocity)", authors = {"DasShorty"})
@Getter
public class PlayerQueue {

    private final ProxyServer server;
    private final ServerHandler serverHandler;
    private final Logger logger;
    private final DatabaseHandler databaseHandler;

    @Inject
    public PlayerQueue(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.databaseHandler = new DatabaseHandler("");
        this.serverHandler = new ServerHandler(this.databaseHandler);
    }

    @Subscribe
    public void onProxyInitializing(ProxyInitializeEvent event) {
        JedisPooled jedis = new JedisPooled("localhost", 6379);


        val queueController = new QueueController(this, server, jedis);

        server.getChannelRegistrar().register(new QueueChannelIdentifier());
        new QueueChannelListener(server, queueController);
    }
}
