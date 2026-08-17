package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class LockpickRestraintPacket implements CustomPacketPayload {
    public static final Type<LockpickRestraintPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "lockpick_restraint"));

    public static final StreamCodec<FriendlyByteBuf, LockpickRestraintPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.speedIncreasePerPick);
            buf.writeInt(val.progressPerPick);
            buf.writeInt(val.stopCode);
            buf.writeUtf(val.restrainedUUID);
            buf.writeInt(val.restraintType);
            buf.writeUtf(val.lockpickerUUID);
        },
        buf -> new LockpickRestraintPacket(
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readUtf(),
            buf.readInt(),
            buf.readUtf()
        )
    );

    public final int speedIncreasePerPick;
    public final int progressPerPick;
    public final int stopCode;
    public final String restrainedUUID;
    public final int restraintType;
    public final String lockpickerUUID;

    public LockpickRestraintPacket(String restrainedUUID, int restraintType, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
        this.stopCode = -1;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickRestraintPacket(boolean wasFailed, String restrainedUUID, int restraintType, String lockpickerUUID) {
        this.speedIncreasePerPick = 0;
        this.progressPerPick = 0;
        this.stopCode = wasFailed ? 0 : 2;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickRestraintPacket(int speedIncreasePerPick, int progressPerPick, int stopCode, String restrainedUUID, int restraintType, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerPick;
        this.progressPerPick = progressPerPick;
        this.stopCode = stopCode;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleServer(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer && stopCode > -1) {
                CuffedAPI.Lockpicking.finishLockpickingRestraint(stopCode == 0, RestraintType.fromInteger(restraintType), UUID.fromString(restrainedUUID), UUID.fromString(lockpickerUUID));
            }
        });
    }
}
