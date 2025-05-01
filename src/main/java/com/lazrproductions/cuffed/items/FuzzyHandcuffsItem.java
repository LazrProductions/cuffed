package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.base.AbstractArmRestraintItem;

import net.minecraft.world.item.ItemStack;

public class FuzzyHandcuffsItem extends AbstractArmRestraintItem
{
    public FuzzyHandcuffsItem(Properties p) {
        super(p);
    }     

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CuffedMod.SERVER_CONFIG.RESTRAINT_DURABILITY_FUZZY_HANDCUFFS.get();
    }
}
