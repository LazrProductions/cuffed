package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.base.PosterType;
import com.lazrproductions.cuffed.init.ModBlockProperties;
import com.lazrproductions.cuffed.items.PosterBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PosterBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<PosterType> POSTER_TYPE = ModBlockProperties.POSTER_TYPE;

    protected static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 15, 16, 16, 16);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 1);
    protected static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 1, 16, 16);
    protected static final VoxelShape SHAPE_WEST = Block.box(15, 0, 0, 16, 16, 16);

    public PosterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER).setValue(POSTER_TYPE, PosterType.NONE));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        if (blockpos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(blockpos.above()).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HALF,
                    DoubleBlockHalf.LOWER);
        } else if (blockpos.getY() > level.getMinBuildHeight() + 1
                && level.getBlockState(blockpos.below()).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HALF,
                    DoubleBlockHalf.UPPER);
        } else {
            return null;
        }
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos,
            @Nonnull CollisionContext ctx) {
        switch(state.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_EAST;
        }
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos,
            @Nonnull CollisionContext ctx) {
        return getShape(state, getter, pos, ctx);
    }

    @Override
    public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation r) {
        return state.setValue(FACING, r.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror m) {
        return state.rotate(m.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nullable LivingEntity entity,
            @Nonnull ItemStack stack) {
        
        if(level.getBlockState(pos.above()).isAir())
            level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(POSTER_TYPE, state.getValue(POSTER_TYPE)), 3);
        else if(level.getBlockState(pos.below()).isAir()) {
            level.setBlock(pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(POSTER_TYPE, state.getValue(POSTER_TYPE)), 3);
        }
    }

    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction,
            @Nonnull BlockState otherState, @Nonnull LevelAccessor level,
            @Nonnull BlockPos pos, @Nonnull BlockPos otherPos) {
        DoubleBlockHalf thisHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && thisHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return otherState.is(this) && otherState.getValue(HALF) != thisHalf
                    ? state.setValue(FACING, otherState.getValue(FACING))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return thisHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
        }
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        Direction behindDirection = state.getValue(FACING).getOpposite();

        BlockPos posBehind = pos.relative(behindDirection);
        BlockState stateBehind = level.getBlockState(posBehind);
        return stateBehind.isFaceSturdy(level, posBehind, behindDirection.getOpposite());
    }

    @Override
    public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nonnull Player player) {
        if (!level.isClientSide) {
            if (player.isCreative())
                preventCreativeDropFromBottomPart(level, pos, state, player);
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HALF, POSTER_TYPE);
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state,
            Player player) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState()
                        : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
            Player player) {
        return PosterBlockItem.newItemFromType(state.getValue(POSTER_TYPE));
    }
}