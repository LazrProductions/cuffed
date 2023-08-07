package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class KeyRing extends Item {

    public KeyRing(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = player.getItemInHand(context.getHand());
            if (!level.isClientSide)
                if (level.getBlockState(context.getClickedPos()).getBlock() instanceof CellDoor) {

                    if (player.getItemInHand(context.getHand()).getTagElement("BoundDoors") == null) {
                        if (CanBindDoor(stack)) {
                            if (!HasBoundDoorAt(context.getItemInHand(), context.getClickedPos())) {
                                BlockPos p = context.getClickedPos();
                                if (level.getBlockState(context.getClickedPos().below()).getBlock() instanceof CellDoor)
                                    p = context.getClickedPos().below();

                                AddBoundDoor(context.getItemInHand(), p);
                                if (player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                                    player.displayClientMessage(
                                            Component.literal(
                                                    "Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()),
                                            false);
                                else
                                    player.displayClientMessage(Component.literal("Bound key to ")
                                            .append(player.level().getBlockState(p).getBlock().getName()), false);
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
    public static void AddBoundDoor(ItemStack stack, BlockPos pos) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("BoundDoors", 9))
            listtag = compoundtag.getList("BoundDoors", 10);
        else
            listtag = new ListTag();

        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putIntArray("Position", new int[] { pos.getX(), pos.getY(), pos.getZ() });
        listtag.add(compoundtag1);
        compoundtag.put("BoundDoors", listtag);
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
    public static boolean TryToAddBoundDoor(Player player, ItemStack stack, BlockPos pos) {
        if (stack.getTagElement("BoundDoors") == null) {
            if (CanBindDoor(stack)) {
                if (!HasBoundDoorAt(stack, pos)) {
                    AddBoundDoor(stack, pos);
                    if (player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                        player.displayClientMessage(
                                Component.literal("Bound key to " + pos.getX() + " " + pos.getY() + " " + pos.getZ()),
                                false);
                    else
                        player.displayClientMessage(Component.literal("Bound key to ")
                                .append(player.level().getBlockState(pos).getBlock().getName()), false);
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
    public static void RemoveBoundDoorAt(ItemStack stack, BlockPos pos) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("BoundDoors", 9)) {
            listtag = compoundtag.getList("BoundDoors", 10);
        } else {
            listtag = new ListTag();
        }

        int index = GetBoundDoorIndex(stack, pos);
        if (index >= 0)
            listtag.remove(index);
        compoundtag.put("BoundDoors", listtag);
    }

    /**
     * Get whether or not the given key has bound a door at a position.
     * 
     * @param stack (ItemStack) The item stack to check.
     * @param pos   (BlockPos) The position of the door to get;
     * @return (boolean) True if this key ring has bound the given door.
     */
    public static boolean HasBoundDoorAt(ItemStack stack, BlockPos pos) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return false;

        if (compoundTag.contains("BoundDoors", 9)) {
            ListTag boundPos = compoundTag.getList("BoundDoors", 10);
            for (int i = 0; i < boundPos.size(); i++) {
                int x = boundPos.getCompound(i).getIntArray("Position")[0];
                int y = boundPos.getCompound(i).getIntArray("Position")[1];
                int z = boundPos.getCompound(i).getIntArray("Position")[2];
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
    public static int GetBoundDoorIndex(ItemStack stack, BlockPos pos) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return -1;

        if (compoundTag.contains("BoundDoors", 9)) {
            ListTag boundPos = compoundTag.getList("BoundDoors", 10);
            for (int i = 0; i < boundPos.size(); i++) {
                int x = boundPos.getCompound(i).getIntArray("Position")[0];
                int y = boundPos.getCompound(i).getIntArray("Position")[1];
                int z = boundPos.getCompound(i).getIntArray("Position")[2];
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
    public static boolean CanBindDoor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return true;

        int bindings = 0;
        int keys = 0;
        if (compoundTag.contains("BoundDoors", 9)) {
            ListTag boundPos = compoundTag.getList("BoundDoors", 10);

            bindings = boundPos.size();

            var tag = stack.getTag();
            if (tag != null && tag.contains("Keys"))
                keys = tag.getInt("Keys");
        } else {
            return true;
        }

        if (bindings < keys)
            return true;
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        int amount = 0;
        var tag = stack.getTag();
        if (tag != null && tag.contains("Keys"))
            amount = tag.getInt("Keys");

        pTooltipComponents.add(Component.literal("Keys: " + amount));

        int bindings = 0;
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            if (compoundTag.contains("BoundDoors", 9)) {
                ListTag boundPos = compoundTag.getList("BoundDoors", 10);
                bindings = boundPos.size();
            }
        }
        if (bindings == amount)
            pTooltipComponents.add(Component.literal("§8Bound Keys: " + bindings));
        else
            pTooltipComponents.add(Component.literal("Bound Keys: " + bindings));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        itemstack.getOrCreateTag().putInt("Keys", 2);
        return itemstack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int num, boolean boo) {
        if (stack.getTag() == null)
            stack.getOrCreateTag().putInt("Keys", 1);
        super.inventoryTick(stack, level, entity, num, boo);

    }
}
