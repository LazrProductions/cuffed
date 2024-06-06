package com.lazrproductions.cuffed.mixin;

import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModDamageTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

@SuppressWarnings("null")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IAnchorableEntity {

    private static final EntityDataAccessor<Integer> DATA_ANCHOR_ID = SynchedEntityData.defineId(LivingEntity.class,
            EntityDataSerializers.INT);
    private static final String ANCHOR_TAG = "Anchor";


    @Nullable
    private Entity anchor = null;
    private boolean shouldBeLoaded = false;
    private UUID uuidToBeLoaded;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isAnchored() {
        return getAnchor() != null;
    }

    @Override
    public Entity getAnchor() {
        return anchor;
    }

    @Override
    public Entity getAnchorClientSide() {
        return level().getEntity(entityData.get(DATA_ANCHOR_ID));
    }

    @Override
    public void setAnchoredTo(@Nullable Entity e) {
        if (!level().isClientSide()) {
            if (e == null) {
                setAnchor(null);

                wasAnchored = false;

                ItemStack stack = new ItemStack(Items.CHAIN, 1);
                ItemEntity en = new ItemEntity(level(), this.position().x(), this.position().y(), this.position().z(),
                        stack);
                en.setDefaultPickUpDelay();
                level().addFreshEntity(en);
            } else 
                setAnchor(e);
        }
    }
    
    @Override
    public void setAnchor(@Nullable Entity e) {
        anchor = e;
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void onDefineSynchedData(CallbackInfo info) {
        entityData.define(DATA_ANCHOR_ID, -1);
    }

    boolean wasAnchored;
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo callback) {
        LivingEntity thisEntity = (LivingEntity)(Object)this;
        if(!level().isClientSide()) {
            if(shouldBeLoaded) {
                if(level() instanceof ServerLevel l) {
                    Entity a = l.getEntity(uuidToBeLoaded);
                    
                    setAnchor(a);

                    if(a!=null)
                        shouldBeLoaded = false;
                }
            }

            if((anchor == null || anchor.isRemoved())  && wasAnchored) {
                setAnchoredTo(null);
                wasAnchored = false;
            }

            if(anchor != null) {
                wasAnchored = true;
                entityData.set(DATA_ANCHOR_ID, anchor.getId());
            
                double minDist = CuffedMod.CONFIG.anchoringSettings.maxChainLength;;
                double maxDist = fallDistance > 0.2f ? CuffedMod.CONFIG.anchoringSettings.maxChainLength : CuffedMod.CONFIG.anchoringSettings.chainSuffocateLength;


                if (this.distanceTo(getAnchor()) > minDist) {
                    if(distanceTo(getAnchor()) > maxDist) {
                        hurt(ModDamageTypes.GetModSource(this, ModDamageTypes.HANG, anchor), 2);
                    }

                    float distance = distanceTo(getAnchor());

                    double dx = (getAnchor().getX() - getX()) / (double) distance;
                    double dy = (getAnchor().getY() - getY()) / (double) distance;
                    double dz = (getAnchor().getZ() - getZ()) / (double) distance;

                    setDeltaMovement(
                            Math.copySign(dx * dx * (distance / 5D) * .45, dx),
                            Math.copySign(dy * dy * (distance / 5D) * .45, dy),
                            Math.copySign(dz * dz * (distance / 5D) * .45, dz));
                }
            } else
                entityData.set(DATA_ANCHOR_ID, -1);
        } else if(thisEntity instanceof Player player && getAnchorClientSide() != null) {
            double minDist = CuffedMod.CONFIG.anchoringSettings.maxChainLength;

            if (this.distanceTo(getAnchorClientSide()) > minDist) {
                float distance = distanceTo(getAnchorClientSide());

                double dx = (getAnchorClientSide().getX() - getX()) / (double) distance;
                double dy = (getAnchorClientSide().getY() - getY()) / (double) distance;
                double dz = (getAnchorClientSide().getZ() - getZ()) / (double) distance;

                player.setDeltaMovement(
                        Math.copySign(dx * dx * (distance / 5D) * .45, dx),
                        Math.copySign(dy * dy * (distance / 5D) * .45, dy),
                        Math.copySign(dz * dz * (distance / 5D) * .45, dz));
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
        if (!level().isClientSide()) {
            if (isAnchored()) 
                tag.putUUID(ANCHOR_TAG, getAnchor().getUUID());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
        
        if (!level().isClientSide()) {
            if(tag.contains(ANCHOR_TAG)) {      
                uuidToBeLoaded = tag.getUUID(ANCHOR_TAG);
                shouldBeLoaded = true;          
            } else
                setAnchor(null);
        }

    }
}