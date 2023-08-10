package net.uniquepixels.playerqueue;


import com.velocitypowered.api.proxy.ProxyServer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import eu.cloudnetservice.modules.bridge.platform.velocity.VelocityPlayerManagementListener;
import eu.cloudnetservice.modules.bridge.platform.velocity.commands.VelocityCloudCommand;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.val;
import net.uniquepixels.playerqueue.queue.QueueController;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelIdentifier;
import net.uniquepixels.playerqueue.queue.listening.QueueChannelListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import redis.clients.jedis.JedisPooled;

@Singleton
@PlatformPlugin(
        platform = "velocity",
        authors = "DasShorty",
        name = "PlayerQueue",
        version = "{project.build.version}",
        dependencies = @Dependency(name = "CloudNet-Bridge")

)
public class PlayerQueue implements PlatformEntrypoint {

    private final Object pluginInstance;
    private final ProxyServer proxyServer;
    private final ModuleHelper moduleHelper;
    private final ServiceRegistry serviceRegistry;
    private final VelocityCloudCommand cloudCommand;
    private final VelocityPlayerManagementListener playerListener;

    @Inject
    public PlayerQueue(@NonNull @Named("plugin") Object pluginInstance,
                       @NonNull ProxyServer proxyServer,
                       @NonNull ModuleHelper moduleHelper,
                       @NonNull ServiceRegistry serviceRegistry,
                       @NonNull VelocityCloudCommand cloudCommand,
                       @NonNull VelocityPlayerManagementListener playerListener) {


        this.pluginInstance = pluginInstance;
        this.proxyServer = proxyServer;
        this.moduleHelper = moduleHelper;
        this.serviceRegistry = serviceRegistry;
        this.cloudCommand = cloudCommand;
        this.playerListener = playerListener;
    }

    @Override
    public void onLoad() {

        JedisPooled jedis = new JedisPooled("localhost", 6379);

        val queueController = new QueueController(serviceRegistry, pluginInstance, proxyServer, jedis);

        proxyServer.getChannelRegistrar().register(new QueueChannelIdentifier());
        new QueueChannelListener(proxyServer, queueController, serviceRegistry.firstProvider(ServiceTaskProvider.class));
    }

    @Override
    public void onDisable() {
        PlatformEntrypoint.super.onDisable();
    }
}
