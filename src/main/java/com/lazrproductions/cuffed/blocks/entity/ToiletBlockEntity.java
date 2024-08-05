package com.lazrproductions.cuffed.blocks.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.items.TrayItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ToiletBlockEntity extends BlockEntity {
    private static final int INVENTORY_SIZE = 1;
    private NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public ToiletBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAY.get(), pos, state);
    }

    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Player interacting,
            @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (hand == InteractionHand.MAIN_HAND && !level.isClientSide()) {
            ItemStack stack = interacting.getItemInHand(hand);
            if(stack.isEmpty()) {
                ItemStack stackToRemove = getNextStack();
                if(removeNextItem()) {
                    interacting.setItemInHand(hand, stackToRemove.copy());

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.CONSUME;
                }
            } else {
                if(addItem(stack.copyWithCount(1))) {
                    stack.shrink(1);

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.FAIL;
    }

    @Nonnull
    public ItemStack getNextStack() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty())
                return itemStack;
        }
        return ItemStack.EMPTY;
    }

    protected boolean addItem(@Nonnull ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack);
                return true;
            }
        }
        return false;
    }

    protected boolean setItem(@Nonnull ItemStack stack, int index) {
        if (items.get(index).isEmpty()) {
            items.set(index, stack);
            return true;
        }
        return false;
    }

    protected boolean removeNextItem() {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if(!items.get(i).isEmpty()) {
                index = i;
                break;
            }
        }
        
        if(index<0)
            return false;

        return removeItem(index);
    }

    protected boolean removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            boolean removedSomething = !items.get(index).isEmpty();
            items.set(index, ItemStack.EMPTY);
            return removedSomething;
        }
        return false;
    }




    protected void sendUpdate(Level level, BlockPos pos, BlockState state) {
        setChanged(level, pos, state);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }



    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);

        ListTag list = new ListTag();
        items.forEach((c) -> {
            CompoundTag t = new CompoundTag();
            c.save(t); 
            list.add(t);
        });
        tag.put(TrayItem.TAG_ITEMS, list);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);

        if(tag.contains(TrayItem.TAG_ITEMS)) {
            ListTag list = tag.getList(TrayItem.TAG_ITEMS, 10);
            items = NonNullList.withSize(list.size(), ItemStack.EMPTY);
            for (int i = 0; i < list.size(); i++) {
                items.set(i, ItemStack.of(list.getCompound(i)));
            }            
        }
    }




    public List<ItemStack> getDrops(@Nonnull Level level, @Nonnull BlockPos pos) {
        List<ItemStack> stacks = List.of();
        for (ItemStack itemStack : items) {
            stacks.add(itemStack);
        }
        return stacks;
    }



    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}