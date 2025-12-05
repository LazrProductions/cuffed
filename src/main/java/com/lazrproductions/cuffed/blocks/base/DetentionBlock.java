package com.lazrproductions.cuffed.blocks.base;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class DetentionBlock extends Block {
    public DetentionBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean canDetainPlayer(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Player player, boolean ignoreState);
}
