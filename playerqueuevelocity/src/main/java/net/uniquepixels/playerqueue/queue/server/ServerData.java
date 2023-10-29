package net.uniquepixels.playerqueue.queue.server;

import net.uniquepixels.playerqueue.queue.server.httpbody.RequestServerStatus;

import java.util.concurrent.CompletableFuture;

public class ServerData {

    private final String serverName;
    private final ServerTask task;

    public ServerData(String serverName, ServerTask task) {
        this.serverName = serverName;
        this.task = task;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerTask getTask() {
        return task;
    }

    public CompletableFuture<ServerStatus> getServerStatus(ServerHandler handler) {
        return handler.getStatusFromServer(new RequestServerStatus(serverName, task.getTaskName()));
    }

}


