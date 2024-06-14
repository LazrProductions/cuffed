package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class LockpickRestraintPacket extends CreativePacket {
    public int speedIncreasePerPick;
    public int progressPerPick;

    public int stopCode;
    public String restrainedUUID; // used only when picking a restrained player
    public int restraintType;
    
    public String lockpickerUUID = "";


    public LockpickRestraintPacket(String restrainedUUID, int restraintType, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.stopCode = -1;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
    }
    
    public LockpickRestraintPacket(boolean wasFailed, String restrainedUUID, int restraintType, String lockpickerUUID) {
        this.stopCode = wasFailed ? 0 : 2;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.lockpickerUUID = lockpickerUUID;
    }

    public LockpickRestraintPacket() { }

    @Override
    public void executeClient(Player arg0) {
        if(stopCode<=-1) {
            Minecraft instance = Minecraft.getInstance();
            CuffedAPI.Lockpicking.beginLockpickingRestraint(instance, restrainedUUID, restraintType, speedIncreasePerPick, progressPerPick);
        }
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        if(stopCode>-1)
            CuffedAPI.Lockpicking.finishLockpickingRestraint(stopCode == 0, RestraintType.fromInteger(restraintType), 
                UUID.fromString(restrainedUUID), UUID.fromString(lockpickerUUID));
    }
}
