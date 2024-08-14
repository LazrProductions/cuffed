package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.SafeBlock;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class KeyRingItem extends Item {

    public static final String TAG_BOUND_BLOCKS = "BoundBlocks";
    public static final String TAG_KEYS = "Keys";

    public KeyRingItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = player.getItemInHand(context.getHand());
            if (!level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND)
                if (level.getBlockState(context.getClickedPos()).getBlock() instanceof CellDoor) {

                    if (player.getItemInHand(context.getHand()).getTagElement(TAG_BOUND_BLOCKS) == null) {
                        if (canBindBlock(stack)) {
                            if (!hasBoundBlockAt(context.getItemInHand(), context.getClickedPos())) {
                                BlockPos p = context.getClickedPos();
                                if (level.getBlockState(context.getClickedPos().below()).getBlock() instanceof CellDoor)
                                    p = context.getClickedPos().below();

                                addBoundBlock(context.getItemInHand(), p);

                                player.awardStat(Stats.ITEM_USED.get(ModItems.KEY_RING.get()), 1);

                                if (player.getLevel().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                                    player.displayClientMessage(
                                            Component.literal(
                                                    "Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()),
                                            false);
                                else
                                    player.displayClientMessage(Component.literal("Bound key to ")
                                            .append(player.getLevel().getBlockState(p).getBlock().getName()), false);
                                player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                } else if (level.getBlockState(context.getClickedPos()).getBlock() instanceof SafeBlock) {

                    if (player.getItemInHand(context.getHand()).getTagElement(TAG_BOUND_BLOCKS) == null) {
                        if (canBindBlock(stack)) {
                            if (!hasBoundBlockAt(context.getItemInHand(), context.getClickedPos())) {
                                BlockPos p = context.getClickedPos();
   
                                addBoundBlock(context.getItemInHand(), p);

                                player.awardStat(Stats.ITEM_USED.get(ModItems.KEY_RING.get()), 1);

                                if (player.getLevel().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                                    player.displayClientMessage(
                                            Component.literal(
                                                    "Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()),
                                            false);
                                else
                                    player.displayClientMessage(Component.literal("Bound key to ")
                                            .append(player.getLevel().getBlockState(p).getBlock().getName()), false);
                                player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
        }

        return InteractionResult.FAIL;
    }

    /**
     * Add an additional bound door to the given ItemStack.
     * 
     * @param stack (ItemStack) The item stack to bind to.
     * @param pos   (BlockPos) The position of the door to bind.
     */
    public static void addBoundBlock(ItemStack stack, BlockPos pos) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains(TAG_BOUND_BLOCKS, 9))
            listtag = compoundtag.getList(TAG_BOUND_BLOCKS, 10);
        else
            listtag = new ListTag();

        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putIntArray(KeyItem.TAG_POSITION, new int[] { pos.getX(), pos.getY(), pos.getZ() });
        listtag.add(compoundtag1);
        compoundtag.put(TAG_BOUND_BLOCKS, listtag);
    }

    /**
     * Attempt to add an additional bound door at a position to the given key.
     * 
     * @param player (Player) The player setting the key.
     * @param stack  (ItemStack) The ItemStack to change
     * @param pos    (BlockPos) The door to set as the bound door.
     * @return (boolean) True if bound door has been set to a new door, false if the
     *         key has already been bound.
     */
    public static boolean tryToAddBoundBlock(Player player, ItemStack stack, BlockPos pos) {
        if (stack.getTagElement(TAG_BOUND_BLOCKS) == null) {
            if (canBindBlock(stack)) {
                if (!hasBoundBlockAt(stack, pos)) {
                    addBoundBlock(stack, pos);
                    if (player.getLevel().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                        player.displayClientMessage(
                                Component.literal("Bound key to " + pos.getX() + " " + pos.getY() + " " + pos.getZ()),
                                false);
                    else
                        player.displayClientMessage(Component.literal("Bound key to ")
                                .append(player.getLevel().getBlockState(pos).getBlock().getName()), false);
                    player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove a bound door from the given key.
     * 
     * @param stack (ItemStack) The item stack to change.
     * @param pos   (BlockPos) The position of the door to remove.
     */
    public static void removeBoundDoorAt(ItemStack stack, BlockPos pos) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains(TAG_BOUND_BLOCKS, 9)) {
            listtag = compoundtag.getList(TAG_BOUND_BLOCKS, 10);
        } else {
            listtag = new ListTag();
        }

        int index = getBoundBlockIndex(stack, pos);
        if (index >= 0)
            listtag.remove(index);
        compoundtag.put(TAG_BOUND_BLOCKS, listtag);
    }

    /**
     * Get whether or not the given key has bound a door at a position.
     * 
     * @param stack (ItemStack) The item stack to check.
     * @param pos   (BlockPos) The position of the door to get;
     * @return (boolean) True if this key ring has bound the given door.
     */
    public static boolean hasBoundBlockAt(ItemStack stack, BlockPos pos) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return false;

        if (compoundTag.contains(TAG_BOUND_BLOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_BLOCKS, 10);
            for (int i = 0; i < boundPos.size(); i++) {
                int x = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[0];
                int y = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[1];
                int z = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[2];
                if (pos.getX() == x && pos.getY() == y && pos.getZ() == z)
                    return true;
            }
        }
        return false;
    }

    /**
     * Get the index of a bound door on the given key.
     * 
     * @param stack (ItemStack) The item stack to check.
     * @param pos   (BlockPos) The the position of the bound door.
     * @return (int) The index of the bound door, -1 if the given door is not bound.
     */
    public static int getBoundBlockIndex(ItemStack stack, BlockPos pos) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return -1;

        if (compoundTag.contains(TAG_BOUND_BLOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_BLOCKS, 10);
            for (int i = 0; i < boundPos.size(); i++) {
                int x = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[0];
                int y = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[1];
                int z = boundPos.getCompound(i).getIntArray(KeyItem.TAG_POSITION)[2];
                if (pos.getX() == x && pos.getY() == y && pos.getZ() == z)
                    return i;
            }
        }
        return -1;
    }

    /**
     * Get whether or not the given item can bind a new door.
     * 
     * @param stack (ItemStack) The item stack to check.
     * @return (boolean) True is there is room on the key ring to bind more doors.
     */
    public static boolean canBindBlock(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return true;

        int bindings = 0;
        int keys = 0;
        if (compoundTag.contains(TAG_BOUND_BLOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_BLOCKS, 10);

            bindings = boundPos.size();

            var tag = stack.getTag();
            if (tag != null && tag.contains(TAG_KEYS))
                keys = tag.getInt(TAG_KEYS);
        } else {
            return true;
        }

        if (bindings < keys)
            return true;
        return false;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level pLevel,
            @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        int amount = 0;
        var tag = stack.getTag();
        if (tag != null && tag.contains(TAG_KEYS))
            amount = tag.getInt(TAG_KEYS);

        pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", amount)
                .withStyle(ChatFormatting.GRAY));

        int bindings = 0;
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            if (compoundTag.contains(TAG_BOUND_BLOCKS, 9)) {
                ListTag boundPos = compoundTag.getList(TAG_BOUND_BLOCKS, 10);
                bindings = boundPos.size();
            }
        }
        if (bindings == amount)
            pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", bindings)
                    .withStyle(ChatFormatting.GRAY));
        else
            pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", bindings)
                    .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        itemstack.getOrCreateTag().putInt(TAG_KEYS, 2);
        return itemstack;
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int num,
            boolean boo) {
        if (stack.getTag() == null)
            stack.getOrCreateTag().putInt(TAG_KEYS, 1);
        super.inventoryTick(stack, level, entity, num, boo);
    }
}
