package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class LockpickLockPacket extends ParameterizedLazrPacket {
    int speedIncreasePerPick;
    int progressPerPick;

    int stopCode;
    int lockId;
    
    String lockpickerUUID;


    public LockpickLockPacket(int lockId, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        super(speedIncreasePerTick, progressPerPick, -1, lockId, lockpickerUUID);
        
        this.stopCode = -1;
        this.lockId = lockId;
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
        this.lockpickerUUID = lockpickerUUID;
        
    }
    public LockpickLockPacket(boolean wasFailed, int lockId, String lockpickerUUID) {
        super(0, 0, wasFailed ? 0 : 2, lockId, lockpickerUUID);
        this.stopCode = wasFailed ? 0 : 2;
        this.lockId = lockId;
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickLockPacket(FriendlyByteBuf buf) {
        super(buf);
    }



    @Override
    public void loadValues(Object[] arg0) {
        speedIncreasePerPick = (int)arg0[0];
        progressPerPick = (int)arg0[1];

        stopCode = (int)arg0[2];
        lockId = (int)arg0[3];

        lockpickerUUID = (String)arg0[4];
    }

    @Override
    public void handleClientside(Supplier<NetworkEvent.Context> ctx) {
        Clientside.handleClientside(ctx, speedIncreasePerPick, progressPerPick, stopCode, lockId, lockpickerUUID);
    }
    @Override
    public void handleServerside(Supplier<NetworkEvent.Context> ctx) {
        Serverside.handleServerside(ctx, speedIncreasePerPick, progressPerPick, stopCode, lockId, lockpickerUUID);
    }

    static class Clientside {
        public static void handleClientside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, int lockId, String lockpickerUUID) {
            if(stopCode<=-1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingLock(instance, lockId, speedIncreasePerPick, progressPerPick);
            }
        }
    }

    static class Serverside {
        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, int lockId, String lockpickerUUID) {
            if(stopCode>-1)
                CuffedAPI.Lockpicking.finishLockpickingLock(stopCode == 0, lockId, UUID.fromString(lockpickerUUID));
        }
    }
}
