package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.base.ILockableBlock;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;

public class KeyItem extends Item {

    public static final String TAG_BOUND_BLOCK = "BoundBlock";
    public static final String TAG_POSITION = "Position";

    public KeyItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        if (!level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND)
            if(level.getBlockState(context.getClickedPos()).getBlock() instanceof ILockableBlock) {
                Player player = context.getPlayer();
                if (player != null) {
                    if (player.getItemInHand(context.getHand()).getTagElement(TAG_BOUND_BLOCK) == null) {
                        BlockPos p = context.getClickedPos();
                        if (level.getBlockState(context.getClickedPos().below()).getBlock() instanceof DoorBlock)
                            p = context.getClickedPos().below();

                        if(ILockableBlock.tryToBindToKey(player, level.getBlockState(p), p, player.getItemInHand(context.getHand()))) {
                            player.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()), 1);
                            return InteractionResult.SUCCESS;
                        } else
                            return InteractionResult.FAIL;
                    }
                }
            } 

        return InteractionResult.FAIL;
    }

    public static boolean tryToSetBoundBlock(Player player, ItemStack stack, BlockPos pos) {
        if (stack.getTagElement(TAG_BOUND_BLOCK) == null) {
            setBoundBlock(stack, pos);
            if (!player.getLevel().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                player.displayClientMessage(Component.literal("Bound key to " + pos.getX() + " " + pos.getY() + " " + pos.getZ()), false);
            else
                player.displayClientMessage(Component.literal("Bound key to ").append(player.getLevel().getBlockState(pos).getBlock().getName()), false);
            player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
            return true;
        }
        return false;
    }


    public static void setBoundBlock(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTagElement(TAG_BOUND_BLOCK).putIntArray(TAG_POSITION,
                new int[] { pos.getX(), pos.getY(), pos.getZ() });
    }


    public static void removeBoundBlock(ItemStack stack) {
        if (stack.getTagElement(TAG_BOUND_BLOCK) != null)
            stack.removeTagKey(TAG_BOUND_BLOCK);
    }

    @Nullable
    public static BlockPos getBoundBlock(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(TAG_BOUND_BLOCK);
        if(tag == null)
            return null;
        int[] pos = tag.getIntArray(TAG_POSITION);
        return new BlockPos(pos[0], pos[1], pos[2]);
    }
    public static boolean isBoundToBlock(@Nonnull ItemStack stack, @Nonnull BlockPos checkPos) {
        CompoundTag tag = stack.getTagElement(TAG_BOUND_BLOCK);
        if(tag == null)
            return false;
        int[] _p = tag.getIntArray(TAG_POSITION);
        BlockPos pos = new BlockPos(_p[0], _p[1], _p[2]);
        return checkPos.equals(pos);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        if (stack.getTagElement(TAG_BOUND_BLOCK) != null)
            pTooltipComponents.add(Component.translatable("item.cuffed.key.description.bound").withStyle(ChatFormatting.DARK_GRAY));
    }
}
