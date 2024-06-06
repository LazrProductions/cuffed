package com.lazrproductions.cuffed.restraints.base;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IEnchantableRestraint {
    public ListTag getEnchantments();
    public void setEnchantments(ListTag tag);

    /** Get whether or not this restraint has the give enchantment */
    public boolean hasEnchantment(Enchantment enchantment);
    /** Get the amplifier of the given enchantment. */
    public int getEnchantmentLevel(Enchantment enchantment);
    /** Apply an enchantment to this restraint. */
    public void enchant(Enchantment enchantment, int value);
}
