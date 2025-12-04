package com.lazrproductions.cuffed.blocks;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Vector3f;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.base.DetentionBlock;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PilloryBlock extends DetentionBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    protected static final VoxelShape SHAPE_NS_CLOSED = Shapes.or(
            Block.box(16 * -0.0625, 16 * 0, 16 * 0.375, 16 * 0.125, 16 * 0.5625, 16 * 0.625),
            Block.box(16 * 0.875, 16 * 0, 16 * 0.375, 16 * 1.0625, 16 * 0.5625, 16 * 0.625),
            Block.box(16 * 0.125, 16 * 0, 16 * 0.4375, 16 * 0.875, 16 * 0.25, 16 * 0.5625));
    protected static final VoxelShape SHAPE_NS_OPEN = Shapes.or(
            Block.box(16 * -0.0625, 16 * 0, 16 * 0.375, 16 * 0.125, 16 * 0.5625, 16 * 0.625),
            Block.box(16 * 0.875, 16 * 0, 16 * 0.375, 16 * 1.0625, 16 * 0.5625, 16 * 0.625),
            Block.box(16 * 0.125, 16 * 0.1875, 16 * 0.4375, 16 * 0.875, 16 * 0.5, 16 * 0.5625));
    protected static final VoxelShape SHAPE_NS_BASE = Shapes.or(
            Block.box(16 * -0.0625, 16 * 0, 16 * 0.375, 16 * 0.125, 16 * 1, 16 * 0.625),
            Block.box(16 * 0.875, 16 * 0, 16 * 0.375, 16 * 1.0625, 16 * 1, 16 * 0.625),
            Block.box(16 * 0.125, 16 * 0.625, 16 * 0.4375, 16 * 0.875, 16 * 0.9375, 16 * 0.5625));

    protected static final VoxelShape SHAPE_EW_CLOSED = Shapes.or(
            Block.box(16 * 0.375, 16 * 0, 16 * 0.875, 16 * 0.625, 16 * 0.5625, 16 * 1.0625),
            Block.box(16 * 0.4375, 16 * 0, 16 * 0.125, 16 * 0.5625, 16 * 0.25, 16 * 0.875),
            Block.box(16 * 0.375, 16 * 0, 16 * -0.0625, 16 * 0.625, 16 * 0.5625, 16 * 0.125));
    protected static final VoxelShape SHAPE_EW_OPEN = Shapes.or(
            Block.box(16 * 0.375, 16 * 0, 16 * 0.875, 16 * 0.625, 16 * 0.5625, 16 * 1.0625),
            Block.box(16 * 0.4375, 16 * 0.1875, 16 * 0.125, 16 * 0.5625, 16 * 0.5, 16 * 0.875),
            Block.box(16 * 0.375, 16 * 0, 16 * -0.0625, 16 * 0.625, 16 * 0.5625, 16 * 0.125));
    protected static final VoxelShape SHAPE_EW_BASE = Shapes.or(
            Block.box(16 * 0.375, 16 * 0, 16 * 0.875, 16 * 0.625, 16 * 1, 16 * 1.0625),
            Block.box(16 * 0.375, 16 * 0, 16 * -0.0625, 16 * 0.625, 16 * 1, 16 * 0.125),
            Block.box(16 * 0.4375, 16 * 0.625, 16 * 0.125, 16 * 0.5625, 16 * 0.9375, 16 * 0.875));

    public PilloryBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(CLOSED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(blockpos.above()).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HALF,
                    DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos,
            @Nonnull CollisionContext ctx) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
            return (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH)
                    ? SHAPE_NS_BASE
                    : SHAPE_EW_BASE;
        return (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH)
                ? (state.getValue(CLOSED)) ? SHAPE_NS_CLOSED : SHAPE_NS_OPEN
                : (state.getValue(CLOSED)) ? SHAPE_EW_CLOSED : SHAPE_EW_OPEN;
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
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction,
            @Nonnull BlockState otherState, @Nonnull LevelAccessor level,
            @Nonnull BlockPos pos, @Nonnull BlockPos otherPos) {
        DoubleBlockHalf thisHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && thisHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return otherState.is(this) && otherState.getValue(HALF) != thisHalf
                    ? state.setValue(FACING, otherState.getValue(FACING)).setValue(CLOSED, otherState.getValue(CLOSED))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return thisHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
        }
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? blockstate.isFaceSturdy(level, blockpos, Direction.UP)
                : blockstate.is(this);
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
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HALF, CLOSED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Player player,
            @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {

        IDetainableEntity en = (IDetainableEntity) player;
        
        if (en.getDetained() > -1)
            return InteractionResult.FAIL;

        if (!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (!level.isClientSide) {
                BlockState state1 = level.getBlockState(pos.above());
                state.getBlock().use(state1, level, pos.above(), player, hand, hit);
                return InteractionResult.SUCCESS;
            } else
                return InteractionResult.SUCCESS;
        }

        if (!player.isCrouching() && !level.isClientSide) {

            boolean wasOpen = !getClosed(state);

            boolean shouldBeOpen = attemptToToggleDetained(level, wasOpen, state, pos);

            if(shouldBeOpen != wasOpen) {
                Random r = new Random();
                level.playSound(null, pos, ModSounds.PILLORY_USE, SoundSource.BLOCKS, 1,
                        (state.getValue(CLOSED) ? 1.0F : 0.8F) + (r.nextFloat() * 0.1F));
                level.setBlock(pos, state.setValue(CLOSED, !shouldBeOpen), UPDATE_ALL_IMMEDIATE);
            }

            return InteractionResult.SUCCESS;
        } else if (!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.UPPER && level.isClientSide)
            return InteractionResult.SUCCESS;
        else
            return InteractionResult.PASS;
    }

    public void setClosed(Level level, BlockPos pos, BlockState state, boolean v) {
        level.setBlock(pos, state.setValue(CLOSED, v), UPDATE_NEIGHBORS);
    }

    public boolean getClosed(BlockState state) {
        return state.getValue(CLOSED);
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

    public float getFacingRotation(BlockState state, BlockPos pos) {
        return state.getValue(FACING).toYRot();
    }

    public static Vec3 getPositionBehind(BlockState state, BlockPos pos) {
        double xOffset = (state.getValue(FACING) == Direction.EAST) ? -0.363d
                : (state.getValue(FACING) == Direction.WEST) ? 0.363d : 0;
        double zOffset = (state.getValue(FACING) == Direction.SOUTH) ? -0.363d
                : (state.getValue(FACING) == Direction.NORTH) ? 0.363d : 0;
        return pos.getCenter().add(new Vec3(xOffset, -1.5F, zOffset));
    }

    /**
     * Attempt to toggle this block as opened or closed, returning whether or not it
     * should be OPEN
     */
    public boolean attemptToToggleDetained(@Nonnull Level level, boolean wasOpen, BlockState state, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            Vec3 behind = getPositionBehind(state, pos);
            Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
            if (p != null) {
                double dist = Math
                        .sqrt(Math.pow(behind.z - p.position().z, 2) + Math.pow(behind.x - p.position().x, 2));

                if (dist < 0.3f) {
                    IDetainableEntity detainableEntity = (IDetainableEntity) p;
                    if (!wasOpen) {
                        if (detainableEntity.getDetained() == 0) {
                            detainableEntity.undetain();
                            return true; // should become openned
                        }
                    } else {
                        if (detainableEntity.getDetained() == -1 && canDetainPlayer(level, state, pos, p)) {
                            detainableEntity.detainToBlock(level,
                                    new Vector3f((float) behind.x(), (float) behind.y(), (float) behind.z()), pos, 0,
                                    getFacingRotation(state, pos));
                            return false; // should become closed
                        } else
                            return true; // leave open if we cant detain
                    }
                }
            }
        }
        return !wasOpen;
    }

    public static Player getDetainedEntity(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos) {
        Vec3 behind = getPositionBehind(state, pos);
        Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
        if (p != null) {
            IDetainableEntity detain = (IDetainableEntity) p;
            if (detain.getDetained() > -1) {
                double dist = Math
                        .sqrt(Math.pow(behind.z - p.position().z, 2) + Math.pow(behind.x - p.position().x, 2));

                if (dist < 0.3f) {
                    return p;
                }
            }
        }

        return null;
    }

    public boolean canDetainPlayer(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos,
            @Nonnull Player player) {
        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
        if (cap.armsRestrained())
            return false;
        return getClosed(state);
    }
}
