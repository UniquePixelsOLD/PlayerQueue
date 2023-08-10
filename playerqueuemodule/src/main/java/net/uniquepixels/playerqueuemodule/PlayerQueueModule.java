package net.uniquepixels.playerqueuemodule;

import com.sun.net.httpserver.spi.HttpServerProvider;
import eu.cloudnetservice.driver.module.Module;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;
import net.uniquepixels.playerqueuemodule.server.RequestServerHandler;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Singleton
public class PlayerQueueModule extends DriverModule {

    private final ServiceTaskProvider taskProvider;
    private final CloudServiceFactory serviceFactory;

    @Inject
    @SneakyThrows
    public PlayerQueueModule(@NotNull ServiceTaskProvider taskProvider, @NotNull CloudServiceFactory serviceFactory) {
        this.taskProvider = taskProvider;
        this.serviceFactory = serviceFactory;

        val databaseHandler = new DatabaseHandler("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        val httpServer = HttpServerProvider.provider().createHttpServer(new InetSocketAddress("localhost", 557), 557);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.createContext("/server/request", new RequestServerHandler(taskProvider, serviceFactory, databaseHandler.getPlatformToken()));

        httpServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            databaseHandler.disConnect();
            httpServer.stop(1);

        }));
    }

}
