package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Key extends Item {
    public Key(Properties p) {
        super(p);
    }

    @Override
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
                            p = context.getClickedPos().below();

                        SetBoundDoor(context.getItemInHand(), p);
                        if (player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                            player.displayClientMessage(Component.literal("Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()), false);
                        else
                            player.displayClientMessage(Component.literal("Bound key to ").append(player.level().getBlockState(p).getBlock().getName()), false);
                        player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

        return InteractionResult.FAIL;
    }

    /**
     * Attempt to set the bound door of a key to the given position.
     * 
     * @param player (Player) The player setting the key.
     * @param stack  (ItemStack) The ItemStack to change
     * @param pos    (BlockPos) The door to set as the bound door.
     * @return (boolean) True if bound door has been set to a new door, false if the
     *         key has already been bound.
     */
    public static boolean TryToSetBoundDoor(Player player, ItemStack stack, BlockPos pos) {
        if (stack.getTagElement("BoundDoor") == null) {
            SetBoundDoor(stack, pos);
            if (!player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                player.displayClientMessage(Component.literal("Bound key to " + pos.getX() + " " + pos.getY() + " " + pos.getZ()), false);
            else
                player.displayClientMessage(Component.literal("Bound key to ").append(player.level().getBlockState(pos).getBlock().getName()), false);
            player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    /**
     * Set the bound door of a key to the given position
     * 
     * @param stack (ItemStack) The stack to set,
     * @param pos   (BlockPos) The position to set as the bound door.
     */
    public static void SetBoundDoor(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTagElement("BoundDoor").putIntArray("Position",
                new int[] { pos.getX(), pos.getY(), pos.getZ() });
    }

    /**
     * Reset bound door for the given key.
     * 
     * @param stack (ItemStack) The item stack of the key to reset.
     */
    public static void RemoveBoundDoor(ItemStack stack) {
        if (stack.getTagElement("BoundDoor") != null)
            stack.removeTagKey("BoundDoor");
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        if (stack.getTagElement("BoundDoor") != null)
            pTooltipComponents.add(Component.literal("§8Bound"));
    }
}
