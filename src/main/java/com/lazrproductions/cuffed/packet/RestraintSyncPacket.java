package com.lazrproductions.cuffed.packet;

import com.lazrproductions.cuffed.CuffedMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class RestraintSyncPacket implements CustomPacketPayload {
    public static final Type<RestraintSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "restraint_sync"));

    public static final StreamCodec<FriendlyByteBuf, RestraintSyncPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.playerId);
            buf.writeUtf(val.playerUUID);
            buf.writeNbt(val.data);
        },
        buf -> new RestraintSyncPacket(
            buf.readInt(),
            buf.readUtf(),
            buf.readNbt()
        )
    );

    public final int playerId;
    public final String playerUUID;
    public final CompoundTag data;

    public RestraintSyncPacket(int playerId, String uuid, CompoundTag data) {
        this.playerId = playerId;
        this.playerUUID = uuid;
        this.data = data;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
