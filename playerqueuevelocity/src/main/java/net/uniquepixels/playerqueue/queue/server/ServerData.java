package net.uniquepixels.playerqueue.queue.server;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class ServerData {

    private final String serverName;
    private final ServerTask task;

    public void connectPlayerToServer(Player player) {

    }

    public ServerStatus getServerStatus() {

        return ServerStatus.STOPPED;
    }

}


