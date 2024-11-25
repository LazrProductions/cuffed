package com.lazrproductions.cuffed.enchantment;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.restraints.RestraintAPI;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DrainEnchantment extends Enchantment {

    public DrainEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slot) {
        super(rarity, category, slot);
    }


    public int getMinCost(int level) {
        return 1;
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
	public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
		return RestraintAPI.isRestraintItem(stack);
	}
}