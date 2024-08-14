package com.lazrproductions.cuffed.cap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.compat.BetterCombatCompat;
import com.lazrproductions.cuffed.compat.ElenaiDodge2Compat;
import com.lazrproductions.cuffed.compat.EpicFightCompat;
import com.lazrproductions.cuffed.compat.ParcoolCompat;
import com.lazrproductions.cuffed.effect.RestrainedEffectInstance;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;
import com.lazrproductions.cuffed.restraints.Restraints;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.client.gui.GuiGraphics;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class RestrainableCapability implements IRestrainableCapability {

    public AbstractArmRestraint armRestraint = null;
    public AbstractLegRestraint legRestraint = null;

    public ServerPlayer playerEscortingMe = null;
    public ServerPlayer whoImEscorting = null;

    private boolean markedForSync = false;

    // #region Server Side Stuff

    public void tickServer(ServerPlayer player) {
        if (armRestraint != null)
            armRestraint.onTickServer(player);
        if (legRestraint != null)
            legRestraint.onTickServer(player);

        // --- HANDLE THE RESTRAINED EFFECT ---

        int encoded = encodeRestraintDisabilities();

        if (legRestraint != null || armRestraint != null) {
            if (!player.hasEffect(ModEffects.RESTRAINED_EFFECT.get())) {
                // Add new effect
                RestrainedEffectInstance inst = new RestrainedEffectInstance(0, encoded);
                player.addEffect(inst);
            } else {
                // Remove and replace existing effect
                if (player.getEffect(ModEffects.RESTRAINED_EFFECT.get()) instanceof RestrainedEffectInstance e
                        && e.getAmplifier() != encoded) {
                    player.removeEffect(ModEffects.RESTRAINED_EFFECT.get());
                    RestrainedEffectInstance inst = new RestrainedEffectInstance(0, encoded);
                    player.addEffect(inst);
                }
            }
        } else if (player.hasEffect(ModEffects.RESTRAINED_EFFECT.get()))
            player.removeEffect(ModEffects.RESTRAINED_EFFECT.get());

        // ---

        // --- HANDLE ESCORTING

        if (playerEscortingMe != null) {
            playerEscortingMe.sendSystemMessage(Component.translatable("info.cuffed.escorting").append(player.getDisplayName()), true);

            if (!armsOrLegsRestrained()) {
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
            } else if (playerEscortingMe == null || playerEscortingMe.isRemoved()) {
                playerEscortingMe = null;
            } else if (playerEscortingMe.distanceTo(player) > 3.5f) {
                playerEscortingMe.sendSystemMessage(Component.translatable("info.cuffed.cancel_escorting")
                        .append(player.getDisplayName()), true);
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
            } else if (playerEscortingMe != null && playerEscortingMe.isRemoved() || player.isRemoved())
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
        }

        if(whoImEscorting == null || whoImEscorting.isRemoved())
            whoImEscorting = null;

        // ---

        if (markedForSync) {
            CuffedAPI.Networking.sendRestraintSyncPacket(player);
            markedForSync = false;
        }

        age++;
    }

    public int age = 0;

    public void copyFrom(CompoundTag tag, ServerLevel level) {
        deserializeNBT(tag);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (armRestraint != null)
            nbt.put("ArmRestraint", armRestraint.serializeNBT());
        if (legRestraint != null)
            nbt.put("LegRestraint", legRestraint.serializeNBT());
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("ArmRestraint"))
            armRestraint = (AbstractArmRestraint) Restraints.GetRestraintFromNBT(nbt.getCompound("ArmRestraint"));
        else
            armRestraint = null;
        if (nbt.contains("LegRestraint"))
            legRestraint = (AbstractLegRestraint) Restraints.GetRestraintFromNBT(nbt.getCompound("LegRestraint"));
        else
            legRestraint = null;

        markedForSync = true;
    }

    public void onInteractedByOther(ServerPlayer player, ServerPlayer other, double interactionHeight, ItemStack stack,
            InteractionHand hand) {
        if (stack.getItem() instanceof AbstractRestraintItem) {
            if (interactionHeight > 0.5f) {
                AbstractRestraint r = Restraints.GetRestraintFromStack(stack, player, other);
                if (r instanceof AbstractArmRestraint a)
                    if (TryEquipRestraint(player, other, a)) {
                        ModStatistics.awardRestraintItemUsed(other, stack);
                        stack.shrink(1);
                    }
            }
            if (interactionHeight <= 0.5f) {
                AbstractRestraint r = Restraints.GetRestraintFromStack(stack, player, other);
                if (r instanceof AbstractLegRestraint l)
                    if (TryEquipRestraint(player, other, l)) {
                        ModStatistics.awardRestraintItemUsed(other, stack);
                        stack.shrink(1);
                    }
            }
        } else if (stack.getItem() instanceof AbstractRestraintKeyItem) {
            if (armRestraint != null && armRestraint.getKeyItem() == stack.getItem() && interactionHeight > 0.5f)
                if(TryUnequipRestraint(player, other, RestraintType.Arm))  {
                    other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                }
            if (legRestraint != null && legRestraint.getKeyItem() == stack.getItem() && interactionHeight <= 0.5f)
                if(TryUnequipRestraint(player, other, RestraintType.Leg)) {
                    other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                }
        } else if (stack.is(ModItems.LOCKPICK.get())) {
            int lockpickType = -1;
            if(interactionHeight > 0.5 && armsRestrained())
                lockpickType = RestraintType.Arm.toInteger();
            if(interactionHeight <= 0.5 && legsRestrained())
                lockpickType = RestraintType.Leg.toInteger();
            if(lockpickType > -1) {
                CuffedAPI.Networking.sendLockpickBeginPickingRestraintPacketToClient((ServerPlayer)other, player.getUUID().toString(), lockpickType,
                    lockpickType == RestraintType.Leg.toInteger() ? getLegRestraint().getLockpickingSpeedIncreasePerPick() : getArmRestraint().getLockpickingSpeedIncreasePerPick(), 
                    lockpickType == RestraintType.Leg.toInteger() ? getLegRestraint().getLockpickingProgressPerPick() : getArmRestraint().getLockpickingProgressPerPick());
            }
        } else if (stack.isEmpty() && other.isCrouching() && armsOrLegsRestrained())
            CuffedAPI.Capabilities.getRestrainableCapability(other).startEscortingPlayer(other, player);
    }

    // #endregion

    // #region Restraint Management

    public boolean restraintsDisabledBreakingBlocks() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowBreakingBlocks())
            v = true;
        if (legRestraint != null && !legRestraint.AllowBreakingBlocks())
            v = true;
        return v;
    }

    public boolean restraintsDisabledItemUse() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowItemUse())
            v = true;
        if (legRestraint != null && !legRestraint.AllowItemUse())
            v = true;
        return v;
    }

    public boolean restraintsDisabledMovement() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowMovement())
            v = true;
        if (legRestraint != null && !legRestraint.AllowMovement())
            v = true;
        return v;
    }

    public boolean restraintsDisabledJumping() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowJumping())
            v = true;
        if (legRestraint != null && !legRestraint.AllowJumping())
            v = true;
        return v;
    }

    public int encodeRestraintDisabilities() {
        boolean noMining = false;
        boolean noItemUse = false;
        boolean noMovement = false;
        boolean noJumping = false;
        if (armRestraint != null) {
            if (!armRestraint.AllowBreakingBlocks())
                noMining = true;
            if (!armRestraint.AllowItemUse())
                noItemUse = true;
            if (!armRestraint.AllowMovement())
                noMovement = true;
            if (!armRestraint.AllowJumping())
                noJumping = true;
        }
        if (legRestraint != null) {
            if (!legRestraint.AllowBreakingBlocks())
                noMining = true;
            if (!legRestraint.AllowItemUse())
                noItemUse = true;
            if (!legRestraint.AllowMovement())
                noMovement = true;
            if (!legRestraint.AllowJumping())
                noJumping = true;
        }
        return RestrainedEffectInstance.encodeRestraintProperties(noMining, noItemUse, noMovement, noJumping);
    }

    public boolean armsRestrained() {
        return armRestraint != null;
    }

    public boolean legsRestrained() {
        return legRestraint != null;
    }

    public boolean armsOrLegsRestrained() {
        return armsRestrained() || legsRestrained();
    }

    public boolean isRestrained(RestraintType type) {
        if (type == RestraintType.Arm)
            return armsRestrained();
        return legsRestrained();
    }

    public String getArmRestraintId() {
        if (armRestraint != null)
            return armRestraint.getId();
        return "";
    }

    public String getLegRestraintId() {
        if (legRestraint != null)
            return legRestraint.getId();
        return "";
    }

    public String getRestraintId(RestraintType type) {
        if (type == RestraintType.Arm)
            return getArmRestraintId();
        return getLegRestraintId();
    }

    public AbstractArmRestraint getArmRestraint() {
        return armRestraint;
    }

    public AbstractLegRestraint getLegRestraint() {
        return legRestraint;
    }

    public AbstractRestraint getRestraint(RestraintType type) {
        if (type == RestraintType.Arm)
            return getArmRestraint();
        return getLegRestraint();
    }

    public void setArmRestraintWithoutWarning(@Nonnull ServerPlayer player,
            @Nullable AbstractArmRestraint newRestraint) {
        armRestraint = newRestraint;
        CuffedAPI.Networking.sendRestraintSyncPacket(player);
    }

    public void setLegRestraintWithoutWarning(@Nonnull ServerPlayer player,
            @Nullable AbstractLegRestraint newRestraint) {
        legRestraint = newRestraint;
        CuffedAPI.Networking.sendRestraintSyncPacket(player);
    }

    public void setRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint newValue,
            RestraintType type) {
        if (type == RestraintType.Arm) {
            if (newValue instanceof AbstractArmRestraint arm)
                armRestraint = arm;
        } else if (newValue instanceof AbstractLegRestraint leg)
            legRestraint = leg;

        CuffedAPI.Networking.sendRestraintSyncPacket(player);
    }

    public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractArmRestraint restraint) {
        if (armRestraint == null) {
            EquipRestraint(player, captor, restraint);
            return true;
        }
        return false;
    }

    public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractLegRestraint restraint) {
        if (legRestraint == null) {
            EquipRestraint(player, captor, restraint);
            return true;
        }
        return false;
    }

    public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractArmRestraint restraint) {
        AbstractLegRestraint oldRestraint = legRestraint; // is supposed to always be null

        armRestraint = restraint;
        armRestraint.onEquippedServer(player, captor);

        // Send sync packet to sync data to client
        CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Arm, armRestraint, oldRestraint);
    }

    public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractLegRestraint restraint) {
        AbstractLegRestraint oldRestraint = legRestraint; // is supposed to always be null

        legRestraint = restraint;
        legRestraint.onEquippedServer(player, captor);

        // Send sync packet to sync data to client
        CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Leg, legRestraint, oldRestraint);
    }

    public boolean TryUnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser,
            RestraintType type) {
        if ((type == RestraintType.Arm && armRestraint != null)
                || (type == RestraintType.Leg && legRestraint != null)) {
            UnequipRestraint(player, releaser, type);
            return true;
        }
        return false;
    }

    public void UnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser, RestraintType type) {
        AbstractRestraint oldRestraint = null;
        if (type == RestraintType.Arm)
            oldRestraint = armRestraint;
        else
            oldRestraint = legRestraint;

        ItemStack stack = oldRestraint.saveToItemStack();
        if (releaser == null || !releaser.addItem(stack)) {
            ItemEntity e = new ItemEntity(player.getLevel(), player.getX(), player.getY() + 0.6D, player.getZ(), stack);
            e.setDefaultPickUpDelay();
            player.getLevel().addFreshEntity(e);
        }

        oldRestraint.onUnequippedServer(player);

        if (type == RestraintType.Arm)
            armRestraint = null;
        else
            legRestraint = null;

        // Send sync packet
        CuffedAPI.Networking.sendRestraintEquipPacket(player, releaser, type, null, oldRestraint);
    }

    // #endregion

    // #region Escort Management

    public void startEscortingPlayer(@Nonnull ServerPlayer self, @Nonnull ServerPlayer playerToEscort) {
        CuffedAPI.Capabilities.getRestrainableCapability(playerToEscort).startGettingEscortedByPlayer(self);
        whoImEscorting = playerToEscort;
    }

    public void startGettingEscortedByPlayer(@Nonnull ServerPlayer other) {
        playerEscortingMe = other;
    }

    public void stopEscortingPlayer() {
        CuffedAPI.Capabilities.getRestrainableCapability(whoImEscorting).stopGettingEscortedByPlayer();
        whoImEscorting = null;
    }

    public void stopGettingEscortedByPlayer() {
        playerEscortingMe = null;
    }

    public ServerPlayer getWhoImEscorting() {
        return whoImEscorting;
    }

    public ServerPlayer getMyEscort() {
        return playerEscortingMe;
    }

    // #endregion

    // #region Client Side Stuff

    public void tickClient(Player player) {
        if (armsOrLegsRestrained()) {
            if (CuffedMod.BetterCombatInstalled)
                BetterCombatCompat.Disable();
            if (CuffedMod.EpicFightInstalled)
                EpicFightCompat.Disable(player);
            if (CuffedMod.ParcoolInstalled)
                ParcoolCompat.Disable();
            if (CuffedMod.ElenaiDodge2Installed)
                ElenaiDodge2Compat.Disable();
        } else {
            if (CuffedMod.BetterCombatInstalled)
                BetterCombatCompat.Reset();
            if (CuffedMod.EpicFightInstalled)
                EpicFightCompat.Reset(player);
            if (CuffedMod.ParcoolInstalled)
                ParcoolCompat.Reset();
            if (CuffedMod.ElenaiDodge2Installed)
                ElenaiDodge2Compat.Reset();
        }

        if (armRestraint != null)
            armRestraint.onTickClient(player);
        if (legRestraint != null)
            legRestraint.onTickClient(player);
    }

    public void onKeyInput(Player player, int keyCode, int action) {
        if (armRestraint != null)
            armRestraint.onKeyInput(player, keyCode, action);
        if (legRestraint != null)
            legRestraint.onKeyInput(player, keyCode, action);
    }

    public void onMouseInput(Player player, int keyCode, int action) {
        if (armRestraint != null)
            armRestraint.onMouseInput(player, keyCode, action);
        if (legRestraint != null)
            legRestraint.onMouseInput(player, keyCode, action);
    }

    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        if (armRestraint != null)
            armRestraint.renderOverlay(player, graphics, partialTick, window);
        if (legRestraint != null)
            legRestraint.renderOverlay(player, graphics, partialTick, window);
    }

    // #endregion

    // #region Events

    public void onLoginServer(ServerPlayer player) {
        if (player.hasEffect(ModEffects.RESTRAINED_EFFECT.get()))
            player.removeEffect(ModEffects.RESTRAINED_EFFECT.get());
    }

    public void onLoginClient(Player player) {
    }

    public void onLogoutServer(ServerPlayer player) {
    }

    public void onLogoutClient(Player player) {
    }

    public void onDeathServer(ServerPlayer player) {
        if (armRestraint != null) {
            armRestraint.onDeathServer(player);

            if (armRestraint instanceof IEnchantableRestraint e)
                if (!e.hasEnchantment(Enchantments.BINDING_CURSE))
                    TryUnequipRestraint(player, null, RestraintType.Arm);
        }
        if (legRestraint != null) {
            legRestraint.onDeathServer(player);

            if (legRestraint instanceof IEnchantableRestraint e)
                if (!e.hasEnchantment(Enchantments.BINDING_CURSE))
                    TryUnequipRestraint(player, null, RestraintType.Leg);
        }

    }

    public void onDeathClient(Player player) {
        if (armRestraint != null)
            armRestraint.onDeathClient(player);
        if (legRestraint != null)
            legRestraint.onDeathClient(player);
    }

    public float onLandServer(ServerPlayer player, float distance, float damageMultiplier) {
        float mult = 1;
        if (armRestraint != null)
            mult *= armRestraint.onLandServer(player, distance, damageMultiplier);
        if (legRestraint != null)
            mult *= legRestraint.onLandServer(player, distance, damageMultiplier);
        return mult;
    }

    public void onLandClient(Player player, float distance, float damageMultiplier) {
        if (armRestraint != null)
            armRestraint.onLandClient(player, distance, damageMultiplier);
        if (legRestraint != null)
            legRestraint.onLandClient(player, distance, damageMultiplier);
    }

    public void onJumpServer(ServerPlayer player) {
        if (armRestraint != null)
            armRestraint.onJumpServer(player);
        if (legRestraint != null)
            legRestraint.onJumpServer(player);
    }

    public void onJumpClient(Player player) {
        if (armRestraint != null)
            armRestraint.onJumpClient(player);
        if (legRestraint != null)
            legRestraint.onJumpClient(player);
    }

    public boolean onTickRideServer(ServerPlayer player, Entity vehicle) {
        vehicle.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    public boolean onTickRideClient(Player player, Entity vehicle) {
        vehicle.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    // #endregion
}
