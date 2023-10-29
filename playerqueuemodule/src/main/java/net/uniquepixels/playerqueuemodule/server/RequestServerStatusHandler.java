package net.uniquepixels.playerqueuemodule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import net.uniquepixels.playerqueuemodule.server.httpbody.ServerStatusBody;
import net.uniquepixels.playerqueuemodule.server.httpbody.ServerStatusResponseBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class RequestServerStatusHandler implements HttpHandler {

    private final CloudServiceProvider serviceProvider;
    private final String httpToken;
    private final Map<String, String> serverLifecycleCache = new HashMap<>();

    public RequestServerStatusHandler(String httpToken, ServiceRegistry serviceRegistry) {
        this.serviceProvider = serviceRegistry.firstProvider(CloudServiceProvider.class);
        this.httpToken = httpToken;

        System.out.println("Start StatusHandler");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::cacheServerInfo, 0, 3, TimeUnit.SECONDS);
    }

    private synchronized void cacheServerInfo() {
        this.serverLifecycleCache.clear();

        System.out.println("Run...");

        this.serviceProvider.runningServices().forEach(serviceInfoSnapshot -> {
            System.out.println(serviceInfoSnapshot.name());
            System.out.println(serviceInfoSnapshot.lifeCycle().name());
            this.serverLifecycleCache.put(serviceInfoSnapshot.name(), serviceInfoSnapshot.lifeCycle().name());
        });
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println("Getting new Request");

        if (!httpExchange.getRequestHeaders().get("Bearer").get(0).equals(httpToken)) {
            System.out.println("failed (Auth)");
            sendResponse(httpExchange, "Bad Request! Invalid token.", 403);
            return;
        }

        System.out.println("Token is valid!");

        if (!httpExchange.getRequestMethod().equals("POST")) {
            System.out.println("failed (Request)");
            sendResponse(httpExchange, "Bad Request! Invalid method!", 405);
            return;
        }

        System.out.println("Method is valid!");

        ServerStatusBody serverStatusBody = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody()), ServerStatusBody.class);
        System.out.println("Converted body");

        System.out.println("printing results into response");

        OutputStream responseBody = httpExchange.getResponseBody();

        System.out.println(serverStatusBody.task());
        System.out.println(serverStatusBody.server());
        System.out.println(this.serverLifecycleCache.get(serverStatusBody.server()));

        String json = new ServerStatusResponseBody(serverStatusBody.task(), serverStatusBody.server(), this.serverLifecycleCache.get(serverStatusBody.server())).toJson();

        System.out.println(json);

        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, json.length());
        responseBody.write(json.getBytes(StandardCharsets.UTF_8));
        responseBody.flush();
        responseBody.close();

        System.out.println("send response to client");
    }

    private void sendResponse(HttpExchange exchange, String msg, int code) throws IOException {
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes(StandardCharsets.UTF_8));
        exchange.sendResponseHeaders(code, msg.length());
        responseBody.flush();
        responseBody.close();
    }
}
