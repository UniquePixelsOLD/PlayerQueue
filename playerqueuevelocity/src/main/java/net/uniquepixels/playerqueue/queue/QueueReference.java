package net.uniquepixels.playerqueue.queue;

import net.uniquepixels.playerqueue.queue.server.ServerData;

import java.util.Map;

public interface QueueReference {

    ServerData data();

    Map<String, QueueBar> languageBossBars();

}
