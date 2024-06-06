package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.api.CuffedAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class LockpickLockPacket extends CreativePacket {
    public int speedIncreasePerPick;
    public int progressPerPick;

    public int stopCode;
    public int lockId = -1;
    
    public String lockpickerUUID = "";

    public LockpickLockPacket(int lockId, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.stopCode = -1;
        this.lockId = lockId;
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickLockPacket(boolean wasFailed, int lockId, String lockpickerUUID) {
        this.stopCode = wasFailed ? 0 : 2;
        this.lockId = lockId;
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickLockPacket() { }

    @Override
    public void executeClient(Player arg0) {
        if(stopCode<=-1) {
            Minecraft instance = Minecraft.getInstance();
            CuffedAPI.Lockpicking.beginLockpickingLock(instance, lockId, speedIncreasePerPick, progressPerPick);
        }
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        if(stopCode>-1)
            CuffedAPI.Lockpicking.finishLockpickingLock(stopCode == 0, lockId, UUID.fromString(lockpickerUUID));
    }
}
