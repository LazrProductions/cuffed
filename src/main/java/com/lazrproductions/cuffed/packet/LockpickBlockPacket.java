package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class LockpickBlockPacket extends ParameterizedLazrPacket {
    
    int speedIncreasePerPick;
    int progressPerPick;

    int stopCode;
    int x;
    int y;
    int z;
    
    String lockpickerUUID;

    public LockpickBlockPacket(BlockPos pos, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        super(speedIncreasePerTick, progressPerPick, -1, pos.getX(), pos.getY(), pos.getZ(), lockpickerUUID);
        this.stopCode = -1;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
    }
    public LockpickBlockPacket(boolean wasFailed, BlockPos pos, String lockpickerUUID) {
        super(0, 0, wasFailed ? 0 : 2, pos.getX(), pos.getY(), pos.getZ(), lockpickerUUID);
        this.stopCode = wasFailed ? 0 : 2;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickBlockPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void loadValues(Object[] arg0) {
        //super(speedIncreasePerTick, progressPerPick, -1, pos.getX(), pos.getY(), pos.getZ(), lockpickerUUID);
        speedIncreasePerPick = (int)arg0[0];
        progressPerPick = (int)arg0[1];
        stopCode = (int)arg0[2];
        x = (int)arg0[3];
        y = (int)arg0[4];
        z = (int)arg0[5];
        lockpickerUUID = (String)arg0[6];
    }

    @Override
    public void handleClientside(Supplier<NetworkEvent.Context> ctx) {
        Clientside.handleClientside(ctx, speedIncreasePerPick, progressPerPick, stopCode, x, y, z, lockpickerUUID);
    }

    @Override
    public void handleServerside(Supplier<NetworkEvent.Context> ctx) {
        Serverside.handleServerside(ctx, speedIncreasePerPick, progressPerPick, stopCode, x, y, z, lockpickerUUID);
    }

    static class Clientside {
        public static void handleClientside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, int x, int y, int z, String lockpickerUUID) {
            if(stopCode<=-1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingCellDoor(instance, new BlockPos(x,y,z), speedIncreasePerPick, progressPerPick);
            }
        }
    }

    static class Serverside {
        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, int x, int y, int z, String lockpickerUUID) {
            if(stopCode>-1)
                CuffedAPI.Lockpicking.finishLockpickingCellDoor(stopCode == 0, new BlockPos(x,y,z), UUID.fromString(lockpickerUUID));
        }
    }
}
