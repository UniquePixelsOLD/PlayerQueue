package net.uniquepixels.playerqueuepaper.npc;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.uniquepixels.coreapi.database.MongoDatabase;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RequiredArgsConstructor
public class NPCManager implements Listener {

    private static final Map<Integer, NPCConfiguration> NPC_CONFIGURATION_MAP = new HashMap<Integer, NPCConfiguration>();
    private final MongoDatabase database;
    private final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
    private final Plugin plugin;
    private final NPCDataType npcDataType;

    public static Map<Integer, NPCConfiguration> npcConfigurationMap() {
        return NPC_CONFIGURATION_MAP;
    }

    public MongoCursor<ObjectId> getAllConfigurationIds() {
        val collection = this.database.collection("minecraft_cloud_playerqueue_npc", NPCConfiguration.class);
        return collection.find().map(NPCConfiguration::_id).iterator();
    }

    public @Nullable NPCConfiguration getConfiguration(@NotNull ObjectId id) {

        val collection = this.database.collection("minecraft_cloud_playerqueue_npc", NPCConfiguration.class);
        val configuration = collection.find(Filters.eq("_id", id)).first();

        if (configuration == null)
            return null;

        return configuration;
    }

    public void saveConfiguration(NPCConfiguration configuration) {
        val collection = this.database.collection("minecraft_cloud_playerqueue_npc", NPCConfiguration.class);
        collection.insertOne(configuration);
    }

    @SneakyThrows
    public void spawnNPC(Player player, NPCConfiguration configuration) {
        val worldServer = ((CraftWorld) configuration.getLocation().getWorld()).getHandle();
        val gameProfile = new GameProfile(UUID.randomUUID(), configuration.displayName());

        CraftPlayer craftPlayer = (CraftPlayer) player;

        val profile = craftPlayer.getProfile();

        val textures = profile.getProperties().get("textures").iterator();

        val property = textures.next();

        val texture = property.getValue();
        val signature = property.getSignature();

        val properties = gameProfile.getProperties().get("properties");
        properties.clear();
        properties.add(new Property("textures", texture, signature));

        val playerConnection = ((CraftPlayer) player).getHandle().connection;

        ServerPlayer serverPlayer = new ServerPlayer(this.server, worldServer, gameProfile);

        val location = configuration.getLocation();
        serverPlayer.setXRot(location.getPitch());
        serverPlayer.setYHeadRot(location.getYaw());
        serverPlayer.setYBodyRot(location.getYaw());
        serverPlayer.setPos(location.x(), location.y(), location.z());

        playerConnection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer));
        playerConnection.send(new ClientboundAddPlayerPacket(serverPlayer));
        playerConnection.send(new ClientboundTeleportEntityPacket(serverPlayer));

        NPC_CONFIGURATION_MAP.put(serverPlayer.getId(), configuration);
    }

    @SneakyThrows
    private void sendPluginMessage(List<Player> player, NPCConfiguration configuration) {

        val dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF("player:add");
        dataOutput.writeUTF(configuration.destinationTask());
        dataOutput.writeUTF(player.stream().map(player1 -> player1.getUniqueId().toString()).toList().toString());

        player.get(0).sendPluginMessage(this.plugin, "minecraft:gamequeue", dataOutput.toByteArray());
    }

    @EventHandler
    public void onUnknownEntity(PlayerUseUnknownEntityEvent event) {

        if (!NPC_CONFIGURATION_MAP.containsKey(event.getEntityId()))
            return;

        val configuration = NPC_CONFIGURATION_MAP.get(event.getEntityId());

        sendPluginMessage(List.of(event.getPlayer()), configuration);

    }

}
