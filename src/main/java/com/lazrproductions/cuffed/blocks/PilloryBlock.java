package com.lazrproductions.cuffed.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PilloryBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    protected static final VoxelShape SHAPE_NS_CLOSED = Shapes.or(
            Block.box(16 * -0.0625, 16 * 0, 16 * 0.375,16 * 0.125, 16 * 0.5625, 16 * 0.625),
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
    public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
        BlockPos blockpos = p_52739_.getClickedPos();
        Level level = p_52739_.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(blockpos.above()).canBeReplaced(p_52739_)) {
            return this.defaultBlockState().setValue(FACING, p_52739_.getHorizontalDirection()).setValue(HALF,
                    DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos,
            CollisionContext ctx) {
        if(state.getValue(HALF) == DoubleBlockHalf.LOWER)
             return (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH)
                ? SHAPE_NS_BASE
                : SHAPE_EW_BASE;
        return (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH)
                ? (state.getValue(CLOSED)) ? SHAPE_NS_CLOSED : SHAPE_NS_OPEN
                : (state.getValue(CLOSED)) ? SHAPE_EW_CLOSED : SHAPE_EW_OPEN;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos,
            CollisionContext ctx) {
        return getShape(state, getter, pos, ctx);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation r) {
        return state.setValue(FACING, r.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror m) {
        return state.rotate(m.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity,
            ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level,
            BlockPos pos, BlockPos otherPos) {
        DoubleBlockHalf thisHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
                && thisHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return otherState.is(this) && otherState.getValue(HALF) != thisHalf
                    ? state.setValue(FACING, otherState.getValue(FACING)).setValue(CLOSED,otherState.getValue(CLOSED))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return thisHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN
                    && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? blockstate.isFaceSturdy(level, blockpos, Direction.UP)
                : blockstate.is(this);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            preventCreativeDropFromBottomPart(level, pos, state, player);
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HALF, CLOSED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if(!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.LOWER)
            if(!level.isClientSide) {
                BlockState state1 = level.getBlockState(pos.above());
                state.getBlock().use(state1, level, pos.above(), player, hand, hit);
                return InteractionResult.SUCCESS;
            } else
                return InteractionResult.SUCCESS; 

        if(!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.UPPER && !level.isClientSide) {
            Random r = new Random();
            level.playSound(null, pos, ModSounds.PILLORY_USE_SOUND, SoundSource.BLOCKS, 1, (state.getValue(CLOSED) ? 1.0F : 0.8F) + (r.nextFloat() * 0.1F));

            boolean closed = getClosed(state);

            //Will close, so capture person behind to detain them
            if (!getClosed(state)) {

                Vec3 behind = getPositionBehind(state, pos);
                Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
                if (p != null) {
                    double dist = Math.sqrt(Math.pow(behind.z-p.position().z,2) + Math.pow(behind.x-p.position().x, 2));
                    if(dist<0.3f) { // make sure the player is close enough
                        CuffedCapability cuffed = CuffedAPI.Capabilities.getCuffedCapability(p);
                        if (cuffed.isHandcuffed() && cuffed.isDetained() == -1&& !cuffed.isAnchored()) {
                            p.teleportTo(behind.x, behind.y, behind.z);
                            p.setYRot(getFacingRotation(state, pos));
                            cuffed.server_setDetained(0);
                        }
                    }
                }
            } else {
                //Will open, so release person behind
                Vec3 behind = getPositionBehind(state, pos);
                Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
                if (p != null) {
                    double dist = Math
                            .sqrt(Math.pow(behind.z - p.position().z, 2) + Math.pow(behind.x - p.position().x, 2));
                    if (dist < 0.3f) { // make sure the player is close enough
                        CuffedCapability cuffed = CuffedAPI.Capabilities.getCuffedCapability(p);
                        if (cuffed.isHandcuffed() && cuffed.isDetained() == 0) {
                            cuffed.server_setDetained(-1);
                        }
                    }
                }
            }

            level.setBlock(pos, state.setValue(CLOSED, !closed), UPDATE_ALL_IMMEDIATE);

            return InteractionResult.SUCCESS;
        } else if(!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.UPPER && level.isClientSide)
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

    protected static void preventCreativeDropFromBottomPart(Level p_52904_, BlockPos p_52905_, BlockState p_52906_,
            Player p_52907_) {
        DoubleBlockHalf doubleblockhalf = p_52906_.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = p_52905_.below();
            BlockState blockstate = p_52904_.getBlockState(blockpos);
            if (blockstate.is(p_52906_.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState()
                        : Blocks.AIR.defaultBlockState();
                p_52904_.setBlock(blockpos, blockstate1, 35);
                p_52904_.levelEvent(p_52907_, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public float getFacingRotation(BlockState state, BlockPos pos) {
        return state.getValue(FACING).toYRot();
    }

    public Vec3 getPositionBehind(BlockState state, BlockPos pos) {
        double xOffset = (state.getValue(FACING)==Direction.EAST) ? -0.363d : (state.getValue(FACING)==Direction.WEST) ? 0.363d : 0;
        double zOffset = (state.getValue(FACING)==Direction.SOUTH) ? -0.363d : (state.getValue(FACING)==Direction.NORTH) ? 0.363d : 0;
        return pos.getCenter().add(new Vec3(xOffset, -1.5F, zOffset));
    }
}
