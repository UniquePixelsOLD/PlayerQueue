package net.uniquepixels.playerqueue.queue;

import net.uniquepixels.playerqueue.queue.server.ServerTask;

import java.util.UUID;

public class QueuePlayer {

    private final String uuid;
    private final String queueId;
    private final String serverTask;

    public QueuePlayer(String uuid, String queueId, String serverTask) {
        this.uuid = uuid;
        this.queueId = queueId;
        this.serverTask = serverTask;
    }

    public ServerTask serverTask() {
        return new ServerTask(serverTask);
    }

    public UUID playerUUID() {
        return UUID.fromString(uuid);
    }

    public UUID queueId() {
        return UUID.fromString(queueId);
    }
}
