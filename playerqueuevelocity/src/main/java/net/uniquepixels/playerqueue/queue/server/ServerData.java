package net.uniquepixels.playerqueue.queue.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.uniquepixels.playerqueue.queue.server.httpbody.RequestServerStatus;

import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class ServerData {

    private final String serverName;
    private final ServerTask task;

    public CompletableFuture<ServerStatus> getServerStatus(ServerHandler handler) {
        return handler.getStatusFromServer(new RequestServerStatus(serverName, task.getTaskName()));
    }

}


