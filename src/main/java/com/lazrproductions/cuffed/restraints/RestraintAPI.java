package com.lazrproductions.cuffed.restraints;

import java.util.ArrayList;
import java.util.List;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
public class RestraintAPI {
    /**
     * Where all restraint registries found by Cuffed are kept and accessed.
     */
    public static final class Registries {

        @Deprecated
        public static void register(Object registry) {
            // No-op in 1.21.1: other mods should register their restraints to ModRestraints.RESTRAINTS DeferredRegister.
        }

        /**
         * Get whether or not Cuffed has found the restraint with the given key.
         * 
         * @param key The key to check for
         */
        public static boolean containsKey(ResourceLocation key) {
            return com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null && com.lazrproductions.cuffed.init.ModRestraints.REGISTRY.containsKey(key);
        }

        /**
         * Get whether or not Cuffed has found the given restraint.
         * 
         * @param restraint The restraint to check for
         */
        public static boolean containsValue(AbstractRestraint restraint) {
            return com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null && com.lazrproductions.cuffed.init.ModRestraints.REGISTRY.getKey(restraint) != null;
        }

        /**
         * Get the restraint with the given key
         * 
         * @param key The key of the restraint to get
         */
        public static AbstractRestraint get(ResourceLocation key) {
            return com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null ? com.lazrproductions.cuffed.init.ModRestraints.REGISTRY.get(key) : null;
        }

        /**
         * Get the restraint from the registry with the given restraint item.
         * 
         * @param restraintItem The item to get a restraint from
         * @param type The type of restraint to look for
         */
        public static AbstractRestraint get(Item restraintItem, RestraintType type) {
            List<Pair<Item, AbstractRestraint>> pairs = getAllRestraintItemsAndTheirRestraints();
            for (Pair<Item, AbstractRestraint> pair : pairs) {
                if (pair.getFirst().equals(restraintItem))
                    if(pair.getSecond().getType() == type)
                        return pair.getSecond();
            }
            return null;
        }

        /**
         * Get all of the restraints found by Cuffed.
         */
        public static List<AbstractRestraint> getAllRestraints() {
            ArrayList<AbstractRestraint> res = new ArrayList<>();
            if (com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null) {
                for (AbstractRestraint ent : com.lazrproductions.cuffed.init.ModRestraints.REGISTRY) {
                    res.add(ent);
                }
            }
            return res;
        }

        /**
         * Get all of the restraints found by Cuffed.
         */
        public static List<Item> getAllRestraintItems() {
            ArrayList<Item> res = new ArrayList<>();
            if (com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null) {
                for (AbstractRestraint ent : com.lazrproductions.cuffed.init.ModRestraints.REGISTRY) {
                    res.add(ent.getItem());
                }
            }
            return res;
        }

        /**
         * Get all of the restraints found by Cuffed and return them and their restraint
         * item as pairs.
         */
        public static List<Pair<Item, AbstractRestraint>> getAllRestraintItemsAndTheirRestraints() {
            ArrayList<Pair<Item, AbstractRestraint>> pairs = new ArrayList<>();
            if (com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null) {
                for (AbstractRestraint ent : com.lazrproductions.cuffed.init.ModRestraints.REGISTRY) {
                    pairs.add(new Pair<>(ent.getItem(), ent));
                }
            }
            return pairs;
        }

        /**
         * How many registries the Cuffed has found to be Restraint registries.
         */
        public static int size() {
            return com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null ? 1 : 0;
        }
        
        /**
         * Get the total amount of restraints registered in all registries.
         */
        public static int total() {
            return com.lazrproductions.cuffed.init.ModRestraints.REGISTRY != null ? com.lazrproductions.cuffed.init.ModRestraints.REGISTRY.size() : 0;
        }

        public static class RestraintRegistryContainsKeyException extends RuntimeException {
            public RestraintRegistryContainsKeyException() {
                super("The Restraint Registry already contains this key!");
            }

            public RestraintRegistryContainsKeyException(ResourceLocation key) {
                super("The Restraint Registry already contains the key " + key);
            }
        }

        public static class RestraintRegistryContainsRestraintException extends RuntimeException {
            public RestraintRegistryContainsRestraintException() {
                super("The Restraint Registry already contains this restraint!");
            }

            public RestraintRegistryContainsRestraintException(ResourceLocation key) {
                super("The Restraint Registry already contains the restraint " + key);
            }
        }

        public static class ConflictingRestraintItemAndTypeException extends RuntimeException {
            public ConflictingRestraintItemAndTypeException() {
                super("The Restraint Registry already contains a restraint for this item with the same restraint type!");

            }

