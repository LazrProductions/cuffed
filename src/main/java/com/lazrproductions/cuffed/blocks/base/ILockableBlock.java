package com.lazrproductions.cuffed.blocks.base;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.items.KeyItem;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface ILockableBlock {
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final BooleanProperty BOUND = BooleanProperty.create("bound");
    
    public static boolean isLocked(@Nonnull BlockState state) {
        return state.getValue(LOCKED);
    }
    
    public static void setIsLocked(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, boolean locked) {
        Level level = player.getLevel();
        if(level != null) {
            state = state.setValue(LOCKED, locked);
            level.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
        
            level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F,
                                level.getRandom().nextFloat() * 0.1F + 0.9F);
            player.displayClientMessage(
                    Component.translatable("info.lock.toggle_" + (locked ? "on" : "off")), true);
        }
    }

    public static boolean isBound(@Nonnull BlockState state) {
        return state.getValue(BOUND);
    }
    
        
    public static boolean tryToBindToKey(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
        if(!isBound(state)) {
            Level level = player.getLevel();
            if(level != null) {        
                if(KeyItem.tryToSetBoundBlock(player, stack, pos)) {
                    state = state.setValue(BOUND, true);
                    level.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
                    return true;
                }
            }
        }
        return false;
    }

    public static void bindToKey(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
        Level level = player.getLevel();
        if(level != null) {        
            if(KeyItem.tryToSetBoundBlock(player, stack, pos)) {
                state = state.setValue(BOUND, true);
                level.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
            }
        }
    }
}
