package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.client.CuffedEventClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CanBeNull;
import team.creative.creativecore.common.network.CreativePacket;

public class HandcuffingPacket extends CreativePacket {
    
    @CanBeNull
    public UUID handcuffer;
    public boolean showGraphic;
    public boolean isCuffed;
    public boolean isBeingCuffed;
    public boolean isSoftCuffed;
    public boolean isChained;
    public int anchor;
    public float progress;
    
    public HandcuffingPacket(UUID handcuffer, boolean showGraphic, boolean isCuffed, boolean isBeingCuffed, boolean isSoftCuffed, boolean isChained, int anchor, float progress) {
        this.handcuffer = handcuffer;
        this.showGraphic = showGraphic;
        this.isCuffed = isCuffed;
        this.isBeingCuffed = isBeingCuffed;
        this.isSoftCuffed = isSoftCuffed;
        this.isChained = isChained;
        this.anchor = anchor;
        this.progress = progress;
    }
    
    public HandcuffingPacket() {}
    
    @Override
    public void executeClient(Player player) {
        //CuffedMod.LOGGER.info("receiving update packet on cleint :)\nhandcuffer -> " + handcuffer+"\nshowGraphic -> " + showGraphic+"\nisCuffed -> " + isCuffed + "\nisBeingCuffed -> " + isBeingCuffed + "\nisChained -> "+isChained+"\nanchor -> " + anchor + "\nprogress -> " + progress);
        CuffedEventClient.handcuffer = handcuffer;
        CuffedEventClient.showGraphic = showGraphic;
        CuffedEventClient.isCuffed = isCuffed;
        CuffedEventClient.isBeingCuffed = isBeingCuffed;
        CuffedEventClient.isSoftCuffed = isSoftCuffed;
        CuffedEventClient.isChained = isChained;
        CuffedEventClient.anchor = anchor;
        CuffedEventClient.progress = progress;
    }
    
    @Override
    public void executeServer(ServerPlayer player) {}
    
}