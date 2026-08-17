package com.lazrproductions.cuffed.packet;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class RestraintEquippedPacket implements CustomPacketPayload {
    public static final Type<RestraintEquippedPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "restraint_equipped"));

    public static final StreamCodec<FriendlyByteBuf, RestraintEquippedPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.playerId);
            buf.writeUtf(val.playerUUID);
            buf.writeInt(val.type);
            buf.writeNbt(val.oldData);
            buf.writeNbt(val.newData);
            buf.writeUtf(val.captorUUID);
        },
        buf -> new RestraintEquippedPacket(
            buf.readInt(),
            buf.readUtf(),
            buf.readInt(),
            buf.readNbt(),
            buf.readNbt(),
            buf.readUtf()
        )
    );

    public final int playerId;
    public final String playerUUID;
    public final int type;
    public final CompoundTag oldData;
    public final CompoundTag newData;
    public final String captorUUID;

    public RestraintEquippedPacket(int playerId, String playerUUID, RestraintType type, @Nullable CompoundTag oldData, @Nullable CompoundTag newData, String captorUUID) {
        this.playerId = playerId;
        this.playerUUID = playerUUID;
        this.type = type.toInteger();
        this.oldData = oldData;
        this.newData = newData;
        this.captorUUID = captorUUID != null ? captorUUID : "null";
    }

    public RestraintEquippedPacket(int playerId, String playerUUID, int type, @Nullable CompoundTag oldData, @Nullable CompoundTag newData, String captorUUID) {
        this.playerId = playerId;
        this.playerUUID = playerUUID;
        this.type = type;
        this.oldData = oldData;
        this.newData = newData;
        this.captorUUID = captorUUID != null ? captorUUID : "null";
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
