package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;

import net.minecraft.world.item.ItemStack;

public class HandcuffsItem extends AbstractRestraintItem
{
    public HandcuffsItem(Properties p) {
        super(p);
    }    

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CuffedMod.SERVER_CONFIG.RESTRAINT_DURABILITY_HANDCUFFS.get();
    }
}
