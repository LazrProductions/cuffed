package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;

import net.minecraft.world.item.ItemStack;

public class ShacklesItem extends AbstractRestraintItem {

    public ShacklesItem(Properties p) {
        super(p);
    } 

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CuffedMod.SERVER_CONFIG.RESTRAINT_DURABILITY_SHACKLES.get();
    }
}
