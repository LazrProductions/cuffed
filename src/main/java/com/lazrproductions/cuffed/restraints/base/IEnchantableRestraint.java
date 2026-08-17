package com.lazrproductions.cuffed.restraints.base;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public interface IEnchantableRestraint {
    public ItemEnchantments getEnchantments();
    public void setEnchantments(ItemEnchantments enchantments);

    /** Get whether or not this restraint has the give enchantment */
    default boolean hasEnchantment(Holder<Enchantment> enchantment) {
        return getEnchantmentLevel(enchantment) > 0;
    }
    /** Get the amplifier of the given enchantment. */
    default int getEnchantmentLevel(Holder<Enchantment> enchantment) {
        return getEnchantments().getLevel(enchantment);
    }
    /** Apply an enchantment to this restraint. */
    default void enchant(Holder<Enchantment> enchantment, int value) {
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(getEnchantments());
        builder.set(enchantment, value);
        setEnchantments(builder.toImmutable());
    }
}
