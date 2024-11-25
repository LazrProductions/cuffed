package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.entity.LockableBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class KeyItem extends Item {

    public static final String TAG_ID = "Id";

    public KeyItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        if (!level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND) {
            Player player = context.getPlayer();
            BlockState state = level.getBlockState(context.getClickedPos());
            if (player != null) {
                ItemStack stack = player.getItemInHand(context.getHand());
                if (state.getBlock() instanceof CellDoor) {
                    BlockPos bottomPos = context.getClickedPos();
                    if(state.getValue(CellDoor.HALF) == DoubleBlockHalf.UPPER) {
                        bottomPos = bottomPos.below();
                        state = level.getBlockState(bottomPos);
                    }

                    if (level.getBlockEntity(bottomPos) instanceof LockableBlockEntity lockable) {
                        if (!isBoundToALock(stack) && !lockable.hasBeenBound()) {
                            if (tryToSetBoundId(player, stack, lockable.getLockId(), "Cell Door")) {
                                lockable.bind();
                                player.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()), 1);
                                return InteractionResult.SUCCESS;
                            } else
                                return InteractionResult.FAIL;
                        }
                    }
                } else if (level.getBlockEntity(context.getClickedPos()) instanceof LockableBlockEntity lockable) {
                    if (!isBoundToALock(stack) && !lockable.hasBeenBound()) {
                        if (tryToSetBoundId(player, stack, lockable.getLockId(), lockable.getLockName())) {
                            lockable.bind();
                            player.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()), 1);
                            return InteractionResult.SUCCESS;
                        } else
                            return InteractionResult.FAIL;
                    }

                } else if (level.getBlockEntity(context.getClickedPos()) instanceof SafeBlockEntity safe) {
                    if (!isBoundToALock(stack) && !safe.hasBeenBound()) {
                        if (tryToSetBoundId(player, stack, safe.getLockId(), "block.cuffed.safe")) {
                            safe.bind();
                            player.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()), 1);
                            return InteractionResult.SUCCESS;
                        } else
                            return InteractionResult.FAIL;
                    }

                }
            }
        }

        return InteractionResult.FAIL;
    }

    public static boolean tryToSetBoundId(Player player, ItemStack stack, UUID id, String lockName) {
        if (!stack.getOrCreateTag().contains(TAG_ID)) {
            setBoundId(stack, id);
            if (!player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO))
                player.displayClientMessage(Component.translatable("item.cuffed.key.info.bound").append(Component.literal(""+id)), false);
            else
                player.displayClientMessage(Component.translatable("item.cuffed.key.info.bound").append(Component.translatable(lockName)), false);
            player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    public static void setBoundId(ItemStack stack, UUID id) {
        stack.getOrCreateTag().putUUID(TAG_ID, id);
    }

    public static void removeBoundLock(ItemStack stack) {
        if (stack.getOrCreateTag().contains(TAG_ID))
            stack.removeTagKey(TAG_ID);
    }

    @Nullable
    public static UUID getBoundLock(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_ID))
            return null;
        return tag.getUUID(TAG_ID);
    }

    public static boolean isBoundToLock(@Nonnull ItemStack stack, @Nonnull UUID id) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_ID))
            return false;
        return tag.getUUID(TAG_ID).equals(id);
    }

    public static boolean isBoundToALock(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(TAG_ID);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level pLevel,
            @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        if (stack.getOrCreateTag().contains(TAG_ID))
            pTooltipComponents.add(Component.translatable("item.cuffed.key.description.bound").withStyle(ChatFormatting.DARK_GRAY));
    }
}
