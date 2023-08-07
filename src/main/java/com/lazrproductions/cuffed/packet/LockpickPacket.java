package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.client.CuffedEventClient;
import com.lazrproductions.cuffed.server.CuffedEventServer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class LockpickPacket extends CreativePacket {

    public int pickPhases = 3;
    public int selectedSlot = -1;
    public int lockpickTick = -1;

    public int lockId = -1;

    public int stopCode = -1;
    public int playerId = -1;
    public String playerUUID = "";

    public LockpickPacket(int tick, int id, int slot, int phases, int playerId) {
        this.pickPhases = phases;
        this.selectedSlot = slot;
        this.lockpickTick = tick;
        this.lockId = id;
        this.playerId = playerId;
    }

    public LockpickPacket(int code, int id, int playerId, String playerUUID) {
        this.lockId = id;
        this.stopCode = code;
        this.playerId = playerId;
        this.playerUUID = playerUUID;
    }

    public LockpickPacket() {
    }

    @Override
    public void executeClient(Player arg0) {
        if (stopCode <= -1) {
            if(arg0.getId() == playerId) {
                CuffedEventClient.maxPhases = pickPhases;
                CuffedEventClient.pickingLock = lockId;
                CuffedEventClient.lockpickTick = lockpickTick;
                CuffedEventClient.pickingSlot = selectedSlot;
                if (lockpickTick > -1)
                    CuffedEventClient.isLockpicking = true;
                else
                    CuffedEventClient.isLockpicking = false;
            }
        }
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        if(stopCode>-1)
            CuffedEventServer.FinishLockpicking(stopCode, lockId, playerId, UUID.fromString(playerUUID));
    }
}
