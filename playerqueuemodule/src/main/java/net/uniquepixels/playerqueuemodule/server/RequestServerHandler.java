package net.uniquepixels.playerqueuemodule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.uniquepixels.playerqueuemodule.server.httpbody.ServerRequestBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class RequestServerHandler implements HttpHandler {
    private final ServiceTaskProvider taskProvider;
    private final String token;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        if (!httpExchange.getRequestHeaders().get("Bearer").get(0).equals(token)) {
            System.out.println("failed (Auth)");
            sendResponse(httpExchange, "Bad Request! Invalid token.", 403);
            return;
        }

        if (!httpExchange.getRequestMethod().equals("POST")) {
            System.out.println("failed (Request)");
            sendResponse(httpExchange, "Bad Request! Invalid method!", 405);
            return;
        }

        val content = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody()), ServerRequestBody.class);


        val serviceTask = this.taskProvider.serviceTask(content.task());
        assert serviceTask != null;
        val cloudService = ServiceConfiguration.builder(serviceTask)
                .autoDeleteOnStop(true)
                .build().createNewService();

        cloudService.serviceInfo().provider().startAsync();

        val data = new ServerData(cloudService.serviceInfo().name(), serviceTask.name());

        val responseBody = httpExchange.getResponseBody();
        val json = new Gson().toJson(data);
        httpExchange.sendResponseHeaders(200, json.length());
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        responseBody.write(json.getBytes(StandardCharsets.UTF_8));
        responseBody.flush();
        responseBody.close();

    }

    @SneakyThrows
    private void sendResponse(HttpExchange exchange, String msg, int code) {
        val responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes(StandardCharsets.UTF_8));
        exchange.sendResponseHeaders(code, msg.length());
        responseBody.flush();
        responseBody.close();
    }
}
