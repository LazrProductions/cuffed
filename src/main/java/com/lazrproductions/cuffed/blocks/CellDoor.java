package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.KeyRing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CellDoor extends DoorBlock {

    public static final BooleanProperty IN_BARS = BooleanProperty.create("in_bars");

    private final BlockSetType type;

    public CellDoor(Properties p, BlockSetType setType) {
        super(p, setType);
        this.type = setType;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(OPEN, Boolean.valueOf(false))
                        .setValue(HINGE, DoorHingeSide.LEFT)
                        .setValue(POWERED, Boolean.valueOf(false))
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(IN_BARS, Boolean.valueOf(false)));
    }

    protected static final VoxelShape BARS_NS_AABB = Block.box(0.0D, 0.0D, 7.0D,
            16.0D, 16.0D, 9.0D);

    protected static final VoxelShape BARS_NORTH_RIGHT_OPEN_AABB = Block.box(0.0D, 0.0D, -7.0D,
            2.0D, 16.0D, 9.0D);
    protected static final VoxelShape BARS_NORTH_LEFT_OPEN_AABB = Block.box(14.0D, 0.0D, -7.0D,
            16.0D, 16.0D, 9.0D);

    protected static final VoxelShape BARS_SOUTH_RIGHT_OPEN_AABB = Block.box(0.0D, 0.0D, 7.0D,
            2.0D, 16.0D, 23.0D);
    protected static final VoxelShape BARS_SOUTH_LEFT_OPEN_AABB = Block.box(14.0D, 0.0D, 7.0D,
            16.0D, 16.0D, 23.0D);


    protected static final VoxelShape BARS_EW_AABB = Block.box(7.0D, 0.0D, 0.0D,
            9.0D, 16.0D, 16.0D);

    protected static final VoxelShape BARS_WEST_RIGHT_OPEN_AABB = Block.box(7.0D, 0.0D, 0.0D,
            23.0D, 16.0D, 2.0D);
    protected static final VoxelShape BARS_WEST_LEFT_OPEN_AABB = Block.box(7.0D, 0.0D, 14.0D,
            23.0D, 16.0D, 16.0D);

    protected static final VoxelShape BARS_EAST_RIGHT_OPEN_AABB = Block.box(-7.0D, 0.0D, 0.0D,
            9.0D, 16.0D, 2.0D);
    protected static final VoxelShape BARS_EAST_LEFT_OPEN_AABB = Block.box(-7.0D, 0.0D, 14.0D,
            9.0D, 16.0D, 16.0D);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_52808_, BlockPos p_52809_,
            CollisionContext p_52810_) {
        if (isInBars(state)) {
            Direction facing = state.getValue(FACING);
            if (facing == Direction.NORTH || facing == Direction.SOUTH)
                if(!state.getValue(OPEN))
                    return BARS_NS_AABB;
                else
                    if(facing == Direction.NORTH)    
                        return (state.getValue(HINGE) == DoorHingeSide.RIGHT) ? BARS_SOUTH_LEFT_OPEN_AABB : BARS_SOUTH_RIGHT_OPEN_AABB;
                    else
                        return (state.getValue(HINGE) == DoorHingeSide.RIGHT) ? BARS_NORTH_RIGHT_OPEN_AABB : BARS_NORTH_LEFT_OPEN_AABB;
            else
                if(!state.getValue(OPEN))
                    return BARS_EW_AABB;
                else
                    if(facing == Direction.WEST) 
                        return (state.getValue(HINGE) == DoorHingeSide.RIGHT) ? BARS_WEST_RIGHT_OPEN_AABB : BARS_WEST_LEFT_OPEN_AABB;
                    else
                        return (state.getValue(HINGE) == DoorHingeSide.RIGHT) ? BARS_EAST_LEFT_OPEN_AABB : BARS_EAST_RIGHT_OPEN_AABB;

        } else
            return super.getShape(state, p_52808_, p_52809_, p_52810_);
    }

    @Override
    public BlockSetType type() {
        return this.type;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(blockpos.above()).canBeReplaced(ctx)) {

            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection())
                    .setValue(HINGE, this.getHinge(ctx)).setValue(POWERED, false)
                    .setValue(OPEN, false).setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(IN_BARS, checkForBars(level, blockpos, ctx.getHorizontalDirection()));
        } else {
            return null;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getInventory().getSelected();

        BlockPos bottomPos = pos;

        if (level.getBlockState(pos.below()).getBlock() instanceof CellDoor)
            bottomPos = pos.below();

        if (stack.is((ModItems.KEY.get())) && stack.getTagElement("BoundDoor") != null) {
            CompoundTag doorTag = stack.getTagElement("BoundDoor");
            if (doorTag != null) {
                int[] boundPos = doorTag.getIntArray("Position");
                // Check if this door is the bottom half or not.
                if (boundPos[0] == bottomPos.getX() && boundPos[1] == bottomPos.getY()
                        && boundPos[2] == bottomPos.getZ()) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 10);
                    this.playSound(player, level, pos, state.getValue(OPEN));
                    level.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE,
                            pos);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            } else
                return InteractionResult.FAIL;

        }

        if (stack.is((ModItems.KEY_RING.get())) && KeyRing.HasBoundDoorAt(stack, bottomPos)) {
            state = state.cycle(OPEN);
            level.setBlock(pos, state, 10);
            this.playSound(player, level, pos, state.getValue(OPEN));
            level.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE,
                    pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.FAIL;
    }

    private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open) {
        level.playSound(entity, pos, open ? this.type.doorOpen() : this.type.doorClose(),
                SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED, IN_BARS);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction updateDirection, BlockState otherState, LevelAccessor level,
            BlockPos pos, BlockPos otherPos) {

            Direction facing = state.getValue(FACING);
            boolean isBottom = state.getValue(HALF) == DoubleBlockHalf.LOWER;
            
            boolean flag3 = false;
            if(isBottom) {
                BlockPos top = pos.above();
                if(checkForBars((Level)level, top, facing))
                    flag3 = true;
            } else {
                BlockPos below = pos.below();
                if(checkForBars((Level)level, below, facing))
                    flag3 = true;
            }

            boolean flag = checkForBars((Level)level, pos, facing);
            boolean flag2 = isInBars(state);
            if (flag != flag2 && flag == flag3) {
                return state.setValue(IN_BARS, flag);
            }
            return super.updateShape(state, updateDirection, otherState, level, pos, otherPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2,
            boolean query) {
        // do nothing, so redstone doesn't effect the door

    }

    public boolean isInBars(BlockState state) {
        return state.getValue(IN_BARS);
    }

    public boolean checkForBars(Level level, BlockPos pos, Direction facing) {
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            BlockState other = level.getBlockState(pos.west());
            boolean leftIs = other.is(BlockTags.WALLS) || other.getBlock() instanceof IronBarsBlock
                    || other.getBlock() instanceof ReinforcedBarsBlock;
            other = level.getBlockState(pos.east());
            boolean rightIs = other.is(BlockTags.WALLS) || other.getBlock() instanceof IronBarsBlock
                    || other.getBlock() instanceof ReinforcedBarsBlock;
            return leftIs && rightIs;
        } else {
            BlockState other = level.getBlockState(pos.north());
            boolean leftIs = other.is(BlockTags.WALLS) || other.getBlock() instanceof IronBarsBlock
                    || other.getBlock() instanceof ReinforcedBarsBlock || other.getBlock() instanceof CellDoor;
            other = level.getBlockState(pos.south());
            boolean rightIs = other.is(BlockTags.WALLS) || other.getBlock() instanceof IronBarsBlock
                    || other.getBlock() instanceof ReinforcedBarsBlock || other.getBlock() instanceof CellDoor;
            return leftIs && rightIs;
        }
    }

    private DoorHingeSide getHinge(BlockPlaceContext p_52805_) {
        BlockGetter blockgetter = p_52805_.getLevel();
        BlockPos blockpos = p_52805_.getClickedPos();
        Direction direction = p_52805_.getHorizontalDirection();
        BlockPos blockpos1 = blockpos.above();
        Direction direction1 = direction.getCounterClockWise();
        BlockPos blockpos2 = blockpos.relative(direction1);
        BlockState blockstate = blockgetter.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.relative(direction1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos3);
        Direction direction2 = direction.getClockWise();
        BlockPos blockpos4 = blockpos.relative(direction2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.relative(direction2);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos5);
        int i = (blockstate.isCollisionShapeFullBlock(blockgetter, blockpos2) ? -1 : 0)
                + (blockstate1.isCollisionShapeFullBlock(blockgetter, blockpos3) ? -1 : 0)
                + (blockstate2.isCollisionShapeFullBlock(blockgetter, blockpos4) ? 1 : 0)
                + (blockstate3.isCollisionShapeFullBlock(blockgetter, blockpos5) ? 1 : 0);
        boolean flag = blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean flag1 = blockstate2.is(this) && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getStepX();
                int k = direction.getStepZ();
                Vec3 vec3 = p_52805_.getClickLocation();
                double d0 = vec3.x - (double) blockpos.getX();
                double d1 = vec3.z - (double) blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D))
                        && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }
}
