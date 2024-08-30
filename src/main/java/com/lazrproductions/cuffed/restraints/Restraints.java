package com.lazrproductions.cuffed.restraints;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Restraints {
    public static boolean IsRestraintItem(ItemStack stack) {
        return IsArmRestraintItem(stack) || IsLegRestraintItem(stack) || IsHeadRestraintItem(stack);
    }
    public static boolean IsArmRestraintItem(ItemStack stack) {
        return stack.is(ModItems.HANDCUFFS.get()) || stack.is(ModItems.SHACKLES.get()) || stack.is(ModItems.FUZZY_HANDCUFFS.get()) || stack.is(ModItems.DUCK_TAPE.get());
    }
    public static boolean IsLegRestraintItem(ItemStack stack) {
        return stack.is(ModItems.LEGCUFFS.get()) || stack.is(ModItems.LEG_SHACKLES.get()) || stack.is(ModItems.DUCK_TAPE.get());
    }
    public static boolean IsHeadRestraintItem(ItemStack stack) {
        if(stack.is(Items.BUNDLE))
            return true;
        return stack.is(ModItems.PILLORY_ITEM.get()) || stack.is(ModItems.DUCK_TAPE.get());
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
            if (stack.is(PilloryRestraint.ITEM)) {
                PilloryRestraint res = new PilloryRestraint(stack, player, captor);
                return res;
            }
            if(stack.is(DuckTapeHeadRestraint.ITEM))
            {
                DuckTapeHeadRestraint res = new DuckTapeHeadRestraint(stack, player, captor);
                return res;
            }
            if(stack.is(Items.BUNDLE))
            {
                BundleRestraint res = new BundleRestraint(stack, player, captor);
                return res;
            }
        }
        return null;
    }
    public static AbstractRestraint GetRestraintFromStack(ItemStack stack, RestraintType type, ServerPlayer player, ServerPlayer captor) {
        if (IsRestraintItem(stack)) {
            if(type == RestraintType.Head) {
                if (stack.is(PilloryRestraint.ITEM)) {
                    PilloryRestraint res = new PilloryRestraint(stack, player, captor);
                    return res;
                } else if(stack.is(DuckTapeHeadRestraint.ITEM))
                {
                    DuckTapeHeadRestraint res = new DuckTapeHeadRestraint(stack, player, captor);
                    return res;
                } else if(stack.is(Items.BUNDLE))
                {
                    BundleRestraint res = new BundleRestraint(stack, player, captor);
                    return res;
                }
            } else if(type== RestraintType.Arm) {
                if (stack.is(HandcuffsRestraint.ITEM)) {
                    HandcuffsRestraint res = new HandcuffsRestraint(stack, player, captor);
                    return res;
                } else if (stack.is(ShacklesRestraint.ITEM)) {
                    ShacklesRestraint res = new ShacklesRestraint(stack, player, captor);
                    return res;
                } else if (stack.is(FuzzyHandcuffsRestraint.ITEM)) {
                    FuzzyHandcuffsRestraint res = new FuzzyHandcuffsRestraint(stack, player, captor);
                    return res;
                } else if(stack.is(DuckTapeArmsRestraint.ITEM))
                {
                    DuckTapeArmsRestraint res = new DuckTapeArmsRestraint(stack, player, captor);
                    return res;
                }
            } else if(type == RestraintType.Leg) {
                if (stack.is(LegcuffsRestraint.ITEM)) {
                    LegcuffsRestraint res = new LegcuffsRestraint(stack, player, captor);
                    return res;
                } else if (stack.is(LegShacklesRestraint.ITEM)) {
                    LegShacklesRestraint res = new LegShacklesRestraint(stack, player, captor);
                    return res;
                } else if(stack.is(DuckTapeLegsRestraint.ITEM))
                {
                    DuckTapeLegsRestraint res = new DuckTapeLegsRestraint(stack, player, captor);
                    return res;
                }
            }
        }
        return null;
    }
    public static AbstractRestraint GetRestraintFromStack(ItemStack stack) {
        if (IsRestraintItem(stack)) {
            if (stack.is(HandcuffsRestraint.ITEM)) {
                HandcuffsRestraint res = new HandcuffsRestraint();
                return res;
            }else
            if (stack.is(ShacklesRestraint.ITEM)) {
                ShacklesRestraint res = new ShacklesRestraint();
                return res;
            }else
            if (stack.is(FuzzyHandcuffsRestraint.ITEM)) {
                FuzzyHandcuffsRestraint res = new FuzzyHandcuffsRestraint();
                return res;
            }else
            if (stack.is(LegcuffsRestraint.ITEM)) {
                LegcuffsRestraint res = new LegcuffsRestraint();
                return res;
            }else
            if (stack.is(LegShacklesRestraint.ITEM)) {
                LegShacklesRestraint res = new LegShacklesRestraint();
                return res;
            } else
            if (stack.is(PilloryRestraint.ITEM)) {
                PilloryRestraint res = new PilloryRestraint();
                return res;
            } else
            if (stack.is(DuckTapeHeadRestraint.ITEM)) {
                DuckTapeHeadRestraint res = new DuckTapeHeadRestraint();
                return res;
            } else if(stack.is(Items.BUNDLE))
            {
                BundleRestraint res = new BundleRestraint();
                return res;
            }
        }
        return null;
    }
    public static AbstractRestraint GetRestraintFromStack(ItemStack stack, RestraintType type) {
        if (IsRestraintItem(stack)) {
            if(type == RestraintType.Head) {
                if (stack.is(PilloryRestraint.ITEM)) {
                    PilloryRestraint res = new PilloryRestraint();
                    return res;
                } else if(stack.is(DuckTapeHeadRestraint.ITEM))
                {
                    DuckTapeHeadRestraint res = new DuckTapeHeadRestraint();
                    return res;
                } else if(stack.is(Items.BUNDLE))
                {
                    BundleRestraint res = new BundleRestraint();
                    return res;
                }
            } else if(type== RestraintType.Arm) {
                if (stack.is(HandcuffsRestraint.ITEM)) {
                    HandcuffsRestraint res = new HandcuffsRestraint();
                    return res;
                } else if (stack.is(ShacklesRestraint.ITEM)) {
                    ShacklesRestraint res = new ShacklesRestraint();
                    return res;
                } else if (stack.is(FuzzyHandcuffsRestraint.ITEM)) {
                    FuzzyHandcuffsRestraint res = new FuzzyHandcuffsRestraint();
                    return res;
                } else if(stack.is(DuckTapeArmsRestraint.ITEM))
                {
                    DuckTapeArmsRestraint res = new DuckTapeArmsRestraint();
                    return res;
                }
            } else if(type == RestraintType.Leg) {
                if (stack.is(LegcuffsRestraint.ITEM)) {
                    LegcuffsRestraint res = new LegcuffsRestraint();
                    return res;
                } else if (stack.is(LegShacklesRestraint.ITEM)) {
                    LegShacklesRestraint res = new LegShacklesRestraint();
                    return res;
                } else if(stack.is(DuckTapeLegsRestraint.ITEM))
                {
                    DuckTapeLegsRestraint res = new DuckTapeLegsRestraint();
                    return res;
                }
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
            case CuffedMod.MODID + ":pillory":
                return new PilloryRestraint();
            case CuffedMod.MODID + ":duck_tape_head":
                return new DuckTapeHeadRestraint();
            case CuffedMod.MODID + ":duck_tape_arms":
                return new DuckTapeArmsRestraint();
            case CuffedMod.MODID + ":duck_tape_legs":
                return new DuckTapeLegsRestraint();
            case CuffedMod.MODID + ":bundle":
                return new BundleRestraint();
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
            case DuckTapeArmsRestraint.ID:
                return DuckTapeArmsRestraint.ARM_ANIMATION_FLAGS;
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
            case DuckTapeLegsRestraint.ID:
                return DuckTapeLegsRestraint.LEG_ANIMATION_FLAGS;
            default:
                return LegRestraintAnimationFlags.NONE;
        }
    }
}
