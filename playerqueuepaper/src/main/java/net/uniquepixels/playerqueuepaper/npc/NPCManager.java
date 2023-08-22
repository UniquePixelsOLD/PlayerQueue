package net.uniquepixels.playerqueuepaper.npc;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.uniquepixels.core.paper.paper.TextureFetcher;
import net.uniquepixels.coreapi.database.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class NPCManager {

    private final MongoDatabase database;
    private final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();

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
        val worldServer = ((CraftWorld) configuration.getWorld()).getHandle();
        val gameProfile = new GameProfile(UUID.randomUUID(), configuration.displayName());

        val skinUrl = TextureFetcher.getSkinUrl(configuration.fromTexture());
        val response = TextureFetcher.getResponse(skinUrl);

        for (JsonElement properties : response.getAsJsonArray("properties")) {
            val jsonObject = properties.getAsJsonObject();
            if (!jsonObject.get("name").getAsString().equals("textures"))
                continue;

            gameProfile.getProperties().put("textures", new Property("textures", jsonObject.get("value").getAsString()));
        }

        val playerConnection = ((CraftPlayer) player).getHandle().connection;

        ServerPlayer serverPlayer = new ServerPlayer(this.server, worldServer, gameProfile);

        playerConnection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer));
        playerConnection.send(new ClientboundAddPlayerPacket(serverPlayer));
    }

}
