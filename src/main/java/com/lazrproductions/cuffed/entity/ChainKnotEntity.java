package com.lazrproductions.cuffed.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChainKnotEntity extends HangingEntity {

    public ChainKnotEntity(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    public ChainKnotEntity(Level world, BlockPos pos) {
        super(ModEntityTypes.CHAIN_KNOT.get(), world, pos);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    /**
     * Get whether or not this knot is on a fence block.
     * 
     * @return (boolean) True if this knot is on a fence block.
     */
    public boolean isOnFence() {
        return !this.level().getBlockState(pos).is(BlockTags.FENCES);
    }

    @Override
    public void dropItem(@Nullable Entity p_31837_) {
        this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
    }

    public boolean hurt(@Nonnull DamageSource source, float f) {
        if (source.getEntity() instanceof IAnchorableEntity a)
            if (a.getAnchor() == this)
                return false;
        return super.hurt(source, f);
    }

    @Override
    public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            if (((IAnchorableEntity) interactor).isAnchored())
                return InteractionResult.PASS;

            boolean flag = false;
            double maxDist = CuffedMod.SERVER_CONFIG.ANCHORING_SUFFOCATION_LENGTH.get() + 5;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(this.getX() - maxDist - 2.0D, this.getY() - maxDist - 2.0D, this.getZ() - maxDist - 2.0D,
                            this.getX() + maxDist + 2.0D, this.getY() + maxDist + 2.0D, this.getZ() + maxDist + 2.0D));

            for (LivingEntity entity : list) {
                IAnchorableEntity anchorableEntity = (IAnchorableEntity) entity;
                if (anchorableEntity.getAnchor() == interactor) {
                    // anchoredEntities.add(entity);
                    anchorableEntity.setAnchoredTo(this);
                    flag = true;
                }
            }

            boolean flag1 = false;
            if (!flag) {
                level().playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7f, 1);
                for (LivingEntity entity : list) {
                    IAnchorableEntity anchorableEntity = (IAnchorableEntity) entity;
                    if (anchorableEntity.isAnchored() && anchorableEntity.getAnchor() == this) {
                        // anchoredEntities.remove(entity);
                        anchorableEntity.setAnchoredTo(null);
                        flag1 = true;
                    }
                }
                this.discard();
            }

            if (flag)
                level().playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7f, 1);
            if (flag1)
                level().playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7f, 1);

            if (flag || flag1) {
                this.gameEvent(GameEvent.BLOCK_ATTACH, interactor);
            }

            return InteractionResult.CONSUME;
        }
    }

    public static ChainKnotEntity getOrCreateKnot(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7f, 1);

        for (ChainKnotEntity leashfenceknotentity : level.getEntitiesOfClass(ChainKnotEntity.class,
                new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D,
                        (double) k + 1.0D))) {
            if (leashfenceknotentity.getPos().equals(pos)) {
                return leashfenceknotentity;
            }
        }

        ChainKnotEntity newEntity = new ChainKnotEntity(level, pos);
        level.addFreshEntity(newEntity);
        return newEntity;
    }

    public static ChainKnotEntity bindEntityToNewOrExistingKnot(LivingEntity entity, Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7f, 1);

        for (ChainKnotEntity knot : level.getEntitiesOfClass(ChainKnotEntity.class,
                new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D,
                        (double) k + 1.0D))) {
            if (knot.getPos().equals(pos)) {
                ((IAnchorableEntity) entity).setAnchor(knot);
                // knot.addAnchored(entity);
                return knot;
            }
        }

        ChainKnotEntity newKnot = new ChainKnotEntity(level, pos);

        // newKnot.addAnchored(entity);
        ((IAnchorableEntity) entity).setAnchor(newKnot);

        level.addFreshEntity(newKnot);
        return newKnot;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, (double) Mth.floor(y) + 0.5D, (double) Mth.floor(z) + 0.5D);
    }

    @Override
    protected void setDirection(@Nonnull Direction pFacingDirection) {
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
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
    public boolean survives() {
        return this.level().getBlockState(this.pos).is(BlockTags.FENCES)
                || this.level().getBlockState(this.pos).is(Blocks.TRIPWIRE_HOOK);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, 0, this.getPos());
    }

    @Override
    public Vec3 getRopeHoldPosition(float partialTick) {
        return this.getPosition(partialTick).add(getLeashOffset(partialTick));
    }

    @Override
    public Vec3 getLeashOffset(float partialTick) {
        return new Vec3(0.0D, (double) getEyeHeight() + (!isOnFence() ? 0.2D : 0D), 0.0D);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.CHAIN);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.375D,
                (double) this.pos.getZ() + 0.5D);
        double d0 = (double) this.getType().getWidth() / 2.0D;
        double d1 = (double) this.getType().getHeight();
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0,
                this.getY() + d1, this.getZ() + d0));
    }
}
