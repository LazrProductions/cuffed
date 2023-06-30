package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.blocks.CellDoor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CellKey extends Item {
    public CellKey(Properties p) {
        super(p);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        if (!level.isClientSide)
            if (level.getBlockState(context.getClickedPos()).getBlock() instanceof CellDoor) {
                Player player = context.getPlayer();

                if (player != null) {
                    if (player.getItemInHand(context.getHand()).getTagElement("BoundDoor") == null) {
                        BlockPos p = context.getClickedPos();
                        if (level.getBlockState(context.getClickedPos().below())
                                .getBlock() instanceof CellDoor)
                            p = context.getClickedPos().below(); // Allways make sure that the bottom half of the door
                                                                 // is
                                                                 // the part that is bound, so that any part of the door
                                                                 // can
                                                                 // be clicked
                        SetBoundDoor(context.getItemInHand(), p);
                        player.displayClientMessage(
                                Component.literal("Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()), false);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        return InteractionResult.FAIL;
    }

    public static void SetBoundDoor(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTagElement("BoundDoor").putIntArray("Position",
                new int[] { pos.getX(), pos.getY(), pos.getZ() });
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }
}
