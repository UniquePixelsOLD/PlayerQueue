package net.uniquepixels.playerqueuemodule;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import jakarta.inject.Singleton;
import net.uniquepixels.playerqueuemodule.server.RequestServerHandler;
import net.uniquepixels.playerqueuemodule.server.RequestServerStatusHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Singleton
public class PlayerQueueModule extends DriverModule {

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
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

        DatabaseHandler databaseHandler = new DatabaseHandler("mongodb://root:root@localhost:27017/?authMechanism=SCRAM-SHA-1");

        HttpServer httpServer = null;
        try {
            httpServer = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(700), 700);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.createContext("/cloud/request-server", new RequestServerHandler(taskProvider, databaseHandler.getHttpToken()));
        httpServer.createContext("/cloud/request-server-status", new RequestServerStatusHandler(databaseHandler.getHttpToken(), serviceRegistry));

        httpServer.start();

        HttpServer finalHttpServer = httpServer;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            databaseHandler.disConnect();
            finalHttpServer.stop(1);

        }));
    }

}
