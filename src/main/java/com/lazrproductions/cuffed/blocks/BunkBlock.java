package com.lazrproductions.cuffed.blocks;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import com.lazrproductions.cuffed.blocks.entity.BunkBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


@SuppressWarnings("deprecation")
public class BunkBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    protected static final int HEIGHT = 9;


    protected static final VoxelShape BASE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 7.0D, 16.0D);


    protected static final VoxelShape NORTH_HEAD_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 1.0D, 15.0D, 9.0D, 16.0D));
    protected static final VoxelShape NORTH_FOOT_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 0.0D, 15.0D, 9.0D, 15.0D));
    
    protected static final VoxelShape SOUTH_HEAD_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 0.0D, 15.0D, 9.0D, 15.0D));
    protected static final VoxelShape SOUTH_FOOT_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 1.0D, 15.0D, 9.0D, 16.0D));
    
    protected static final VoxelShape EAST_HEAD_SHAPE = Shapes.or(BASE, Block.box(0.0D, 7.0D, 1.0D, 15.0D, 9.0D, 15.0D));
    protected static final VoxelShape EAST_FOOT_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 1.0D, 16.0D, 9.0D, 15.0D));
    
    protected static final VoxelShape WEST_HEAD_SHAPE = Shapes.or(BASE, Block.box(1.0D, 7.0D, 1.0D, 16.0D, 9.0D, 15.0D));
    protected static final VoxelShape WEST_FOOT_SHAPE = Shapes.or(BASE, Block.box(0.0D, 7.0D, 1.0D, 15.0D, 9.0D, 15.0D));

    public BunkBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.valueOf(false)));
    }

    @Nullable
    public static Direction getBedOrientation(BlockGetter level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return blockstate.getBlock() instanceof BunkBlock ? blockstate.getValue(FACING) : null;
    }

    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Player interactor, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (state.getValue(PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(FACING));
                state = level.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }

            if (!canSetSpawn(level)) {
                level.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
                if (level.getBlockState(blockpos).is(this)) {
                    level.removeBlock(blockpos, false);
                }

                Vec3 vec3 = pos.getCenter();
                level.explode((Entity) null, level.damageSources().badRespawnPointExplosion(vec3),
                        (ExplosionDamageCalculator) null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                interactor.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);

                return InteractionResult.SUCCESS;
            } else {
                interactor.startSleepInBed(pos).ifLeft((sleepProblem) -> {
                    if (sleepProblem != null) {
                        Component c = sleepProblem.getMessage();
                        if(c != null)
                            interactor.displayClientMessage(c, true);
                    }

                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos,
            @org.jetbrains.annotations.Nullable Entity player) {
        return true;
    }

    public static boolean canSetSpawn(Level level) {
        return level.dimensionType().bedWorks();
    }

    public void fallOn(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Entity entity,
            float f) {
        super.fallOn(level, state, pos, entity, f * 0.5F);
    }

    public void updateEntityAfterFallOn(@Nonnull BlockGetter getter, @Nonnull Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(getter, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(vec3.x, -vec3.y * (double) 0.66F * d0, vec3.z);
        }
    }

    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction,
            @Nonnull BlockState otherState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos,
            @Nonnull BlockPos otherPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return otherState.is(this) && otherState.getValue(PART) != state.getValue(PART)
                    ? state.setValue(OCCUPIED, otherState.getValue(OCCUPIED))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, direction, otherState, level, pos, otherPos);
        }
    }

    private static Direction getNeighbourDirection(BedPart p_49534_, Direction p_49535_) {
        return p_49534_ == BedPart.FOOT ? p_49535_ : p_49535_.getOpposite();
    }

    public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nonnull Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.FOOT) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getCounterClockWise(Axis.Y);
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        
        Direction face = context.getClickedFace();
        if(face == Direction.DOWN || face == Direction.UP)
            return null;

        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
                ? this.defaultBlockState().setValue(FACING, direction)
                : null;
    }

    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull CollisionContext collisionContext) {
        Direction direction = state.getValue(FACING);
        switch (direction) {
            case NORTH:
                return state.getValue(PART) == BedPart.HEAD ? NORTH_HEAD_SHAPE : NORTH_FOOT_SHAPE;
            case SOUTH:
                return state.getValue(PART) == BedPart.HEAD ? SOUTH_HEAD_SHAPE : SOUTH_FOOT_SHAPE;
            case WEST:
                return state.getValue(PART) == BedPart.HEAD ? WEST_HEAD_SHAPE : WEST_FOOT_SHAPE;
            default:
                return state.getValue(PART) == BedPart.HEAD ? EAST_HEAD_SHAPE : EAST_FOOT_SHAPE;
        }
    }

    public static Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        BedPart bedpart = state.getValue(PART);
        return bedpart == BedPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    public static Optional<Vec3> findStandUpPosition(EntityType<?> entityType, CollisionGetter collision, BlockPos pos,
            Direction facing, float f) {
        Direction direction = facing.getClockWise();
        Direction direction1 = direction.isFacingAngle(f) ? direction.getOpposite() : direction;

        int[][] aint = bedStandUpOffsets(facing, direction1);
        Optional<Vec3> optional = findStandUpPositionAtOffset(entityType, collision, pos, aint, true);
        return optional.isPresent() ? optional : findStandUpPositionAtOffset(entityType, collision, pos, aint, false);

    }

    private static Optional<Vec3> findStandUpPositionAtOffset(EntityType<?> entityType, CollisionGetter collision,
            BlockPos pos, int[][] p_49473_, boolean p_49474_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int[] aint : p_49473_) {
            blockpos$mutableblockpos.set(pos.getX() + aint[0], pos.getY(), pos.getZ() + aint[1]);
            Vec3 vec3 = DismountHelper.findSafeDismountLocation(entityType, collision, blockpos$mutableblockpos,
                    p_49474_);
            if (vec3 != null) {
                return Optional.of(vec3);
            }
        }

        return Optional.empty();
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new BunkBlockEntity(pos, state);
    }

    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            level.setBlock(blockpos, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }

    }

    public long getSeed(@Nonnull BlockState p_49522_, @Nonnull BlockPos p_49523_) {
        BlockPos blockpos = p_49523_.relative(p_49522_.getValue(FACING),
                p_49522_.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), p_49523_.getY(), blockpos.getZ());
    }

    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull PathComputationType path) {
        return false;
    }

    private static int[][] bedStandUpOffsets(Direction p_49539_, Direction p_49540_) {
        return ArrayUtils.addAll((int[][]) bedSurroundStandUpOffsets(p_49539_, p_49540_),
                (int[][]) bedAboveStandUpOffsets(p_49539_));
    }

    private static int[][] bedSurroundStandUpOffsets(Direction p_49552_, Direction p_49553_) {
        return new int[][] { { p_49553_.getStepX(), p_49553_.getStepZ() },
                { p_49553_.getStepX() - p_49552_.getStepX(), p_49553_.getStepZ() - p_49552_.getStepZ() },
                { p_49553_.getStepX() - p_49552_.getStepX() * 2, p_49553_.getStepZ() - p_49552_.getStepZ() * 2 },
                { -p_49552_.getStepX() * 2, -p_49552_.getStepZ() * 2 },
                { -p_49553_.getStepX() - p_49552_.getStepX() * 2, -p_49553_.getStepZ() - p_49552_.getStepZ() * 2 },
                { -p_49553_.getStepX() - p_49552_.getStepX(), -p_49553_.getStepZ() - p_49552_.getStepZ() },
                { -p_49553_.getStepX(), -p_49553_.getStepZ() },
                { -p_49553_.getStepX() + p_49552_.getStepX(), -p_49553_.getStepZ() + p_49552_.getStepZ() },
                { p_49552_.getStepX(), p_49552_.getStepZ() },
                { p_49553_.getStepX() + p_49552_.getStepX(), p_49553_.getStepZ() + p_49552_.getStepZ() } };
    }

    private static int[][] bedAboveStandUpOffsets(Direction p_49537_) {
        return new int[][] { { 0, 0 }, { -p_49537_.getStepX(), -p_49537_.getStepZ() } };
    }
}