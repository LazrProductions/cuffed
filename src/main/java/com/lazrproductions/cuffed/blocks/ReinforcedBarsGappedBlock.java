package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class ReinforcedBarsGappedBlock extends Block implements SimpleWaterloggedBlock {
    public static final VoxelShape SHAPE_EW = Shapes.or(
            Block.box(7, 0, 0, 9, 3, 16),
            Block.box(7, 3, 0, 9, 13, 1),
            Block.box(7, 3, 15, 9, 13, 16),
            Block.box(7, 13, 0, 9, 16, 16));
    public static final VoxelShape SHAPE_NS = Shapes.or(
        Block.box(0, 0, 7, 16, 3, 9),
        Block.box(0, 3, 7, 1, 13, 9),
        Block.box(15, 3, 7, 16, 13, 9),
        Block.box(0, 13, 7, 16, 16, 9));

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ReinforcedBarsGappedBlock(BlockBehaviour.Properties ctx) {
        super(ctx);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull CollisionContext context) {
        if (state.getValue(FACING) == Direction.EAST || state.getValue(FACING) == Direction.WEST)
            return SHAPE_EW;
        return SHAPE_NS;
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    public FluidState getFluidState(@Nonnull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Deprecated
    public BlockState updateShape(@Nonnull BlockState blockState, @Nonnull Direction direction,
            @Nonnull BlockState facingState,
            @Nonnull LevelAccessor levelAccessor, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, facingState, levelAccessor, currentPos, facingPos);
    }
}