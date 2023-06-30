package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.CellKeyRing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class CellDoor extends DoorBlock {

    private final BlockSetType type;

    public static final IntegerProperty KEYID = IntegerProperty.create("keyid", 0, 128);

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
                        .setValue(KEYID, 0));
    }

    @Override
    public BlockSetType type() {
        return this.type;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getInventory().getSelected();

        BlockPos bottomPos = pos;

        if (level.getBlockState(pos.below()).getBlock() instanceof CellDoor)
            bottomPos = pos.below();

        if (stack.is((ModItems.CELL_KEY.get())) && stack.getTagElement("BoundDoor") != null) {
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

        if (stack.is((ModItems.CELL_KEY_RING.get())) && CellKeyRing.HasBoundDoorAt(stack, bottomPos)) {
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
        builder.add(KEYID, HALF, FACING, OPEN, HINGE, POWERED);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2,
            boolean query) {
        // do nothing, so redstone doesn't effect the door
    }
}
