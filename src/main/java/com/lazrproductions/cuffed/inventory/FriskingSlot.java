package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FriskingSlot extends Slot {

    public FriskingSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack p_40231_) {
        return false;
    }

    @Override
    public void onTake(@Nonnull Player player, @Nonnull ItemStack stack) {
        super.onTake(player, stack);
    }
}
