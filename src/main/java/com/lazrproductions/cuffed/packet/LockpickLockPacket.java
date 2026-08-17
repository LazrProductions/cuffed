package com.lazrproductions.cuffed.packet;

import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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
        private static final double MAX_LOCKPICK_DISTANCE = 6.0;

        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int speedIncreasePerPick, int progressPerPick, int stopCode, int lockId, String lockpickerUUID) {
            if(stopCode > -1) {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;

                if (!sender.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.LOCKPICK.get())) {
                    return;
                }

                Entity entity = sender.level().getEntity(lockId);
                if (!(entity instanceof PadlockEntity padlock)) {
                    return;
                }

                double distance = sender.position().distanceTo(padlock.position());
                if (distance > MAX_LOCKPICK_DISTANCE) {
                    return;
                }

                if (!padlock.isLocked()) {
                    return;
                }

                CuffedAPI.Lockpicking.finishLockpickingLock(stopCode == 0, lockId, sender.getUUID());
            }
        }
    }
}
