package com.lazrproductions.cuffed.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PadlockEntity extends HangingEntity {

    private static final EntityDataAccessor<Boolean> DATA_LOCKED = SynchedEntityData.defineId(PadlockEntity.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_KEY = SynchedEntityData.defineId(PadlockEntity.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_REINFORCED = SynchedEntityData.defineId(PadlockEntity.class,
            EntityDataSerializers.BOOLEAN);

    public PadlockEntity(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    public PadlockEntity(Level world, BlockPos pos) {
        super(ModEntityTypes.PADLOCK.get(), world, pos);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
        this.getPersistentData().putBoolean("Locked", true);
    }

    public boolean isOnSuitableBlock() {
        return this.level().getBlockState(pos).is(ModTags.Blocks.LOCKABLE_BLOCKS);
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
        float xO = this.getYRot() == 90.0f ? -1 : this.getYRot() == 270.0f || this.getYRot() == -90.0f ? 1 : 0;
        float zO = this.getYRot() == 0.0f ? 1 : this.getYRot() == 180.0f || this.getYRot() == -180.0f ? -1 : 0;
        ItemEntity itementity = new ItemEntity(this.level(), this.pos.getX() + 0.5f + xO, this.pos.getY() + 0.5f,
                this.pos.getZ() + 0.5f + zO, new ItemStack(ModItems.PADLOCK.get()));
        itementity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itementity);
    }

    @Override
    public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = interactor.getItemInHand(hand);
            if (stack.is(ModItems.KEY.get())) {
                if (KeyItem.isBoundToBlock(stack, pos)) {
                    interactor.awardStat(Stats.ITEM_USED.get(ModItems.KEY.get()));
                    // Toggle locked
                    if (!interactor.isCrouching()) {
                        setLocked(!isLocked());
                        if (isLocked())
                            interactor.displayClientMessage(Component.literal("Padlock locked."), true);
                        else
                            interactor.displayClientMessage(Component.literal("Padlock unlocked."), true);

                        this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    } else {
                        // pickup lock
                        KeyItem.removeBoundBlock(stack);
                        this.RemoveLock();
                        return InteractionResult.SUCCESS;
                    }
                } else if (!hasKey()) {
                    if (KeyItem.tryToSetBoundBlock(interactor, stack, this.pos)) {
                        setHasKey(true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (stack.is((ModItems.KEY_RING.get()))) {
                if (KeyRingItem.hasBoundBlockAt(stack, this.pos)) {
                    interactor.awardStat(Stats.ITEM_USED.get(ModItems.KEY_RING.get()));
                    // Unlock
                    if (!interactor.isCrouching()) {
                        setLocked(!isLocked());
                        if (isLocked())
                            interactor.displayClientMessage(Component.literal("Padlock locked."), true);
                        else
                            interactor.displayClientMessage(Component.literal("Padlock unlocked."), true);
                        this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
                        return InteractionResult.CONSUME;
                    } else {
                        // pickup lock
                        KeyRingItem.removeBoundDoorAt(stack, this.pos);
                        this.RemoveLock();
                        return InteractionResult.CONSUME;
                    }
                } else if (!hasKey() && KeyRingItem.canBindBlock(stack)) {
                    if (KeyRingItem.tryToAddBoundBlock(interactor, stack, this.pos)) {
                        setHasKey(true);
                        return InteractionResult.CONSUME;
                    }
                }
            }

            if (stack.is((Items.DIAMOND)) && !isReinforced()) {
                interactor.awardStat(Stats.ITEM_USED.get(Items.DIAMOND));
                setReinforced(true);
                stack.shrink(1);
                this.playSound(SoundEvents.NETHERITE_BLOCK_PLACE, 1, 1);
                return InteractionResult.CONSUME;
            }

            if (stack.is(ModItems.LOCKPICK.get())) {
                CuffedAPI.Networking.sendLockpickBeginPickingLockPacketToClient((ServerPlayer) interactor, this.getId(),
                        isReinforced()
                                ? CuffedMod.CONFIG.lockpickingSettings.speedIncreasePerPickForBreakingReinforcedPadlocks
                                : CuffedMod.CONFIG.lockpickingSettings.speedIncreasePerPickForBreakingPadlocks,
                        isReinforced()
                                ? CuffedMod.CONFIG.lockpickingSettings.progressPerPickForBreakingReinforcedPadlocks
                                : CuffedMod.CONFIG.lockpickingSettings.progressPerPickForBreakingPadlocks);

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }
    }

    public static PadlockEntity getOrCreateLockAt(Level level, BlockPos pos, Direction direction) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (PadlockEntity padlockentity : level.getEntitiesOfClass(PadlockEntity.class,
                new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D,
                        (double) k + 1.0D))) {
            if (padlockentity.getPos().equals(pos)) {
                return padlockentity;
            }
        }

        PadlockEntity padlockentity1 = new PadlockEntity(level, pos);
        padlockentity1.setRot(direction.toYRot(), 0);
        level.addFreshEntity(padlockentity1);
        return padlockentity1;
    }

    public static PadlockEntity getLockAt(Level level, BlockPos pos) {
        if (pos == null)
            return null;

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (PadlockEntity padlockentity : level.getEntitiesOfClass(PadlockEntity.class,
                new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D,
                        (double) k + 1.0D))) {
            if (padlockentity.getPos().equals(pos)) {
                return padlockentity;
            }
        }

        return null;
    }

    public void RemoveLock() {
        float xO = this.getYRot() == 90.0f ? -1 : this.getYRot() == 270.0f || this.getYRot() == -90.0f ? 1 : 0;
        float zO = this.getYRot() == 0.0f ? 1 : this.getYRot() == 180.0f || this.getYRot() == -180.0f ? -1 : 0;
        ItemEntity itementity = new ItemEntity(this.level(), this.pos.getX() + 0.5f + xO, this.pos.getY() + 0.5f,
                this.pos.getZ() + 0.5f + zO, new ItemStack(ModItems.PADLOCK.get()));
        itementity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itementity);

        if (isReinforced()) {
            ItemEntity diamondEntity = new ItemEntity(this.level(), this.pos.getX() + 0.5f + xO, this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.5f + zO, new ItemStack(Items.DIAMOND));
            diamondEntity.setDefaultPickUpDelay();
            this.level().addFreshEntity(diamondEntity);
        }

        this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, (double) Mth.floor(y) + 0.5D, (double) Mth.floor(z) + 0.5D);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    protected float getEyeHeight(@Nonnull Pose p_31839_, @Nonnull EntityDimensions p_31840_) {
        return 0.0625F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LOCKED, false);
        this.entityData.define(DATA_HAS_KEY, false);
        this.entityData.define(DATA_REINFORCED, false);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Locked", this.isLocked());
        tag.putBoolean("HasKey", this.hasKey());
        tag.putBoolean("Reinforced", this.isReinforced());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_LOCKED, tag.getBoolean("Locked"));
        this.setLocked(tag.getBoolean("Locked"));

        this.entityData.set(DATA_HAS_KEY, tag.getBoolean("HasKey"));
        this.setLocked(tag.getBoolean("HasKey"));

        this.entityData.set(DATA_REINFORCED, tag.getBoolean("Reinforced"));
        this.setLocked(tag.getBoolean("Reinforced"));
    }

    @Override
    public boolean survives() {
        return !this.level().getBlockState(this.pos).isAir();
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.NETHERITE_BLOCK_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, 0, this.getPos());
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.PADLOCK.get());
    }

    public void setLocked(boolean value) {
        this.entityData.set(DATA_LOCKED, value);
    }

    public void setHasKey(boolean value) {
        this.entityData.set(DATA_HAS_KEY, value);
    }

    public void setReinforced(boolean value) {
        this.entityData.set(DATA_REINFORCED, value);
    }

    public boolean isLocked() {
        return this.entityData.get(DATA_LOCKED);
    }

    public boolean hasKey() {
        return this.entityData.get(DATA_HAS_KEY);
    }

    public boolean isReinforced() {
        return this.entityData.get(DATA_REINFORCED);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.3D,
                (double) this.pos.getZ() + 0.5D);

        this.setBoundingBox(new AABB(this.getX() - 0.52D, this.getY() - 0.12D, this.getZ() - 0.52D,
                this.getX() + 0.52D, this.getY() + 0.13D, this.getZ() + 0.52D));
    }
}