package net.uniquepixels.playerqueue.queue;

import net.kyori.adventure.bossbar.BossBar;
import net.uniquepixels.playerqueue.queue.server.ServerData;
import net.uniquepixels.playerqueue.queue.server.ServerTask;

import java.util.Map;

public interface QueueReference {

    ServerData data();

    Map<String, BossBar> languageBossBars();

    void sendBossBarsToPlayer();

}
