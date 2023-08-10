package net.uniquepixels.playerqueue;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import net.uniquepixels.playerqueue.queue.QueueController;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelIdentifier;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelListener;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPooled;


@Plugin(id = "playerqueuevelocity", name = "PlayerQueue (Velocity)", authors = {"DasShorty"})
public class PlayerQueue {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public PlayerQueue(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitializing(ProxyInitializeEvent event) {
        JedisPooled jedis = new JedisPooled("localhost", 6379);

        val queueController = new QueueController(this, server, jedis);

        server.getChannelRegistrar().register(new QueueChannelIdentifier());
        new QueueChannelListener(server, queueController));
    }
}
