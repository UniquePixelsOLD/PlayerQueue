package net.uniquepixels.playerqueue.queue.server;

import com.google.gson.Gson;
import net.uniquepixels.playerqueue.DatabaseHandler;
import net.uniquepixels.playerqueue.queue.server.httpbody.GotServerStatus;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestNewCloudServer;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestServerStatus;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


public class ServerHandler {
    private final HttpClient client = HttpClient.newHttpClient();
    private final DatabaseHandler databaseHandler;
    private final Gson gson = new Gson();
    public ServerHandler(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public CompletableFuture<ServerData> requestNewServer(RequestNewCloudServer body) {

        HttpRequest.Builder builder = null;
        try {
            builder = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(body), StandardCharsets.UTF_8))
                    .uri(new URI("http://localhost:700/cloud/request-server"))
                    .header("Bearer", databaseHandler.getHttpToken());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        var future = new CompletableFuture<ServerData>();

        this.client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofInputStream()).thenAccept(inputStreamHttpResponse -> {

            if (inputStreamHttpResponse.statusCode() != 200) {
                System.out.println("(Request Server) Wrong HTTP Request: " + inputStreamHttpResponse.statusCode());
                future.cancel(true);
                return;
            }

            future.complete(new Gson().fromJson(new InputStreamReader(inputStreamHttpResponse.body()), ServerData.class));

        });

        return future;

    }


    public CompletableFuture<ServerStatus> getStatusFromServer(RequestServerStatus body) {
        HttpRequest.Builder builder = null;
        try {
            builder = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(body), StandardCharsets.UTF_8))
                    .uri(new URI("http://localhost:700/cloud/request-server-status"))
                    .header("Bearer", databaseHandler.getHttpToken());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        var future = new CompletableFuture<ServerStatus>();

        System.out.println("Sending http request...");

        this.client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofInputStream()).thenAccept(inputStreamHttpResponse -> {


            if (inputStreamHttpResponse.statusCode() != 200) {
                System.out.println("(Request Status) Wrong HTTP Request: " + inputStreamHttpResponse.statusCode());
                future.cancel(true);
                return;
            }

            System.out.println("HTTP OK!");

            GotServerStatus gotServerStatus = new Gson().fromJson(new InputStreamReader(inputStreamHttpResponse.body()), GotServerStatus.class);
            future.complete(ServerStatus.valueOf(gotServerStatus.getStatus().toUpperCase()));

        });

        return future;
    }

}
