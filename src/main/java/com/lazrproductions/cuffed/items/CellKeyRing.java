package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CellKeyRing extends CellKey {

    public CellKeyRing(Properties p) {

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
                                if (level.getBlockState(context.getClickedPos().below())
                                        .getBlock() instanceof CellDoor)
                                    p = context.getClickedPos().below(); // Allways make sure that the bottom half of
                                                                         // the
                                                                         // door
                                                                         // is
                                                                         // the part that is bound, so that any part of
                                                                         // the
                                                                         // door
                                                                         // can
                                                                         // be clicked
                                SetBoundDoor(context.getItemInHand(), p);
                                player.displayClientMessage(
                                        Component.literal("Bound key to " + p.getX() + " " + p.getY() + " " + p.getZ()),
                                        false);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
        }

        return InteractionResult.FAIL;
    }

    public static void SetBoundDoor(ItemStack stack, BlockPos pos) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("BoundDoors", 9)) {
            listtag = compoundtag.getList("BoundDoors", 10);
        } else {
            listtag = new ListTag();
        }

        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putIntArray("Position",
                new int[] { pos.getX(), pos.getY(), pos.getZ() });
        listtag.add(compoundtag1);
        compoundtag.put("BoundDoors", listtag);
    }

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
