package com.lazrproductions.cuffed.entity;



import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CrumblingBlockEntity extends Entity {
    static int CRUMBLE_STAGE_TIME = 20;

    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time = CRUMBLE_STAGE_TIME*5;
    
    
    protected static final EntityDataAccessor<Integer> DATA_CRUMBLE_PROGRESS = SynchedEntityData
            .defineId(CrumblingBlockEntity.class, EntityDataSerializers.INT);

    public CrumblingBlockEntity(EntityType<? extends CrumblingBlockEntity> type, Level level) {
        super(type, level);
    }

    public CrumblingBlockEntity(Level world, BlockPos pos, BlockState state, int crumbleProgress) {
        super(ModEntityTypes.CRUMBLING_BLOCK.get(), world);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
        
        setCrumbleProgress(crumbleProgress, 1);
        blockState = state;
        time = CRUMBLE_STAGE_TIME*5;
    }

    @SuppressWarnings("deprecation")
    public static CrumblingBlockEntity crumbleBlock(Level level, BlockPos pos, BlockState state, int crumbleStrength) {
        if(state.isSolid()) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            for (CrumblingBlockEntity e : level.getEntitiesOfClass(CrumblingBlockEntity.class,
                    new AABB((double) x - 1.0D, (double) y - 1.0D, (double) z - 1.0D, (double) x + 1.0D, (double) y + 1.0D, (double) z + 1.0D))) {
                if (e.blockPosition().equals(pos) && e.blockState == state) {
                    e.setCrumbleProgress(e.getCrumbleProgress() + crumbleStrength, 5);
                    return e;
                }
            }

            CrumblingBlockEntity e = new CrumblingBlockEntity(level, pos, state, crumbleStrength);
            level.addFreshEntity(e);
            return e;
        } else return null;
    }


    public void setCrumbleProgress(int b, int timeMul) {
        this.entityData.set(DATA_CRUMBLE_PROGRESS, b);
        this.time = CRUMBLE_STAGE_TIME * timeMul;
    }
    public int getCrumbleProgress() {
        return this.entityData.get(DATA_CRUMBLE_PROGRESS);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }


    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_CRUMBLE_PROGRESS, 1);
    }


    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        Level l = level();
        if (l.getBlockState(blockPosition()).isAir()) {
            this.discard();
        } else {
            if(getCrumbleProgress() < 5) {
                if(getCrumbleProgress()<=0) {
                    discard();
                } else {
                    // UNCOMMENT TO HAVE THE CRUMBLE REPAIR ITSELF
                    // if(time<=0) {
                    //     time = CRUMBLE_STAGE_TIME;
                    //     setCrumbleProgress(getCrumbleProgress() - 1, 1);
                    // }
                    // --this.time;
                }
            }
            else {
                l.destroyBlock(blockPosition(), true);
                this.discard();
            }
        }
    }


    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        tag.putInt("Time", this.time);
        tag.putInt("CrumbleProgress", getCrumbleProgress());
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        this.time = tag.getInt("Time");
        setCrumbleProgress(tag.getInt("CrumbleProgress"), 1);
    }


    
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}
