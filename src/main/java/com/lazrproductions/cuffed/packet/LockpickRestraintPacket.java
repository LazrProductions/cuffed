package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class LockpickRestraintPacket extends ParameterizedLazrPacket {
    int speedIncreasePerPick;
    int progressPerPick;

    int stopCode;
    String restrainedUUID;
    int restraintType;
    
    String lockpickerUUID;


    public LockpickRestraintPacket(String restrainedUUID, int restraintType, int speedIncreasePerTick, int progressPerPick, String lockpickerUUID) {
        super(speedIncreasePerTick, progressPerPick, -1, restrainedUUID, restraintType, lockpickerUUID);

        this.stopCode = -1;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.speedIncreasePerPick = speedIncreasePerTick;
        this.progressPerPick = progressPerPick;
    }    
    public LockpickRestraintPacket(boolean wasFailed, String restrainedUUID, int restraintType, String lockpickerUUID) {
        super(0, 0, wasFailed ? 0 : 2, restrainedUUID, restraintType, lockpickerUUID);

        this.stopCode = wasFailed ? 0 : 2;
        this.restrainedUUID = restrainedUUID;
        this.restraintType = restraintType;
        this.lockpickerUUID = lockpickerUUID;
    }
    public LockpickRestraintPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void loadValues(Object[] arg0) {
        speedIncreasePerPick = (int)arg0[0];
        progressPerPick = (int)arg0[1];
        
        stopCode = (int)arg0[2];
        restrainedUUID = (String)arg0[3];
        restraintType = (int)arg0[4];

        lockpickerUUID = (String)arg0[5];
    }

    @Override
    public void handleClientside(Supplier<NetworkEvent.Context> ctx) {
        Clientside.handleClientside(ctx, speedIncreasePerPick, progressPerPick, stopCode, restrainedUUID, restraintType, lockpickerUUID);
    }

    @Override
    public void handleServerside(Supplier<NetworkEvent.Context> ctx) {
        Serverside.handleServerside(ctx, speedIncreasePerPick, progressPerPick, stopCode, restrainedUUID, restraintType, lockpickerUUID);
    }

    static class Clientside {
        public static void handleClientside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, String restrainedUUID, int restraintType, String lockpickerUUID) {
            if(stopCode<=-1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingRestraint(instance, restrainedUUID, restraintType, speedIncreasePerPick, progressPerPick);
            }
        }
    }

    static class Serverside {
        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, String restrainedUUID, int restraintType, String lockpickerUUID) {
            if(stopCode>-1)
                CuffedAPI.Lockpicking.finishLockpickingRestraint(stopCode == 0, RestraintType.fromInteger(restraintType), 
                    UUID.fromString(restrainedUUID), UUID.fromString(lockpickerUUID));
        }
    }
}
