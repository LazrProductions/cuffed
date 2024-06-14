package com.lazrproductions.cuffed.items.base;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModEnchantments;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class AbstractRestraintItem extends Item {
    public AbstractRestraintItem(Properties p) {
        super(p);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.UNBREAKING)
            return true;
        if (enchantment == Enchantments.BINDING_CURSE)
            return true;
        if (enchantment == ModEnchantments.IMBUE.get())
            return true;
        if (enchantment == ModEnchantments.FAMINE.get())
            return true;
        if (enchantment == ModEnchantments.SHROUD.get())
            return true;
        if (enchantment == ModEnchantments.EXHAUST.get())
            return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }
}
