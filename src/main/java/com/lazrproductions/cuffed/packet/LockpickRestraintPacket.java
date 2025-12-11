package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

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
        private static final double MAX_LOCKPICK_DISTANCE = 4.0;

        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, String restrainedUUID, int restraintType, String lockpickerUUID) {
            if(stopCode > -1) {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;

                if (!sender.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.LOCKPICK.get())) {
                    return;
                }

                UUID targetUUID;
                try {
                    targetUUID = UUID.fromString(restrainedUUID);
                } catch (IllegalArgumentException e) {
                    return;
                }

                ServerPlayer restrainedPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(targetUUID);
                if (restrainedPlayer == null) {
                    return;
                }

                double distance = sender.position().distanceTo(restrainedPlayer.position());
                if (distance > MAX_LOCKPICK_DISTANCE) {
                    return;
                }

                RestraintType type = RestraintType.fromInteger(restraintType);
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(restrainedPlayer);
                AbstractRestraint restraint = cap.getRestraint(type);
                if (restraint == null || !restraint.getLockpickable()) {
                    return;
                }

                CuffedAPI.Lockpicking.finishLockpickingRestraint(stopCode == 0, type, targetUUID, sender.getUUID());
            }
        }
    }
}
