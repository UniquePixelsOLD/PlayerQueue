package net.uniquepixels.playerqueuemodule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.uniquepixels.playerqueuemodule.server.httpbody.ServerStatusBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class RequestServerStatusHandler implements HttpHandler {

    private final ServiceTaskProvider taskProvider;
    private final String httpToken;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        if (!httpExchange.getRequestHeaders().get("Bearer").get(0).equals(httpToken)) {
            System.out.println("failed (Auth)");
            sendResponse(httpExchange, "Bad Request! Invalid token.", 403);
            return;
        }

        if (!httpExchange.getRequestMethod().equals("POST")) {
            System.out.println("failed (Request)");
            sendResponse(httpExchange, "Bad Request! Invalid method!", 405);
            return;
        }

        val serverStatusBody = new Gson().fromJson(new InputStreamReader(httpExchange.getRequestBody()), ServerStatusBody.class);



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
