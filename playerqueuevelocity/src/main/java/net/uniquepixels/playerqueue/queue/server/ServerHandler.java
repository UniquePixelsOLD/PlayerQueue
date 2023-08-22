package net.uniquepixels.playerqueue.queue.server;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.uniquepixels.playerqueue.DatabaseHandler;
import net.uniquepixels.playerqueue.queue.server.httpbody.GotServerStatus;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestNewCloudServer;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestServerStatus;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ServerHandler {

    private final HttpClient client = HttpClient.newHttpClient();
    private final DatabaseHandler databaseHandler;
    private final Gson gson = new Gson();

    @SneakyThrows
    public CompletableFuture<ServerData> requestNewServer(RequestNewCloudServer body) {

        val builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(body), StandardCharsets.UTF_8))
                .uri(new URI("http://127.0.0.1:778/cloud/request-server"))
                .header("Bearer", databaseHandler.getHttpToken());

        var future = new CompletableFuture<ServerData>();

        this.client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofInputStream()).thenAccept(inputStreamHttpResponse -> {

            if (inputStreamHttpResponse.statusCode() != 200) {
                future.complete(null);
                return;
            }

            future.complete(new Gson().fromJson(new InputStreamReader(inputStreamHttpResponse.body()), ServerData.class));

        });

        return future;

    }

    @SneakyThrows
    public CompletableFuture<ServerStatus> getStatusFromServer(RequestServerStatus body) {
        val builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(body), StandardCharsets.UTF_8))
                .uri(new URI("http://127.0.0.1:778/cloud/request-server-status"))
                .header("Bearer", databaseHandler.getHttpToken());

        var future = new CompletableFuture<ServerStatus>();

        this.client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofInputStream()).thenAccept(inputStreamHttpResponse -> {

            if (inputStreamHttpResponse.statusCode() != 200) {
                future.complete(null);
                return;
            }

            val gotServerStatus = new Gson().fromJson(new InputStreamReader(inputStreamHttpResponse.body()), GotServerStatus.class);
            future.complete(ServerStatus.valueOf(gotServerStatus.getStatus().toUpperCase()));

        });

        return future;
    }

}
