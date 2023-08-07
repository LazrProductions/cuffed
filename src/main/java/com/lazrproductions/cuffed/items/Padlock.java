package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Padlock extends Item {
    public Padlock(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        if (!level.isClientSide)
            if (level.getBlockState(context.getClickedPos()).is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
                BlockPos pos = context.getClickedPos();
                if(PadlockEntity.getLockAt(level, pos) != null)
                    return InteractionResult.FAIL;
                else{
                    PadlockEntity.getOrCreateLockAt(level, pos, context.getClickedFace());
                    player.getItemInHand(context.getHand()).shrink(1);
                    return InteractionResult.CONSUME;
                }
            }

        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }
}