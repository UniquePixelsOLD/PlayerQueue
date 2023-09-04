package net.uniquepixels.playerqueuepaper.npc;

import org.bson.types.ObjectId;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class NPCDataType implements PersistentDataType<byte[], ObjectId> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ObjectId> getComplexType() {
        return ObjectId.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ObjectId complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toByteArray();
    }

    @Override
    public @NotNull ObjectId fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        return new ObjectId(primitive);
    }
}
