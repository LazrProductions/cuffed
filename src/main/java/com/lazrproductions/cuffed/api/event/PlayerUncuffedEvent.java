package com.lazrproductions.cuffed.api.event;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.IHandcuffed;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** Fired before a player is uncuffed. */
public class PlayerUncuffedEvent extends PlayerEvent {
    
    private final IHandcuffed handcuffed;
    
    public PlayerUncuffedEvent(Player player, IHandcuffed handcuffed) {
        super(player);
        this.handcuffed = handcuffed;
        CuffedMod.LOGGER.info("(PlayerUncuffedEvent) PlayerUncuffedEvent called.");
    }
    
    public IHandcuffed getHandcuffed() {
        return this.handcuffed;
    }
}