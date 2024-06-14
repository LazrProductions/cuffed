package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.base.ILockableBlock;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class SafeBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ILockableBlock {
    public static final VoxelShape SHAPE_EW = Block.box(16 * 0.0625F, 16 * 0F, 16 * 0.125F,
            16 * 0.9375F, 16 * 1F, 16 * 0.875F);
    public static final VoxelShape SHAPE_NS = Block.box(16 * 0.125F, 16 * 0F, 16 * 0.0625F,
            16 * 0.875F, 16 * 1F, 16 * 0.9375F);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public SafeBlock(BlockBehaviour.Properties p_49046_) {
        super(p_49046_);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.FALSE)
                .setValue(LOCKED, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(BOUND, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull CollisionContext context) {
        if (state.getValue(FACING) == Direction.EAST || state.getValue(FACING) == Direction.WEST)
            return SHAPE_EW;
        return SHAPE_NS;
    }

    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Player player,
            @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = player.getInventory().getSelected();

            boolean flag = stack.is((ModItems.KEY.get())) && KeyItem.isBoundToBlock(stack, pos);
            boolean flag1 = stack.is((ModItems.KEY_RING.get())) && KeyRingItem.hasBoundBlockAt(stack, pos);
            boolean flag2 = stack.is(ModItems.LOCKPICK.get());

            if (!flag2) {
                if ((flag || flag1) && ILockableBlock.isBound(state)) {
                    boolean willEndUpLocked = !ILockableBlock.isLocked(state);
                    ILockableBlock.setIsLocked(player, state, pos, willEndUpLocked);

                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            } else if (ILockableBlock.isLocked(state)) {
                CuffedAPI.Networking.sendLockpickBeginPickingCellDoorPacketToClient((ServerPlayer) player, pos,
                        CuffedMod.CONFIG.lockpickingSettings.speedIncreasePerPickForBreakingSafes,
                        CuffedMod.CONFIG.lockpickingSettings.progressPerPickForBreakingSafes);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            if (!ILockableBlock.isLocked(state)) {
                BlockEntity blockentity = level.getBlockEntity(pos);
                if (blockentity instanceof SafeBlockEntity) {
                    player.openMenu((SafeBlockEntity) blockentity);
                    player.awardStat(ModStatistics.OPEN_SAFE.get());
                    PiglinAi.angerNearbyPiglins(player, true);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, LOCKED, BOUND, WATERLOGGED);
    }

    /* BLOCK ENTITY STUFFS */
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull BlockState newState,
            boolean p_49080_) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, pos, (Container) blockentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, p_49080_);
        }
    }

    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos,
            @Nonnull RandomSource random) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof SafeBlockEntity) {
            ((SafeBlockEntity) blockentity).recheckOpen();
        }

    }

    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new SafeBlockEntity(pos, state);
    }

    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nullable LivingEntity entity,
            @Nonnull ItemStack fromStack) {
        if (fromStack.hasCustomHoverName()) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof SafeBlockEntity) {
                ((SafeBlockEntity) blockentity).setCustomName(fromStack.getHoverName());
            }
        }

    }

    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull PathComputationType path) {
        return false;
    }

    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
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