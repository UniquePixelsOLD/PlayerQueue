package net.uniquepixels.playerqueuemodule;

import com.sun.net.httpserver.spi.HttpServerProvider;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;
import net.uniquepixels.playerqueuemodule.server.RequestServerHandler;
import net.uniquepixels.playerqueuemodule.server.RequestServerStatusHandler;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Singleton
public class PlayerQueueModule extends DriverModule {

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    @SneakyThrows
    public void startUp(@NotNull ServiceTaskProvider taskProvider, @NotNull CloudServiceFactory serviceFactory, @NotNull ServiceRegistry serviceRegistry) {
        System.out.println("\n" +
                "  _____  _                        ____                        \n" +
                " |  __ \\| |                      / __ \\                       \n" +
                " | |__) | | __ _ _   _  ___ _ __| |  | |_   _  ___ _   _  ___ \n" +
                " |  ___/| |/ _` | | | |/ _ \\ '__| |  | | | | |/ _ \\ | | |/ _ \\ v" + this.version() + "\n" +
                " | |    | | (_| | |_| |  __/ |  | |__| | |_| |  __/ |_| |  __/\n" +
                " |_|    |_|\\__,_|\\__, |\\___|_|   \\___\\_\\\\__,_|\\___|\\__,_|\\___|\n" +
                "                  __/ |                                       \n" +
                "                 |___/                                        \n");

        val databaseHandler = new DatabaseHandler("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        val httpServer = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(700), 700);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.createContext("/cloud/request-server", new RequestServerHandler(taskProvider, databaseHandler.getHttpToken()));
        httpServer.createContext("/cloud/request-server-status", new RequestServerStatusHandler(taskProvider, databaseHandler.getHttpToken()));

        httpServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            databaseHandler.disConnect();
            httpServer.stop(1);

        }));
    }

}
