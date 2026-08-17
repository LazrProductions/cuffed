package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class LockpickBlockPacket implements CustomPacketPayload {
    public static final Type<LockpickBlockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "lockpick_block"));

    public static final StreamCodec<FriendlyByteBuf, LockpickBlockPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.speedIncreasePerPick);
            buf.writeInt(val.progressPerPick);
            buf.writeInt(val.stopCode);
            buf.writeInt(val.x);
            buf.writeInt(val.y);
            buf.writeInt(val.z);
            buf.writeUtf(val.lockpickerUUID);
        },
        buf -> new LockpickBlockPacket(
            buf.readInt(),
            buf.readInt(),
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
    public final int x;
    public final int y;
    public final int z;
    public final String lockpickerUUID;

    public LockpickBlockPacket(BlockPos pos, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
        this.stopCode = -1;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickBlockPacket(boolean wasFailed, BlockPos pos, String lockpickerUUID) {
        this.speedIncreasePerPick = 0;
        this.progressPerPick = 0;
        this.stopCode = wasFailed ? 0 : 2;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.lockpickerUUID = lockpickerUUID != null ? lockpickerUUID : "null";
    }

    public LockpickBlockPacket(int speedIncreasePerPick, int progressPerPick, int stopCode, int x, int y, int z, String lockpickerUUID) {
        this.speedIncreasePerPick = speedIncreasePerPick;
        this.progressPerPick = progressPerPick;
        this.stopCode = stopCode;
        this.x = x;
        this.y = y;
        this.z = z;
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
                CuffedAPI.Lockpicking.finishLockpickingCellDoor(stopCode == 0, new BlockPos(x, y, z), UUID.fromString(lockpickerUUID));
            }
        });
    }
}
