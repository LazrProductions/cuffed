package com.lazrproductions.cuffed.restraints.base;

import net.minecraft.client.Options;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public interface IBreakableRestraint {
    public SoundEvent getBreakSound();
    public int getMaxDurability();
    public boolean isKeyToAttemptBreak(int keyCode, Options options);
    public boolean requireAlternateKeysToAttemptBreak();
    public boolean dropItemOnBroken();
    public boolean canBeBrokenOutOf();

    public int getDurability();

    public void attemptToBreak(Player player, int keyCode, int action, Options options);
    public void setDurability(ServerPlayer player, int value);
    public void incrementDurability(ServerPlayer player, int value);

    public void onBrokenServer(ServerPlayer player);
    public void onBrokenClient(Player player);
}
