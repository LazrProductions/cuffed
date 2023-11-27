package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.event.ModClientEvents;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class CuffedDebugPacket extends CreativePacket {

    public String other;

    public CuffedDebugPacket(String other) {
        this.other = other;    
    }

    public CuffedDebugPacket() {
    }

    @Override
    public void executeClient(Player arg0) {
        if(other!=null)
            ModClientEvents.clientSideHandcuffedCommand(UUID.fromString(other));
        else
            ModClientEvents.clientSideHandcuffedCommand();
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
    }
}
