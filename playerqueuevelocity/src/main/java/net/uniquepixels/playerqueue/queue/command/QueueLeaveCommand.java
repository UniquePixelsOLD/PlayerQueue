package net.uniquepixels.playerqueue.queue.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.uniquepixels.playerqueue.queue.QueueController;

import java.util.List;

public class QueueLeaveCommand {

    public BrigadierCommand createCommand(final ProxyServer server, QueueController controller) {

        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("leave")
                .executes(context -> {

                    val source = context.getSource();

                    val optionalUUID = source.pointers().get(Identity.UUID);

                    if (optionalUUID.isEmpty()) {

                        source.sendMessage(Component.text("Etwas ist beim verifizieren deiner Identität fehlgeschlagen!"));

                        return Command.SINGLE_SUCCESS;
                    }

                    val uuid = optionalUUID.get();
                    if (!controller.isPlayerInQueue(uuid)) {
                        source.sendMessage(Component.text("Du bist in keiner Queue"));
                        return Command.SINGLE_SUCCESS;
                    }

                    source.sendMessage(Component.text("Du hast die Queue verlassen"));
                    val serverTask = controller.findPlayer(uuid).serverTask();
                    controller.removePlayersFromQueue(serverTask, List.of(server.getPlayer(uuid).get()));


                    return Command.SINGLE_SUCCESS;
                }).build();

        return new BrigadierCommand(node);

    }

}
