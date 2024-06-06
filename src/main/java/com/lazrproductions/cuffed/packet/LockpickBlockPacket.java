package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.api.CuffedAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class LockpickBlockPacket extends CreativePacket{
    
    public int speedIncreasePerPick;
    public int progressPerPick;

    public int stopCode;
    public int x;
    public int y;
    public int z;
    
    public String lockpickerUUID = "";

    public LockpickBlockPacket(BlockPos pos, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        this.stopCode = -1;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
    }
    public LockpickBlockPacket(boolean wasFailed, BlockPos pos, String lockpickerUUID) {
        this.stopCode = wasFailed ? 0 : 2;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickBlockPacket() {}

    @Override
    public void executeClient(Player arg0) {
        if(stopCode<=-1) {
            Minecraft instance = Minecraft.getInstance();
            CuffedAPI.Lockpicking.beginLockpickingCellDoor(instance, new BlockPos(x,y,z), speedIncreasePerPick, progressPerPick);
        }
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        if(stopCode>-1)
            CuffedAPI.Lockpicking.finishLockpickingCellDoor(stopCode == 0, new BlockPos(x,y,z), UUID.fromString(lockpickerUUID));
    }
}
