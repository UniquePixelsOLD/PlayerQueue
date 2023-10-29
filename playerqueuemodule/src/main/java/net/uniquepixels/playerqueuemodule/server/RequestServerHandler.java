package net.uniquepixels.playerqueuemodule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceCreateResult;
import eu.cloudnetservice.driver.service.ServiceTask;
import net.uniquepixels.playerqueuemodule.server.httpbody.ServerRequestBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RequestServerHandler implements HttpHandler {
    private final ServiceTaskProvider taskProvider;
    private final String token;

    public RequestServerHandler(ServiceTaskProvider taskProvider, String token) {
        this.taskProvider = taskProvider;
        this.token = token;
    }

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

        ServerRequestBody content = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody()), ServerRequestBody.class);


        ServiceTask serviceTask = this.taskProvider.serviceTask(content.task());
        assert serviceTask != null;
        ServiceCreateResult cloudService = ServiceConfiguration.builder(serviceTask)
                .autoDeleteOnStop(true)
                .build().createNewService();

        cloudService.serviceInfo().provider().startAsync();

        ServerData data = new ServerData(cloudService.serviceInfo().name(), serviceTask.name());

        OutputStream responseBody = httpExchange.getResponseBody();
        String json = new Gson().toJson(data);
        httpExchange.sendResponseHeaders(200, json.length());
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        responseBody.write(json.getBytes(StandardCharsets.UTF_8));
        responseBody.flush();
        responseBody.close();

    }

    private void sendResponse(HttpExchange exchange, String msg, int code) throws IOException {
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(msg.getBytes(StandardCharsets.UTF_8));
        exchange.sendResponseHeaders(code, msg.length());
        responseBody.flush();
        responseBody.close();
    }
}
