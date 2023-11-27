package com.lazrproductions.cuffed.packet;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class CuffedBreakOutPacket extends CreativePacket {

    public int value;

    public CuffedBreakOutPacket(int value) {
        this.value = value;
    }

    public CuffedBreakOutPacket() {
    }

    @Override
    public void executeClient(Player arg0) {
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(arg0);
        cap.server_setBreakProgress(value);
    }
}

