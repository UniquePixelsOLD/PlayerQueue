package net.uniquepixels.playerqueue.queue;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class QueueBar {

    private final BossBar bossBar;

    public QueueBar() {
        this.bossBar = BossBar.bossBar(Component.translatable("queuebar.empty"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    public void updateText(Component barText) {
        this.bossBar.name(barText);
    }

    public void addPlayer(Player player) {
        this.bossBar.addViewer(player);
    }

    public void removePlayer(Player player) {
        this.bossBar.removeViewer(player);
    }
}
