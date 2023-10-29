package net.uniquepixels.playerqueuemodule.server.httpbody;

import com.google.gson.Gson;

public record ServerStatusResponseBody(String task, String server, String lifecycle) {

    public String toJson() {
        return new Gson().toJson(this);
    }

}
