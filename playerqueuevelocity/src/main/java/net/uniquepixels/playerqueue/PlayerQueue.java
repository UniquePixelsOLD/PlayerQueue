package net.uniquepixels.playerqueue;


import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.uniquepixels.playerqueue.queue.QueueController;
import net.uniquepixels.playerqueue.queue.command.QueueLeaveCommand;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelListener;
import net.uniquepixels.playerqueue.queue.server.ServerHandler;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPooled;

import java.util.Locale;
import java.util.ResourceBundle;


@Plugin(id = "playerqueuevelocity", name = "PlayerQueue (Velocity)", authors = {"DasShorty"})
@Getter
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

        setupLang();

        val databaseHandler = new DatabaseHandler("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");
        val serverHandler = new ServerHandler(databaseHandler);

        JedisPooled jedis = new JedisPooled("localhost", 6379);

        val queueController = new QueueController(this, this.server, jedis, serverHandler);

        val queueChannelIdentifier = MinecraftChannelIdentifier.forDefaultNamespace("gamequeue");
        this.server.getChannelRegistrar().register(queueChannelIdentifier);

        this.server.getEventManager().register(this, new QueueChannelListener(this.server, queueController));

        CommandManager commandManager = server.getCommandManager();

        CommandMeta commandMeta = commandManager.metaBuilder("leave")
                .plugin(this)
                .build();

        commandManager.register(commandMeta, new QueueLeaveCommand().createCommand(this.server, queueController));
    }

    private void setupLang() {
        val registry = TranslationRegistry.create(Key.key("uniquepixels:playerqueue"));
        val bundle = ResourceBundle.getBundle("translation");
        registry.registerAll(Locale.ENGLISH, bundle, true);
        GlobalTranslator.translator().addSource(registry);
    }
}
