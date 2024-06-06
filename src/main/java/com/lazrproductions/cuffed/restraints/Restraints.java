package com.lazrproductions.cuffed.restraints;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class Restraints {
    public static boolean IsRestraintItem(ItemStack stack) {
        return stack.is(ModItems.HANDCUFFS.get()) || stack.is(ModItems.SHACKLES.get()) || stack.is(ModItems.FUZZY_HANDCUFFS.get()) || stack.is(ModItems.LEGCUFFS.get()) | stack.is(ModItems.LEG_SHACKLES.get());
    }
    public static boolean IsArmRestraintItem(ItemStack stack) {
        return stack.is(ModItems.HANDCUFFS.get()) || stack.is(ModItems.SHACKLES.get()) || stack.is(ModItems.FUZZY_HANDCUFFS.get());
    }
    public static boolean IsLegRestraintItem(ItemStack stack) {
        return stack.is(ModItems.LEGCUFFS.get()) || stack.is(ModItems.LEG_SHACKLES.get());
    }

    public static boolean IsRestraintKeyItem(ItemStack stack) {
        return stack.is(ModItems.HANDCUFFS_KEY.get());
    }

    public static AbstractRestraint GetRestraintFromStack(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        if (IsRestraintItem(stack)) {
            if (stack.is(HandcuffsRestraint.ITEM)) {
                HandcuffsRestraint res = new HandcuffsRestraint(stack, player, captor);
                return res;
            }
            if (stack.is(ShacklesRestraint.ITEM)) {
                ShacklesRestraint res = new ShacklesRestraint(stack, player, captor);
                return res;
            }
            if (stack.is(FuzzyHandcuffsRestraint.ITEM)) {
                FuzzyHandcuffsRestraint res = new FuzzyHandcuffsRestraint(stack, player, captor);
                return res;
            }
            if (stack.is(LegcuffsRestraint.ITEM)) {
                LegcuffsRestraint res = new LegcuffsRestraint(stack, player, captor);
                return res;
            }
            if (stack.is(LegShacklesRestraint.ITEM)) {
                LegShacklesRestraint res = new LegShacklesRestraint(stack, player, captor);
                return res;
            }
        }
        return null;
    }
    public static AbstractRestraint GetRestraintFromStack(ItemStack stack) {
        if (IsRestraintItem(stack)) {
            if (stack.is(HandcuffsRestraint.ITEM)) {
                HandcuffsRestraint res = new HandcuffsRestraint();
                return res;
            }
            if (stack.is(ShacklesRestraint.ITEM)) {
                ShacklesRestraint res = new ShacklesRestraint();
                return res;
            }
            if (stack.is(FuzzyHandcuffsRestraint.ITEM)) {
                FuzzyHandcuffsRestraint res = new FuzzyHandcuffsRestraint();
                return res;
            }
            if (stack.is(LegcuffsRestraint.ITEM)) {
                LegcuffsRestraint res = new LegcuffsRestraint();
                return res;
            }
            if (stack.is(LegShacklesRestraint.ITEM)) {
                LegShacklesRestraint res = new LegShacklesRestraint();
                return res;
            }
        }
        return null;
    }

    public static AbstractRestraint GetRestraintFromNBT(CompoundTag tag) {
        if (tag.contains("Id")) {
            AbstractRestraint r = getNewRestraintById(tag.getString("Id"));
            if (r != null) {
                r.deserializeNBT(tag);
                return r;
            }
        }
        return null;
    }

    public static AbstractRestraint getNewRestraintById(String id) {
        switch (id) {
            case CuffedMod.MODID + ":handcuffs":
                return new HandcuffsRestraint();
            case CuffedMod.MODID + ":shackles":
                return new ShacklesRestraint();
            case CuffedMod.MODID + ":fuzzy_handcuffs":
                return new FuzzyHandcuffsRestraint();
            case CuffedMod.MODID + ":legcuffs":
                return new LegcuffsRestraint();
            case CuffedMod.MODID + ":leg_shackles":
                return new LegShacklesRestraint();
            default:
                return null;
        }
    }

    public static ArmRestraintAnimationFlags getArmAnimationFlagById(String id) {
        switch (id) {
            case HandcuffsRestraint.ID:
                return HandcuffsRestraint.ARM_ANIMATION_FLAGS;
            case ShacklesRestraint.ID:
                return ShacklesRestraint.ARM_ANIMATION_FLAGS;
            case FuzzyHandcuffsRestraint.ID:
                return FuzzyHandcuffsRestraint.ARM_ANIMATION_FLAGS;
            default:
                return ArmRestraintAnimationFlags.NONE;
        }
    }
    public static LegRestraintAnimationFlags getLegAnimationFlagById(String id) {
        switch (id) {
            case LegcuffsRestraint.ID:
                return LegcuffsRestraint.LEG_ANIMATION_FLAGS;
            case LegShacklesRestraint.ID:
                return LegShacklesRestraint.LEG_ANIMATION_FLAGS;
            default:
                return LegRestraintAnimationFlags.NONE;
        }
    }
}
