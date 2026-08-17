package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class LockpickLockPacket implements CustomPacketPayload {
    public static final Type<LockpickLockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "lockpick_lock"));

    public static final StreamCodec<FriendlyByteBuf, LockpickLockPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.speedIncreasePerPick);
            buf.writeInt(val.progressPerPick);
            buf.writeInt(val.stopCode);
            buf.writeInt(val.lockId);
            buf.writeUtf(val.lockpickerUUID);
        },
        buf -> new LockpickLockPacket(
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readUtf()
        )
    );

    public final int speedIncreasePerPick;
    public final int progressPerPick;
    public final int stopCode;
    public final int lockId;
    public final String lockpickerUUID;

    public LockpickLockPacket(int lockId, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
        this.stopCode = -1;
        this.lockId = lockId;
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickLockPacket(boolean wasFailed, int lockId, String lockpickerUUID) {
        this.speedIncreasePerPick = 0;
        this.progressPerPick = 0;
        this.stopCode = wasFailed ? 0 : 2;
        this.lockId = lockId;
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickLockPacket(int speedIncreasePerPick, int progressPerPick, int stopCode, int lockId, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerPick;
        this.progressPerPick = progressPerPick;
        this.stopCode = stopCode;
        this.lockId = lockId;
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
                CuffedAPI.Lockpicking.finishLockpickingLock(stopCode == 0, lockId, UUID.fromString(lockpickerUUID));
            }
        });
    }
}
