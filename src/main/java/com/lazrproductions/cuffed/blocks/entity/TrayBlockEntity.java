package com.lazrproductions.cuffed.blocks.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.TrayItem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class TrayBlockEntity extends BlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public TrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAY.get(), pos, state);
    }

    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Player interacting,
            @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack itemInMainHand = interacting.getItemInHand(hand);
            if(itemInMainHand.isEmpty()) {
                if(!interacting.isCrouching()) {
                    if (hasFood()) {
                        float nutritionRatio = 1;
                        if (hasFork())
                            nutritionRatio += 0.33333f;
                        if (hasSpoon())
                            nutritionRatio += 0.33333f;
                        if (hasKnife())
                            nutritionRatio += 0.33333f;
                        ItemStack food = getFood();
                        food.getFoodProperties(interacting);

                        eat(level, interacting, food, nutritionRatio);
                        sendUpdate(level, pos, state);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    ItemStack food = getFood().copy();
                    ItemStack fork = getFork().copy();
                    ItemStack spoon = getSpoon().copy();
                    ItemStack knife = getKnife().copy();
                    if(removeItem(0)) {
                        interacting.setItemInHand(hand, food);

                        level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                            SoundEvents.COD_FLOP, SoundSource.NEUTRAL, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                        sendUpdate(level, pos, state);
                        return InteractionResult.SUCCESS;
                    } else if(removeItem(1)) {
                        interacting.setItemInHand(hand, fork);

                        level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                            SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                        sendUpdate(level, pos, state);
                        return InteractionResult.SUCCESS;
                    } else if(removeItem(2)) {
                        interacting.setItemInHand(hand, spoon);

                        level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                            SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                        sendUpdate(level, pos, state);
                        return InteractionResult.SUCCESS;
                    } else if(removeItem(3)) {
                        interacting.setItemInHand(hand, knife);

                        level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                            SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                        sendUpdate(level, pos, state);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                if(TrayItem.itemIsFood(itemInMainHand) && !hasFood()) {
                    setItem(itemInMainHand.copyWithCount(1), 0);
                    itemInMainHand.shrink(1);

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.COD_FLOP, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.SUCCESS;
                } else if(TrayItem.itemIsFork(itemInMainHand) && !hasFork()) {
                    setItem(itemInMainHand.copyWithCount(1), 1);
                    itemInMainHand.shrink(1);

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.SUCCESS;
                } else if(TrayItem.itemIsSpoon(itemInMainHand) && !hasSpoon()) {
                    setItem(itemInMainHand.copyWithCount(1), 2);
                    itemInMainHand.shrink(1);

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.SUCCESS;
                } else if(TrayItem.itemIsKnife(itemInMainHand) && !hasKnife()) {
                    setItem(itemInMainHand.copyWithCount(1), 3);
                    itemInMainHand.shrink(1);

                    level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                        SoundEvents.LANTERN_HIT, SoundSource.NEUTRAL, 1.0F,
                        1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

                    sendUpdate(level, pos, state);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.FAIL;
    }

    public void eat(Level level, Player player, ItemStack stack, float nutritionRatio) {
        if (stack.isEdible()) {
            FoodProperties foodproperties = stack.getFoodProperties(player);
            player.getFoodData().eat(Mth.floor((float) foodproperties.getNutrition() * nutritionRatio),
                    (float) foodproperties.getSaturationModifier());

            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.playSound((Player) null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(),
                    SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
            }

            for(Pair<MobEffectInstance, Float> pair : stack.getFoodProperties(player).getEffects()) {
                if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                    player.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            }
            
            level.playSound((Player) null, getBlockPos().getX() + 0.5f, getBlockPos().getY(), getBlockPos().getZ() + 0.5f,
                    player.getEatingSound(stack), SoundSource.NEUTRAL, 1.0F,
                    1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

            removeItem(0);

            player.gameEvent(GameEvent.EAT);
        }
    }

    @Nonnull
    public ItemStack getFoodStack() {
        for (ItemStack itemStack : items) {
            if (TrayItem.itemIsFood(itemStack))
                return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getForkStack() {
        for (ItemStack itemStack : items) {
            if (itemStack.is(ModItems.FORK.get()))
                return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getSpoonStack() {
        for (ItemStack itemStack : items) {
            if (itemStack.is(ModItems.SPOON.get()))
                return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getKnifeStack() {
        for (ItemStack itemStack : items) {
            if (itemStack.is(ModItems.KNIFE.get()))
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

    public void loadFromItem(@Nonnull ItemStack stack, Level level, BlockPos pos, BlockState state) {
        items = TrayItem.getContents(stack);
        sendUpdate(level, pos, state);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put(TrayItem.TAG_ITEMS, TrayItem.saveItemToTagList(items));
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);

        if(tag.contains(TrayItem.TAG_ITEMS))
            items = TrayItem.getContents(tag.getList(TrayItem.TAG_ITEMS, 10));
    }



    public void dropItem(@Nonnull Level level, @Nonnull BlockPos pos) {
        ItemStack i = TrayItem.createTrayFrom(items);
        double d0 = (double) EntityType.ITEM.getWidth();
        double d1 = 0.2D;
        double d2 = d0 / 2.0D;
        double d3 = Math.floor(pos.getX()) + level.random.nextDouble() * d1 + d2;
        double d4 = Math.floor(pos.getY()) + level.random.nextDouble() * d1;
        double d5 = Math.floor(pos.getZ()) + level.random.nextDouble() * d1 + d2;

        ItemEntity e = new ItemEntity(level, d3, d4, d5, i);

        e.setDeltaMovement(
                level.random.triangle(0.0D, 0.11485000171139836D),
                level.random.triangle(0.2D, 0.11485000171139836D),
                level.random.triangle(0.0D, 0.11485000171139836D));
        e.setDefaultPickUpDelay();

        level.addFreshEntity(e);
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




    protected boolean hasFood() {
        for (ItemStack itemStack : items) {
            if (itemStack.getFoodProperties(null) != null)
                return true;
        }
        return false;
    }

    protected boolean hasSpoon() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.SPOON.get()))
                return true;
        return false;
    }

    protected boolean hasFork() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.FORK.get()))
                return true;
        return false;
    }

    protected boolean hasKnife() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.KNIFE.get()))
                return true;
        return false;
    }

    protected ItemStack getFood() {
        for (ItemStack itemStack : items) {
            if (itemStack.getFoodProperties(null) != null)
                return itemStack;
        }
        return ItemStack.EMPTY;
    }

    protected ItemStack getFork() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.FORK.get()))
                return itemStack;
        return ItemStack.EMPTY;
    }

    protected ItemStack getSpoon() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.SPOON.get()))
                return itemStack;
        return ItemStack.EMPTY;
    }

    protected ItemStack getKnife() {
        for (ItemStack itemStack : items)
            if (itemStack.is(ModItems.KNIFE.get()))
                return itemStack;
        return ItemStack.EMPTY;
    }
}