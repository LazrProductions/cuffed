package com.lazrproductions.cuffed.api.event;

import com.lazrproductions.cuffed.cap.CuffedCapability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** Fired before a player is uncuffed. */
public class PlayerUncuffedEvent extends PlayerEvent {
    
    private final CuffedCapability handcuffed;
    
    public PlayerUncuffedEvent(Player player, CuffedCapability handcuffed) {
        super(player);
        this.handcuffed = handcuffed;
    }
    
    public CuffedCapability getHandcuffed() {
        return this.handcuffed;
    }
}