package com.lazrproductions.cuffed.cap;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.compat.BetterCombatCompat;
import com.lazrproductions.cuffed.compat.ElenaiDodge2Compat;
import com.lazrproductions.cuffed.compat.EpicFightCompat;
import com.lazrproductions.cuffed.compat.ParcoolCompat;
import com.lazrproductions.cuffed.compat.PlayerReviveCompat;
import com.lazrproductions.cuffed.effect.RestrainedEffectInstance;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class RestrainableCapability implements IRestrainableCapability {

    public AbstractArmRestraint armRestraint = null;
    public AbstractLegRestraint legRestraint = null;
    public AbstractHeadRestraint headRestraint = null;

    public ServerPlayer playerEscortingMe = null;
    public ServerPlayer whoImEscorting = null;

    public ServerPlayer playerFriskingMe = null;

    private boolean markedForSync = false;

    // #region Server Side Stuff

    public void tickServer(ServerPlayer player) {
        if (headRestraint != null)
            headRestraint.onTickServer(player);
        if (armRestraint != null)
            armRestraint.onTickServer(player);
        if (legRestraint != null)
            legRestraint.onTickServer(player);

        // --- HANDLE THE RESTRAINED EFFECT ---

        int encoded = encodeRestraintDisabilities();

        if (isRestrained()) {
            if (!player.hasEffect(ModEffects.RESTRAINED_EFFECT.get())) {
                // Add new effect
                RestrainedEffectInstance inst = new RestrainedEffectInstance(-1, encoded);
                player.addEffect(inst);
            } else {
                // Remove and replace existing effect
                if (player.getEffect(ModEffects.RESTRAINED_EFFECT.get()) instanceof RestrainedEffectInstance e
                        && e.getAmplifier() != encoded) {
                    player.removeEffect(ModEffects.RESTRAINED_EFFECT.get());
                    RestrainedEffectInstance inst = new RestrainedEffectInstance(-1, encoded);
                    player.addEffect(inst);
                }
            }
        } else if (player.hasEffect(ModEffects.RESTRAINED_EFFECT.get()))
            player.removeEffect(ModEffects.RESTRAINED_EFFECT.get());

        // ---

        // --- HANDLE ESCORTING

        if (playerEscortingMe != null) {            
            playerEscortingMe.sendSystemMessage(
                    Component.translatable("info.cuffed.escorting.giving").append(player.getDisplayName()), true);
            player.sendSystemMessage(
                    Component.translatable("info.cuffed.escorting.getting").append(playerEscortingMe.getDisplayName()), true);

            Vec3 escortPivot = MathUtilities.GetPositionFromTowardsRotationInDegrees(playerEscortingMe.position(), playerEscortingMe.getYRot() + 90, 0, 0.45f);
            Vec3 escortTarget = MathUtilities.GetPositionFromTowardsRotationInDegrees(escortPivot, playerEscortingMe.getYRot(), 0, 0.9f);
            
            player.connection.teleport(escortTarget.x(), escortTarget.y(), escortTarget.z(), playerEscortingMe.getYRot(), player.getXRot(), RelativeMovement.ROTATION);

            if (!isRestrained()) {
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
            } else if (playerEscortingMe == null || playerEscortingMe.isRemoved()) {
                playerEscortingMe = null;
            } else if (playerEscortingMe.distanceTo(player) > 3.5f || playerEscortingMe.isCrouching()) {
                playerEscortingMe.sendSystemMessage(Component.translatable("info.cuffed.escorting.giving.cancel")
                        .append(player.getDisplayName()), true);
                player.sendSystemMessage(Component.translatable("info.cuffed.escorting.getting.cancel", playerEscortingMe.getDisplayName()), true);
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
            } else if (playerEscortingMe != null && playerEscortingMe.isRemoved() || player.isRemoved())
                CuffedAPI.Capabilities.getRestrainableCapability(playerEscortingMe).stopEscortingPlayer();
        }

        if (whoImEscorting == null || whoImEscorting.isRemoved())
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
        if (headRestraint != null)
            nbt.put("HeadRestraint", headRestraint.serializeNBT());
        if (armRestraint != null)
            nbt.put("ArmRestraint", armRestraint.serializeNBT());
        if (legRestraint != null)
            nbt.put("LegRestraint", legRestraint.serializeNBT());
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("HeadRestraint"))
            headRestraint = (AbstractHeadRestraint) RestraintAPI.getRestraintFromTag(nbt.getCompound("HeadRestraint"));
        else
            headRestraint = null;

        if (nbt.contains("ArmRestraint"))
            armRestraint = (AbstractArmRestraint) RestraintAPI.getRestraintFromTag(nbt.getCompound("ArmRestraint"));
        else
            armRestraint = null;

        if (nbt.contains("LegRestraint"))
            legRestraint = (AbstractLegRestraint) RestraintAPI.getRestraintFromTag(nbt.getCompound("LegRestraint"));
        else
            legRestraint = null;

        markedForSync = true;
    }

    public boolean onInteractedByOther(ServerPlayer player, ServerPlayer other, double interactionHeight, ItemStack stack,
            InteractionHand hand, boolean avoidEscort) {
        if (RestraintAPI.isRestraintItem(stack)) {
            if (interactionHeight > 1.5f) {
                AbstractRestraint r = RestraintAPI.getRestraintFromStack(stack, RestraintType.Head, player, other);
                if (r instanceof AbstractHeadRestraint a)
                    if (TryEquipRestraint(player, other, a)) {
                        ModStatistics.awardRestraintItemUsed(other, stack);
                        stack.shrink(1);
                        return true;
                    }
            }
            if (interactionHeight > 0.33f && interactionHeight <= 1.5f) {
                AbstractRestraint r = RestraintAPI.getRestraintFromStack(stack, RestraintType.Arm, player, other);
                if (r instanceof AbstractArmRestraint a)
                    if (TryEquipRestraint(player, other, a)) {
                        ModStatistics.awardRestraintItemUsed(other, stack);
                        stack.shrink(1);
                        return true;
                    }
            }
            if (interactionHeight <= 0.33f) {
                AbstractRestraint r = RestraintAPI.getRestraintFromStack(stack, RestraintType.Leg, player, other);
                if (r instanceof AbstractLegRestraint l)
                    if (TryEquipRestraint(player, other, l)) {
                        ModStatistics.awardRestraintItemUsed(other, stack);
                        stack.shrink(1);
                        return true;
                    }
            }
        
        } else if (stack.getItem() instanceof AbstractRestraintKeyItem) {
            if (interactionHeight > 1.5f) {
                if (headRestraint != null)
                    if (headRestraint.getKeyItem() == null || headRestraint.getKeyItem() == stack.getItem())
                        if (TryUnequipRestraint(player, other, RestraintType.Head)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            } else if (interactionHeight > 0.33f && interactionHeight <= 1.5f) {
                if (armRestraint != null)
                    if (armRestraint.getKeyItem() == null || armRestraint.getKeyItem() == stack.getItem())
                        if (TryUnequipRestraint(player, other, RestraintType.Arm)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            } else if (interactionHeight <= 0.33f) {
                if (legRestraint != null)
                    if (legRestraint.getKeyItem() == null || legRestraint.getKeyItem() == stack.getItem())
                        if (TryUnequipRestraint(player, other, RestraintType.Leg)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            }
        } else if (stack.is(ModItems.LOCKPICK.get())) {
            int lockpickType = -1;
            if (interactionHeight > 1.5f && headRestrained() && headRestraint.getLockpickable())
                lockpickType = RestraintType.Head.toInteger();
            else if (interactionHeight > 0.33f && interactionHeight <= 1.5f && armsRestrained()
                    && armRestraint.getLockpickable())
                lockpickType = RestraintType.Arm.toInteger();
            else if (interactionHeight <= 0.33f && legsRestrained() && legRestraint.getLockpickable())
                lockpickType = RestraintType.Leg.toInteger();

            if (lockpickType > -1) {
                int speedIncreasePerPick = lockpickType == RestraintType.Leg.toInteger()
                        ? getLegRestraint().getLockpickingSpeedIncreasePerPick()
                        : lockpickType == RestraintType.Arm.toInteger()
                                ? getArmRestraint().getLockpickingSpeedIncreasePerPick()
                                : getHeadRestraint().getLockpickingSpeedIncreasePerPick();
                int progressPerPick = lockpickType == RestraintType.Leg.toInteger()
                        ? getLegRestraint().getLockpickingProgressPerPick()
                        : lockpickType == RestraintType.Arm.toInteger()
                                ? getArmRestraint().getLockpickingProgressPerPick()
                                : getHeadRestraint().getLockpickingProgressPerPick();

                CuffedAPI.Networking.sendLockpickBeginPickingRestraintPacketToClient((ServerPlayer) other,
                        player.getUUID().toString(), lockpickType, speedIncreasePerPick, progressPerPick);
                return true;
            }
        } else if (stack.isEmpty() && other.isCrouching()) {
            if (interactionHeight > 1.5f) {
                if (headRestraint != null)
                    if (headRestraint.getKeyItem() == null)
                        if (TryUnequipRestraint(player, other, RestraintType.Head)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            } else if (interactionHeight > 0.33f && interactionHeight <= 1.5f) {
                if (armRestraint != null)
                    if (armRestraint.getKeyItem() == null)
                        if (TryUnequipRestraint(player, other, RestraintType.Arm)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            } else if (interactionHeight <= 0.33f) {
                if (legRestraint != null)
                    if (legRestraint.getKeyItem() == null)
                        if (TryUnequipRestraint(player, other, RestraintType.Leg)) {
                            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
                            return true;
                        }
            }
        } else if (stack.isEmpty() && !other.isCrouching() && isRestrained() && !avoidEscort) {
            CuffedAPI.Capabilities.getRestrainableCapability(other).startEscortingPlayer(other, player);
            return true;
        }
    
        return false;
    }

    // #endregion

    // #region Restraint Management

    public boolean restraintsDisabledBreakingBlocks() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowBreakingBlocks())
            v = true;
        if (legRestraint != null && !legRestraint.AllowBreakingBlocks())
            v = true;
        if (headRestraint != null && !headRestraint.AllowBreakingBlocks())
            v = true;
        return v;
    }

    public boolean restraintsDisabledItemUse() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowItemUse())
            v = true;
        if (legRestraint != null && !legRestraint.AllowItemUse())
            v = true;
        if (headRestraint != null && !headRestraint.AllowItemUse())
            v = true;
        return v;
    }

    public boolean restraintsDisabledMovement() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowMovement())
            v = true;
        if (legRestraint != null && !legRestraint.AllowMovement())
            v = true;
        if (headRestraint != null && !headRestraint.AllowMovement())
            v = true;
        return v;
    }

    public boolean restraintsDisabledJumping() {
        boolean v = false;
        if (armRestraint != null && !armRestraint.AllowJumping())
            v = true;
        if (legRestraint != null && !legRestraint.AllowJumping())
            v = true;
        if (headRestraint != null && !headRestraint.AllowJumping())
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
        if (headRestraint != null) {
            if (!headRestraint.AllowBreakingBlocks())
                noMining = true;
            if (!headRestraint.AllowItemUse())
                noItemUse = true;
            if (!headRestraint.AllowMovement())
                noMovement = true;
            if (!headRestraint.AllowJumping())
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

    public boolean headRestrained() {
        return headRestraint != null;
    }

    public boolean isRestrained() {
        return armsRestrained() || legsRestrained() || headRestrained();
    }

    public boolean isRestrained(RestraintType type) {
        if (type == RestraintType.Arm)
            return armsRestrained();
        if (type == RestraintType.Leg)
            return legsRestrained();
        return headRestrained();
    }

    public ResourceLocation getArmRestraintId() {
        if (armRestraint != null)
            return armRestraint.getId();
        return ResourceLocation.fromNamespaceAndPath("minecraft","air");
    }

    public ResourceLocation getLegRestraintId() {
        if (legRestraint != null)
            return legRestraint.getId();
        return ResourceLocation.fromNamespaceAndPath("minecraft","air");
    }

    public ResourceLocation getHeadRestraintId() {
        if (headRestraint != null)
            return headRestraint.getId();
        return ResourceLocation.fromNamespaceAndPath("minecraft","air");
    }

    public ResourceLocation getRestraintId(RestraintType type) {
        if (type == RestraintType.Arm)
            return getArmRestraintId();
        if (type == RestraintType.Head)
            return getHeadRestraintId();
        return getLegRestraintId();
    }

    public AbstractArmRestraint getArmRestraint() {
        return armRestraint;
    }

    public AbstractLegRestraint getLegRestraint() {
        return legRestraint;
    }

    public AbstractHeadRestraint getHeadRestraint() {
        return headRestraint;
    }

    public AbstractRestraint getRestraint(RestraintType type) {
        if (type == RestraintType.Arm)
            return getArmRestraint();
        if (type == RestraintType.Head)
            return getHeadRestraint();
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

    public void setHeadRestraintWithoutWarning(@Nonnull ServerPlayer player,
            @Nullable AbstractHeadRestraint newRestraint) {
        headRestraint = newRestraint;
        CuffedAPI.Networking.sendRestraintSyncPacket(player);
    }

    public void setRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint newValue,
            RestraintType type) {
        if (type == RestraintType.Arm) {
            if (newValue instanceof AbstractArmRestraint arm)
                armRestraint = arm;
        } else if (type == RestraintType.Head) {
            if (newValue instanceof AbstractHeadRestraint head)
                headRestraint = head;
        } else if (type == RestraintType.Leg) {
            if (newValue instanceof AbstractLegRestraint leg)
                legRestraint = leg;
        }

        CuffedAPI.Networking.sendRestraintSyncPacket(player);
    }

    public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractArmRestraint restraint) {
        if (armRestraint == null) {
            if (CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() && !isRestrained()) {
                if(!CuffedMod.PlayerReviveInstalled) {
                    if (player.getHealth() / player.getMaxHealth() <= 0.3f) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                } else {
                    if(PlayerReviveCompat.IsBleedingOut(player)) {
                        PlayerReviveCompat.Revive(player);
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                }
            } else {
                EquipRestraint(player, captor, restraint);
                return true;
            }
        }
        return false;
    }

    public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractLegRestraint restraint) {
        if (legRestraint == null) {
            if (CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() && !isRestrained()) {
                if(!CuffedMod.PlayerReviveInstalled) {
                    if (player.getHealth() / player.getMaxHealth() <= 0.3f) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                } else {
                    if(PlayerReviveCompat.IsBleedingOut(player)) {
                        PlayerReviveCompat.Revive(player);
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                }
            } else {
                EquipRestraint(player, captor, restraint);
                return true;
            }
        }
        return false;
    }

    public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractHeadRestraint restraint) {
        if (headRestraint == null) {
            if (CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() && !isRestrained()) {
                if(!CuffedMod.PlayerReviveInstalled) {
                    if (player.getHealth() / player.getMaxHealth() <= 0.3f) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                } else {
                    if(PlayerReviveCompat.IsBleedingOut(player)) {
                        PlayerReviveCompat.Revive(player);
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        EquipRestraint(player, captor, restraint);
                        return true;
                    }
                }
            } else {
                EquipRestraint(player, captor, restraint);
                return true;
            }
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

    public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor,
            AbstractHeadRestraint restraint) {
        AbstractHeadRestraint oldRestraint = headRestraint; // is supposed to always be null

        headRestraint = restraint;
        headRestraint.onEquippedServer(player, captor);

        // Send sync packet to sync data to client
        CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Head, headRestraint, oldRestraint);
    }

    public boolean TryUnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser,
            RestraintType type) {
        if ((type == RestraintType.Arm && armRestraint != null)
                || (type == RestraintType.Leg && legRestraint != null)
                || (type == RestraintType.Head && headRestraint != null)) {
            UnequipRestraint(player, releaser, type);
            return true;
        }
        return false;
    }

    public void UnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser, RestraintType type) {
        AbstractRestraint oldRestraint = null;
        if (type == RestraintType.Arm)
            oldRestraint = armRestraint;
        else if (type == RestraintType.Leg)
            oldRestraint = legRestraint;
        else if (type == RestraintType.Head)
            oldRestraint = headRestraint;

        if (oldRestraint != null) {
            ItemStack stack = oldRestraint.saveToItemStack();
            if (releaser == null || !releaser.addItem(stack)) {
                ItemEntity e = new ItemEntity(player.level(), player.getX(), player.getY() + 0.6D, player.getZ(),
                        stack);
                e.setDefaultPickUpDelay();
                player.level().addFreshEntity(e);
            }

            oldRestraint.onUnequippedServer(player);
        }

        if (type == RestraintType.Arm)
            armRestraint = null;
        else if (type == RestraintType.Leg)
            legRestraint = null;
        else if (type == RestraintType.Head)
            headRestraint = null;

        // Send sync packet
        CuffedAPI.Networking.sendRestraintEquipPacket(player, releaser, type, null, oldRestraint);
    }

    // #endregion

    //#region Skillcheck Management

    public void beginSkillcheck() {
        
    }

    //#endregion

    // #region Escort Management

    public void startEscortingPlayer(@Nonnull ServerPlayer self, @Nonnull ServerPlayer playerToEscort) {
        playerToEscort.removeVehicle();
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
        if (isRestrained()) {
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

        if (headRestraint != null)
            headRestraint.onTickClient(player);
        if (armRestraint != null)
            armRestraint.onTickClient(player);
        if (legRestraint != null)
            legRestraint.onTickClient(player);
    }

    public void onKeyInput(Player player, int keyCode, int action) {
        if (headRestraint != null)
            headRestraint.onKeyInput(player, keyCode, action);
        if (armRestraint != null)
            armRestraint.onKeyInput(player, keyCode, action);
        if (legRestraint != null)
            legRestraint.onKeyInput(player, keyCode, action);
    }

    public void onMouseInput(Player player, int keyCode, int action) {
        if (headRestraint != null)
            headRestraint.onMouseInput(player, keyCode, action);
        if (armRestraint != null)
            armRestraint.onMouseInput(player, keyCode, action);
        if (legRestraint != null)
            legRestraint.onMouseInput(player, keyCode, action);
    }

    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        if (headRestraint != null)
            headRestraint.renderOverlay(player, graphics, partialTick, window);
        if (armRestraint != null)
            armRestraint.renderOverlay(player, graphics, partialTick, window);
        if (legRestraint != null)
            legRestraint.renderOverlay(player, graphics, partialTick, window);
    }

    public ArrayList<Integer> gatherBlockedInputs() {
        ArrayList<Integer> b = new ArrayList<Integer>();
        if (headRestraint != null)
            b.addAll(headRestraint.getBlockedKeyCodes());
        if (armRestraint != null)
            b.addAll(armRestraint.getBlockedKeyCodes());
        if (legRestraint != null)
            b.addAll(legRestraint.getBlockedKeyCodes());
        return b;
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
        if (headRestraint != null) {
            headRestraint.onDeathServer(player);

            boolean shouldUnequip = true;
            if (headRestraint instanceof IEnchantableRestraint e)
                if(e.hasEnchantment(Enchantments.BINDING_CURSE))
                    shouldUnequip = false;
            if(shouldUnequip)
                TryUnequipRestraint(player, null, RestraintType.Head);
        }
        if (armRestraint != null) {
            armRestraint.onDeathServer(player);

            boolean shouldUnequip = true;
            if (armRestraint instanceof IEnchantableRestraint e)
                if(e.hasEnchantment(Enchantments.BINDING_CURSE))
                    shouldUnequip = false;
            if(shouldUnequip)
                TryUnequipRestraint(player, null, RestraintType.Arm);
        }
        if (legRestraint != null) {
            legRestraint.onDeathServer(player);
            
            boolean shouldUnequip = true;
            if (legRestraint instanceof IEnchantableRestraint e)
                if(e.hasEnchantment(Enchantments.BINDING_CURSE))
                    shouldUnequip = false;
            if(shouldUnequip)
                TryUnequipRestraint(player, null, RestraintType.Leg);
        }

    }

    public void onDeathClient(Player player) {
        if (headRestraint != null)
            headRestraint.onDeathClient(player);
        if (armRestraint != null)
            armRestraint.onDeathClient(player);
        if (legRestraint != null)
            legRestraint.onDeathClient(player);
    }

    public float onLandServer(ServerPlayer player, float distance, float damageMultiplier) {
        float mult = damageMultiplier;
        if (headRestraint != null)
            mult *= headRestraint.onLandServer(player, distance, damageMultiplier);
        if (armRestraint != null)
            mult *= armRestraint.onLandServer(player, distance, damageMultiplier);
        if (legRestraint != null)
            mult *= legRestraint.onLandServer(player, distance, damageMultiplier);
        return mult;
    }

    public void onLandClient(Player player, float distance, float damageMultiplier) {
        if (headRestraint != null)
            headRestraint.onLandClient(player, distance, damageMultiplier);
        if (armRestraint != null)
            armRestraint.onLandClient(player, distance, damageMultiplier);
        if (legRestraint != null)
            legRestraint.onLandClient(player, distance, damageMultiplier);
    }

    public void onJumpServer(ServerPlayer player) {
        if (headRestraint != null)
            headRestraint.onJumpServer(player);
        if (armRestraint != null)
            armRestraint.onJumpServer(player);
        if (legRestraint != null)
            legRestraint.onJumpServer(player);
    }

    public void onJumpClient(Player player) {
        if (headRestraint != null)
            headRestraint.onJumpClient(player);
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
