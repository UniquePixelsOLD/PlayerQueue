package net.uniquepixels.playerqueue.queue.server;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.val;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ServerHandler {

    private final HttpClient client;

    public ServerHandler() {
        this.client = HttpClient.newHttpClient();
    }

    @SneakyThrows
    public CompletableFuture<ServerData> requestNewServer(String task) {

        val builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"task\": \"" + task + "\"}", StandardCharsets.UTF_8))
                .uri(new URI("http://127.0.0.1:778/server/request?task=" + task))
                .header("Bearer", "defaulttoken");

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

}
