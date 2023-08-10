package net.uniquepixels.playerqueuemodule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class RequestServerHandler implements HttpHandler {
    private final ServiceTaskProvider taskProvider;
    private final CloudServiceFactory serviceFactory;
    private final String token;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println("new request incomming...");

        if (!httpExchange.getRequestHeaders().get("Bearer").get(0).equals(token)) {
            sendResponse(httpExchange, "Bad Request! Invalid token.", 403);
            return;
        }

        if (!httpExchange.getRequestMethod().equals("POST")) {
            sendResponse(httpExchange, "Bad Request! Invalid method!", 405);
            return;
        }

        sendResponse(httpExchange, "OK", 200);

        val content = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody()), HttpBodyContent.class);

        val serviceTask = this.taskProvider.serviceTask(content.getTask());
        assert serviceTask != null;
        val cloudService = serviceFactory.createCloudService(ServiceConfiguration.builder(serviceTask).build());

        val data = new ServerData(cloudService.serviceInfo().name(), serviceTask.name());

        val responseBody = httpExchange.getResponseBody();
        val json = new Gson().toJson(data);
        responseBody.write(json.getBytes(StandardCharsets.UTF_8));
        responseBody.flush();
        responseBody.close();
        httpExchange.sendResponseHeaders(json.length(), 200);

    }

    @SneakyThrows
    private void sendResponse(HttpExchange exchange, String msg, int code) {
        val responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes(StandardCharsets.UTF_8));
        responseBody.flush();
        responseBody.close();
        exchange.sendResponseHeaders(code, msg.length());
    }
}
