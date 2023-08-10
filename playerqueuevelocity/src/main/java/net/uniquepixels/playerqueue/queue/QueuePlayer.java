package net.uniquepixels.playerqueue.queue;

import java.util.UUID;

public class QueuePlayer {

    private String uuid;
    private String queueId;

    public QueuePlayer(String uuid, String queueId) {
        this.uuid = uuid;
        this.queueId = queueId;
    }

    public UUID playerUUID() {
        return UUID.fromString(uuid);
    }

    public UUID queueId() {
        return UUID.fromString(queueId);
    }
}
