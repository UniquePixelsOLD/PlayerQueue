package net.uniquepixels.playerqueue.queue;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTask;
import net.kyori.adventure.bossbar.BossBar;

import java.util.Map;

public interface QueueReference {

    ServiceInfoSnapshot snapShot();
    ServiceTask task();
    Map<String, BossBar> languageBossBars();

    void sendBossBarsToPlayer();

}