            public ConflictingRestraintItemAndTypeException(ResourceLocation key, RestraintType type) {
                super("The Restraint Registry already contains a restraint for " + key + " with the same restraint type of " + type);
            }
        }
    }

    /**
     * Get a restraint from it's serialized data.
     * 
     * @param tag The serialized data of the restraint to get.
     * @return A new restraint serialized from the given data.
     */
    public static AbstractRestraint getRestraintFromTag(net.minecraft.core.HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("Id")) {
            AbstractRestraint r = getNewRestraintByKey(ResourceLocation.bySeparator(tag.getString("Id"),':'));
            if (r != null) {
                r.deserializeNBT(provider, tag);
                return r;
            }
        }
        return null;
    }

    /**
     * Get the base class for the restraint with the given key
     * 
     * @param key The key of the restraint to get.
     * @return The base class for the restraint with the given, this is not to be
     *         assigned as the worn restraint of any player before the data is
     *         modified or deserialized.
     */
    public static AbstractRestraint getNewRestraintByKey(ResourceLocation key) {
        if (Registries.containsKey(key))
            return Registries.get(key);
        return null;
    }

    /**
     * Get a new restraint instance from it's item.
     * 
     * @param stack  The itemstack to get a new restraint from
     * @param type   The RestraintType to get
     * @param player The player being restrained
     * @param captor The player who is applying the restraints
     * @return The new restraint instance from the item given
     */
    public static AbstractRestraint getRestraintFromStack(ItemStack stack, RestraintType type, ServerPlayer player, ServerPlayer captor) {
        if (stack.getItem() != null) {
            var restraintBase = Registries.get(stack.getItem(), type);
            if(restraintBase != null) {
                String className = restraintBase.getClass().getName();

                try {
                    Object o = Class.forName(className).getConstructor(ItemStack.class, ServerPlayer.class, ServerPlayer.class).newInstance(stack, player, captor);
                    if(o instanceof AbstractRestraint r)
                        return r;
                } catch (Exception e) {
                    CuffedMod.LOGGER.info("Error getting new instance of restraint for stack " + stack.getHoverName().getString());
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Get whether or not the given itemstack is a restraint item.
     * @param stack The stack to check
     */
    public static boolean isRestraintItem(ItemStack stack) {
        return IsHeadRestraintItem(stack) || IsArmRestraintItem(stack) || IsLegRestraintItem(stack);
    }
    
    /**
     * Get whether or not the given itemstack is a head restraint item.
     * @param stack The stack to check
     */
    public static boolean IsHeadRestraintItem(ItemStack stack) {
        AbstractRestraint r = Registries.get(stack.getItem(), RestraintType.Head);
        return r != null && r.getType() == RestraintType.Head;
    }

    /**
     * Get whether or not the given itemstack is a head restraint item.
     * @param stack The stack to check
     */
    public static boolean IsArmRestraintItem(ItemStack stack) {
        AbstractRestraint r = Registries.get(stack.getItem(),RestraintType.Arm);
        return r != null && r.getType() == RestraintType.Arm;
    }
    
    /**
     * Get whether or not the given itemstack is a leg restraint item.
     * @param stack The stack to check
     */
    public static boolean IsLegRestraintItem(ItemStack stack) {
        AbstractRestraint r = Registries.get(stack.getItem(), RestraintType.Leg);
        return r != null && r.getType() == RestraintType.Leg;
    }

    /**
     * Get whether or not the given item stack can be equipped as a restraint.
     * @param stack The item stack of the restraint to check
     * @param type The type of restraint to check for
     * @param player The player being restrained
     * @param captor The player restraining
     * @return Whether or not the restraint item can be equipped
     */
    public static boolean canEquipRestriantItem(ItemStack stack, RestraintType type, ServerPlayer player, ServerPlayer captor) {
        var r = Registries.get(stack.getItem(), type);
        return r != null ? r.canEquipRestraintItem(stack, player, captor) : false;
    }

    /**
     * Get the arm animation flags by its key.
     * @param key The key to check
     */
    public static ArmRestraintAnimationFlags getArmAnimationFlagByKey(ResourceLocation key) {
        var r = Registries.get(key);
        return r != null ? r.getArmAnimationFlags() : ArmRestraintAnimationFlags.NONE;
    }
    
    /**
     * Get the leg animation flags by its key.
     * @param key The key to check
     */
    public static LegRestraintAnimationFlags getLegAnimationFlagByKey(ResourceLocation key) {
        var r = Registries.get(key);
        return r != null ? r.getLegAnimationFlags() : LegRestraintAnimationFlags.NONE;
    }
}
