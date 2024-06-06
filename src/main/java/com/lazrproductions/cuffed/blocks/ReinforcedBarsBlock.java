package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ReinforcedBarsBlock extends IronBarsBlock {

    // 0 -> bottom, 1 -> middle, 2 -> top
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 2);

    public ReinforcedBarsBlock(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(COLUMN, 0));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, COLUMN);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        boolean up = level.getBlockState(pos.above()).is(ModBlocks.REINFORCED_BARS.get())
                || level.getBlockState(pos.above()).is(ModBlocks.CELL_DOOR.get());
        boolean down = level.getBlockState(pos.below()).is(ModBlocks.REINFORCED_BARS.get())
                || level.getBlockState(pos.below()).is(ModBlocks.CELL_DOOR.get());
        int column = 0;
        if (up && down)
            column = 1;
        if (down && !up)
            column = 2;

        return super.getStateForPlacement(ctx).setValue(COLUMN, column);
    }

    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction updateDirection,
            @Nonnull BlockState otherState,
            @Nonnull LevelAccessor level,
            @Nonnull BlockPos pos, @Nonnull BlockPos otherPos) {

        if (updateDirection == Direction.DOWN || updateDirection == Direction.UP) {
            boolean up = level.getBlockState(pos.above()).is(ModBlocks.REINFORCED_BARS.get())
                    || level.getBlockState(pos.above()).is(ModBlocks.CELL_DOOR.get());
            boolean down = level.getBlockState(pos.below()).is(ModBlocks.REINFORCED_BARS.get())
                    || level.getBlockState(pos.below()).is(ModBlocks.CELL_DOOR.get());
            int column = 0;
            if (up && down)
                column = 1;
            if (down && !up)
                column = 2;
            return super.updateShape(state.setValue(COLUMN, column), updateDirection, otherState, level, pos, otherPos);
        }

        return super.updateShape(state, updateDirection, otherState, level, pos, otherPos);
    }

    public int getColumn(BlockState state) {
        return state.getValue(COLUMN);
    }
}
