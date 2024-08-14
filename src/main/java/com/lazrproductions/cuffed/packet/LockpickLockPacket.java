package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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
    public void handleClientside(@Nonnull Player arg0) {
        if(stopCode<=-1) {
            Minecraft instance = Minecraft.getInstance();
            CuffedAPI.Lockpicking.beginLockpickingLock(instance, lockId, speedIncreasePerPick, progressPerPick);
        }
    }
    @Override
    public void handleServerside(@Nonnull ServerPlayer arg0) {
        if(stopCode>-1)
            CuffedAPI.Lockpicking.finishLockpickingLock(stopCode == 0, lockId, UUID.fromString(lockpickerUUID));
    }
}
