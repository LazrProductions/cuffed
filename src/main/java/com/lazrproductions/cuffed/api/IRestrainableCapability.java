package com.lazrproductions.cuffed.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IRestrainableCapability {

    //#region Server-Side operations

    /**
     * Copy data from another capability to this one, the given capabilit must be the same type to copy it's data.
     * @param cap The other capability to copy from
     */
    public void copyFrom(CompoundTag tag, ServerLevel level);

    public CompoundTag serializeNBT();
    public void deserializeNBT(CompoundTag nbt);

    /**
    * Called every tick on the server
    * @param player The player that is getting ticked
    */
    public void tickServer(ServerPlayer player);

    //#endregion

    //#region Client-Side operations

    /**
    * Called every tick only on the local client
    * @param player The client that is getting ticked
    */
    public void tickClient(Player player);

    /**
     * Called on the client when any key is pressed, released, or held.
     * @param player The player who performed the action
     * @param keyCode The key that was pressed, released, or held.
     * @param action The action code cooresponding to whether the button was pressed, released, or held. (1 -> pressed, 0 -> released, 2 -> held.)
     */
    public void onKeyInput(Player player, int keyCode, int action);
    /**
     * Called on the client when any mouse button is pressed, released, or held.
     * @param player The player who performed the action
     * @param keyCode The mouse button that was pressed, released, or held.
     * @param action The action code cooresponding to whether the button was pressed, released, or held. (1 -> pressed, 0 -> released, 2 -> held.)
     */
    public void onMouseInput(Player player, int keyCode, int action);


    /**
    * Render any overlay this hero may have onto the screen.
    */
    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window);

    //#endregion

    //#region Restraint Management

    public boolean restraintsDisabledBreakingBlocks();
    public boolean restraintsDisabledItemUse();
    public boolean restraintsDisabledMovement();
    public boolean restraintsDisabledJumping();
    public int encodeRestraintDisabilities();

    /** Get whether or not this player's arms are restrained. */
    public boolean armsRestrained();
    /** Get whether or not this player's legs are restrained. */
    public boolean legsRestrained();
    /** Get whether or not this player's head is restrained. */
    public boolean headRestrained();
    /** Get whether or not this player is restrained in any way. */
    public boolean isRestrained();
    /** Get whether or not this player is restrained with the given type. */
    public boolean isRestrained(RestraintType type);
    /** Get the id of this player's arm restraint. */
    public String getArmRestraintId();
    /** Get the id of this player's leg restraint. */
    public String getLegRestraintId();
    /** Get the id of this player's head restraint. */
    public String getHeadRestraintId();
    /** Get the id of this player's restraint with the given type. */
    public String getRestraintId(RestraintType type);
    /** Get this player's arm restraint. */
    @Nullable public AbstractArmRestraint getArmRestraint();
    /** Get this player's leg restraint */
    @Nullable public AbstractLegRestraint getLegRestraint();
    /** Get this player's head restraint */
    @Nullable public AbstractHeadRestraint getHeadRestraint();
    /** Get this player's restraint with the given type */
    @Nullable public AbstractRestraint getRestraint(RestraintType type);
    /** Set this player's arm restraint without sending any events. */
    public void setArmRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractArmRestraint newValue);
    /** Set this player's leg restraint without sending any events. */
    public void setLegRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractLegRestraint newValue);
    /** Set this player's leg restraint without sending any events. */
    public void setHeadRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractHeadRestraint newValue);
    /** Set this player's restraint with the given type without sending any events. */
    public void setRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint newValue, RestraintType type);

    //#endregion

    //#region Escorting

    /**
     * Start escorting another player, and notify them that they are being escorted.
     */
    public void startEscortingPlayer(@Nonnull ServerPlayer self, @Nonnull ServerPlayer other);
    /**
     * Notify this player that they have started to be escorted..
     */
    public void startGettingEscortedByPlayer(@Nonnull ServerPlayer other);

    /**
     * Stop escorting who you were, and notify them that they are no longer being escorted.
     */
    public void stopEscortingPlayer();
    /**
     * Notify this player that they are no longer being escorted.
     */
    public void stopGettingEscortedByPlayer();

    /**
     * Get the player who this player is escorting.
     */
    public ServerPlayer getWhoImEscorting();
    /**
     * Get the player who is escorting this player.
     */
    public ServerPlayer getMyEscort();
    
    //#endregion

    //#region Event Handlers

    public void onInteractedByOther(ServerPlayer player, ServerPlayer other, double interactionHeight, ItemStack stack, InteractionHand hand);

    /** Called on the server when this player has died. */
    public abstract void onDeathServer(ServerPlayer player);
    /** Called on the client when this player has died. */
    public abstract void onDeathClient(Player player);

    /** Called on the server when this player logs in. */
    public abstract void onLoginServer(ServerPlayer player);
    /** Called on the client when this player logs in. */
    public abstract void onLoginClient(Player player);

    /** Called on the server when this player logs out. */
    public abstract void onLogoutServer(ServerPlayer player);
    /** Called on the client when this player logs out. */
    public abstract void onLogoutClient(Player player);

    /**
     * Called on the server when this player lands on the ground.
     * @param player The player who is landing.
     * @param damageToTake How much fall damage this player will take.
     * @return The modified amount of damage to take, will replace the original value and the player will instead take this amount of damage.
     */
    public abstract float onLandServer(ServerPlayer player, float distance, float damageMultiplier);
    /**
     * Called on the client when this player lands on the ground.
     * @param player The player who is landing.
     * @param damageToTake How much fall damage this player will take.
     */
    public abstract void onLandClient(Player player, float distance, float damageMultiplier);

    /** Called on the server when this player jumps. */
    public abstract void onJumpServer(ServerPlayer player);
    /** Called on the client when this player jumps. */
    public abstract void onJumpClient(Player player);

    public abstract boolean onTickRideServer(ServerPlayer player, Entity vehicle);
    public abstract boolean onTickRideClient(Player player, Entity vehicle);

    //#endregion
}
