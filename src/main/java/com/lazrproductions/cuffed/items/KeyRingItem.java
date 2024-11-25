package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.SafeBlock;
import com.lazrproductions.cuffed.blocks.entity.LockableBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class KeyRingItem extends Item {

    public static final String TAG_BOUND_LOCKS = "BoundLocks";
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
        BlockState state = level.getBlockState(context.getClickedPos());
        if (player != null) {
            ItemStack stack = player.getItemInHand(context.getHand());
            if (!level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND) {
                if (canBindLock(stack)) {
                    if (state.getBlock() instanceof CellDoor) {
                        BlockPos bottomPos = context.getClickedPos();
                        if (state.getValue(CellDoor.HALF) == DoubleBlockHalf.UPPER) {
                            bottomPos = bottomPos.below();
                            state = level.getBlockState(bottomPos);
                        }

                        if (level.getBlockEntity(bottomPos) instanceof LockableBlockEntity lockable) {
                            if(!lockable.hasBeenBound()) {
                                if (tryToAddBoundId(player, stack, lockable.getLockId(), "block.cuffed.cell_door")) {
                                    lockable.bind();
                                    player.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()), 1);
                                    return InteractionResult.SUCCESS;
                                } else
                                    return InteractionResult.FAIL;
                            }
                        }
                    } else if (level.getBlockEntity(context.getClickedPos()) instanceof LockableBlockEntity lockable) {
                        if(!lockable.hasBeenBound()) {
                            if(tryToAddBoundId(player, stack, lockable.getLockId(), lockable.getLockName())) {
                                lockable.bind();
                                player.awardStat(Stats.ITEM_USED.get(ModItems.KEY_RING.get()), 1);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }else if (state.getBlock() instanceof SafeBlock) {
                        if (level.getBlockEntity(context.getClickedPos()) instanceof SafeBlockEntity safe) {
                            if(!safe.hasBeenBound()) {
                                if(tryToAddBoundId(player, stack, safe.getLockId(), "block.cuffed.safe")) {
                                    safe.bind();
                                    player.awardStat(Stats.ITEM_USED.get(ModItems.KEY_RING.get()), 1);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
        }

        return InteractionResult.FAIL;
    }

    public static void addBoundId(ItemStack stack, UUID id) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains(TAG_BOUND_LOCKS, 9))
            listtag = compoundtag.getList(TAG_BOUND_LOCKS, 10);
        else
            listtag = new ListTag();

        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putUUID(KeyItem.TAG_ID, id);
        listtag.add(compoundtag1);
        compoundtag.put(TAG_BOUND_LOCKS, listtag);
    }

    public static void addKey(ItemStack stack, ItemStack key) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains(TAG_BOUND_LOCKS, 9))
            listtag = compoundtag.getList(TAG_BOUND_LOCKS, 10);
        else
            listtag = new ListTag();

        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putUUID(KeyItem.TAG_ID, key.getOrCreateTag().getUUID(KeyItem.TAG_ID));
        if(key.getOrCreateTag().contains("display"))
            compoundtag1.putString(KeyMoldItem.TAG_NAME, key.getOrCreateTag().getCompound("display").getString("Name"));
        listtag.add(compoundtag1);
        compoundtag.put(TAG_BOUND_LOCKS, listtag);
    }

    public static boolean tryToAddBoundId(Player player, ItemStack stack, UUID id, String lockName) {
        if (canBindLock(stack)) {
            if (!hasBoundId(stack, id)) {
                addBoundId(stack, id);
                if (player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                    player.displayClientMessage(
                            Component.translatable("item.cuffed.key.info.bound").append(Component.literal(""+id)), false);
                else
                    player.displayClientMessage(Component.translatable("item.cuffed.key.info.bound").append(Component.translatable(lockName)), false);
                player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
                return true;
            }
        }
        
        return false;
    }

    public static void removeBoundId(ItemStack stack, UUID id) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains(TAG_BOUND_LOCKS, 9)) {
            listtag = compoundtag.getList(TAG_BOUND_LOCKS, 10);
        } else {
            listtag = new ListTag();
        }

        int index = getBoundIdIndex(stack, id);
        if (index >= 0)
            listtag.remove(index);
        compoundtag.put(TAG_BOUND_LOCKS, listtag);
    }

    public static boolean hasBoundId(ItemStack stack, UUID id) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag == null)
            return false;

        if (compoundTag.contains(TAG_BOUND_LOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_LOCKS, 10);
            for (int i = 0; i < boundPos.size(); i++)
                if (boundPos.getCompound(i).getUUID(KeyItem.TAG_ID).equals(id))
                    return true;
        }
        return false;
    }

    public static int getBoundIdIndex(ItemStack stack, UUID id) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return -1;

        if (compoundTag.contains(TAG_BOUND_LOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_LOCKS, 10);
            for (int i = 0; i < boundPos.size(); i++)
                if (boundPos.getCompound(i).getUUID(KeyItem.TAG_ID) == id)
                    return i;
        }
        return -1;
    }

    public static boolean canBindLock(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null)
            return true;

        int bindings = 0;
        int keys = 0;
        if (compoundTag.contains(TAG_BOUND_LOCKS, 9)) {
            ListTag boundPos = compoundTag.getList(TAG_BOUND_LOCKS, 10);

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
            if (compoundTag.contains(TAG_BOUND_LOCKS, 9)) {
                ListTag boundPos = compoundTag.getList(TAG_BOUND_LOCKS, 10);
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
