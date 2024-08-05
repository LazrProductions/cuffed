package com.lazrproductions.cuffed.mixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.blocks.base.DetentionBlock;
import com.lazrproductions.cuffed.effect.RestrainedEffectInstance;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.entity.base.IPrivacyOperand;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Player.class)
public class PlayerMixin extends LivingEntity implements IRestrainableEntity, IDetainableEntity, INicknamable, IPrivacyOperand {
    
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }
    
    private static final EntityDataAccessor<Boolean> DATA_HAS_NICKNAME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Component> DATA_NICKNAME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPONENT);
    private static final String NICKNAME_TAG = "Nickname";


    private static final EntityDataAccessor<Integer> DATA_RESTRAINT_CODE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_ARM_RESTRAINT_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_LEG_RESTRAINT_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> DATA_ARM_ENCHANTED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LEG_ENCHANTED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_DETAINED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_DETAINED_TO_BLOCK = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Float> DATA_DETAINED_ROTATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vector3f> DATA_DETAINED_POSITION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.VECTOR3);
    private static final String DETAINED_TAG = "Detained";
    private static final String DETAINED_ID_TAG = "Id";
    private static final String DETAINED_ROTATION_TAG = "Rotation";
    private static final String DETAINED_TO_BLOCK_TAG = "Block";
    private static final String DETAINED_POSITION_X_TAG = "X";
    private static final String DETAINED_POSITION_Y_TAG = "Y";
    private static final String DETAINED_POSITION_Z_TAG = "Z";
    
    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    protected void defineSynchedData(CallbackInfo callback) {
        entityData.define(DATA_HAS_NICKNAME, false);
        entityData.define(DATA_NICKNAME, Component.empty());

        entityData.define(DATA_RESTRAINT_CODE, 0); // used for synching restraint proprties to clients.
        entityData.define(DATA_ARM_RESTRAINT_ID, ""); // used for displaying arm restraint models
        entityData.define(DATA_LEG_RESTRAINT_ID, ""); // used for displaying leg restraint models

        entityData.define(DATA_ARM_ENCHANTED, false);
        entityData.define(DATA_LEG_ENCHANTED, false);

        entityData.define(DATA_DETAINED, -1);
        entityData.define(DATA_DETAINED_ROTATION, 0f);
        entityData.define(DATA_DETAINED_TO_BLOCK, BlockPos.ZERO);
        entityData.define(DATA_DETAINED_POSITION, new Vector3f());
    }

    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    public void getName(CallbackInfoReturnable<Component> callback) {
        if(entityData.get(DATA_HAS_NICKNAME)) {
            Component n = getNickname();
            if (n != null)
                callback.setReturnValue(n);
        }
    }
    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> callback) {
        if(entityData.get(DATA_HAS_NICKNAME)) {
            Component n = getNickname();
            if (n != null)
                callback.setReturnValue(n);
        }
    }
    
    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo callback) {
        if (!this.level().isClientSide()) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability((Player)(Object)this);
            
            if(cap!=null) {
                setArmRestraintId(cap.getArmRestraintId());
                setLegRestraintId(cap.getLegRestraintId());

                setArmsEnchanted(cap.getArmRestraint() instanceof IEnchantableRestraint e && e.getEnchantments() != null && e.getEnchantments().size() > 0);
                setLegsEnchanted(cap.getLegRestraint() instanceof IEnchantableRestraint e && e.getEnchantments() != null && e.getEnchantments().size() > 0);
            }

            if(!this.hasEffect(ModEffects.RESTRAINED_EFFECT.get()))
                setRestraintCode(0);
            else if(this.getEffect(ModEffects.RESTRAINED_EFFECT.get()) instanceof RestrainedEffectInstance i)
                setRestraintCode(i.getAmplifier());
        
            if(getDetained() > -1) {
                this.setYBodyRot(getDetainedRotation());
                this.setYRot(getDetainedRotation());
                this.teleportTo(getDetainedPosition().x(), getDetainedPosition().y(), getDetainedPosition().z());

                BlockState state = getBlockDetainedTo(level()); 
                boolean flag = state.getBlock() instanceof DetentionBlock;
                boolean flag1 = false;
                if(state.getBlock() instanceof DetentionBlock detentionBlock)
                    flag1 = detentionBlock.canDetainPlayer(level(), state, entityData.get(DATA_DETAINED_TO_BLOCK), (Player)(Object)this);

                if(!flag || !flag1)
                    undetain();
            }    
        }
    }

    @Inject(at = @At("HEAD"), method = "wantsToStopRiding", cancellable = true)
    protected void wantsToStopRiding(CallbackInfoReturnable<Boolean> callback) {
        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability((Player)(Object)this);
        if(cap.restraintsDisabledMovement())
            callback.setReturnValue(false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo callback) {
        CompoundTag detainedTag = new CompoundTag();
        detainedTag.putInt(DETAINED_ID_TAG, getDetained());
        detainedTag.putFloat(DETAINED_ROTATION_TAG, getDetainedRotation());
        BlockPos blockPos = entityData.get(DATA_DETAINED_TO_BLOCK);
        detainedTag.putIntArray(DETAINED_TO_BLOCK_TAG, new int[] { blockPos.getX(), blockPos.getY(), blockPos.getZ() });
        Vector3f pos = entityData.get(DATA_DETAINED_POSITION);
        detainedTag.putFloat(DETAINED_POSITION_X_TAG, pos.x());
        detainedTag.putFloat(DETAINED_POSITION_Y_TAG, pos.y());
        detainedTag.putFloat(DETAINED_POSITION_Z_TAG, pos.z());
        
        tag.put(DETAINED_TAG, detainedTag);

        if(CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_LOGOUT.get() && getNickname() != null)
            tag.putString(NICKNAME_TAG, serializeNickname());
        
        tag.put(TAG_RESTRICTIONS, serializeRestrictions());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo callback) {
        if(tag.contains(DETAINED_TAG)) {
            CompoundTag t = tag.getCompound(DETAINED_TAG);
            setDetained(t.getInt(DETAINED_ID_TAG));
            setDetainedRotation(t.getFloat(DETAINED_ROTATION_TAG));
            int[] a = t.getIntArray(DETAINED_TO_BLOCK_TAG);
            setBlockDetainedTo(new BlockPos(a[0], a[1], a[2]));
            setDetainedPosition(new Vector3f(t.getFloat(DETAINED_POSITION_X_TAG), t.getFloat(DETAINED_POSITION_Y_TAG), t.getFloat(DETAINED_POSITION_Z_TAG)));
        }

        if(CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_LOGOUT.get() && tag.contains(NICKNAME_TAG))
            setNickname(Component.Serializer.fromJson(tag.getString(NICKNAME_TAG)));
        else
            setNickname(null);

        deserializeRestrictions(tag);
    }




    //#region Nicknamable Data Management
    
    public Component getNickname() {
        if(entityData.get(DATA_HAS_NICKNAME))
            return entityData.get(DATA_NICKNAME);
        return null;
    }
    public void setNickname(@Nullable Component value) {
        if(value == null) {
            entityData.set(DATA_HAS_NICKNAME, false);
            entityData.set(DATA_NICKNAME, Component.empty());
        } else {
            entityData.set(DATA_HAS_NICKNAME, true);
            entityData.set(DATA_NICKNAME, value);
        }
    }
    public String serializeNickname() {
        return Component.Serializer.toJson(getNickname());
    }
    public void deserializeNickname(CompoundTag tag) {
        if(tag.contains(NICKNAME_TAG))
            setNickname(Component.Serializer.fromJson(tag.getString(NICKNAME_TAG)));
    }
    public void deserializeNickname(String nickTag) {
        setNickname(Component.Serializer.fromJson(nickTag));
    }

    //#endregion

    //#region Restraint Data Management    

    @Override
    public boolean isRestrained() {
        return entityData.get(DATA_RESTRAINT_CODE) > 0;
    }
    @Override
    public int getRestraintCode() {
        return entityData.get(DATA_RESTRAINT_CODE);
    }
    @Override
    public String getArmRestraintId() {
        return entityData.get(DATA_ARM_RESTRAINT_ID);
    }
    @Override
    public String getLegRestraintId() {
        return entityData.get(DATA_LEG_RESTRAINT_ID);
    }
    @Override
    public void setRestraintCode(int v) {
        entityData.set(DATA_RESTRAINT_CODE, v);
    }
    @Override
    public void setArmRestraintId(String v) {
        entityData.set(DATA_ARM_RESTRAINT_ID, v);
    }
    @Override
    public void setLegRestraintId(String v) {
        entityData.set(DATA_LEG_RESTRAINT_ID, v);
    }
    

    @Override
    public boolean getArmsAreEnchanted() {
        return entityData.get(DATA_ARM_ENCHANTED);
    }
    @Override
    public boolean getLegsAreEnchanted() {
        return entityData.get(DATA_LEG_ENCHANTED);
    }
    @Override
    public void setArmsEnchanted(boolean v) {
        entityData.set(DATA_ARM_ENCHANTED, v);
    }
    @Override
    public void setLegsEnchanted(boolean v) {
        entityData.set(DATA_LEG_ENCHANTED, v);
    }


    //#endregion

    //#region Detainable Data Management

    public void detainToBlock(@Nonnull Level level, Vector3f detainPos, @Nonnull BlockPos pos, int detaintType, float facingRotation) {
        setDetained(detaintType);
        setDetainedRotation(facingRotation);
        setBlockDetainedTo(pos);
        setDetainedPosition(detainPos);
    }
    public void undetain() {
        setDetained(-1);
        setDetainedRotation(0);
        setBlockDetainedTo(BlockPos.ZERO);
        setDetainedPosition(new Vector3f(0,0,0));
    }

    public int getDetained() {
        return entityData.get(DATA_DETAINED);
    }
    public void setDetained(int value) {
        entityData.set(DATA_DETAINED, value);
    }

    public float getDetainedRotation() {
        return entityData.get(DATA_DETAINED_ROTATION);
    }
    public void setDetainedRotation(float value) {
        entityData.set(DATA_DETAINED_ROTATION, value);
    }

    public BlockState getBlockDetainedTo(@Nonnull Level level) {
        return level.getBlockState(entityData.get(DATA_DETAINED_TO_BLOCK));
    }
    public void setBlockDetainedTo(@Nonnull BlockPos pos) {
        entityData.set(DATA_DETAINED_TO_BLOCK, pos);
    }

    public Vector3f getDetainedPosition() {
        return entityData.get(DATA_DETAINED_POSITION);
    }
    public void setDetainedPosition(Vector3f value) {
        entityData.set(DATA_DETAINED_POSITION, value);
    }

    //#endregion

    //#region Privacy Data Management
    final HashMap<String, PrivacyRestriction> privacyRestrictions = new HashMap<>();

    public PrivacyRestriction getAnchoringRestrictions() {
        return getRestriction(ANCHORING_RESTRICTION);
    }
    public void setAnchoringRestrictions(PrivacyRestriction newRestriction) {
        setRestriction(ANCHORING_RESTRICTION, newRestriction);
    }

    public PrivacyRestriction getDetainingRestrictions() {
        return getRestriction(DETAINING_RESTRICTION);
    }
    public void setDetainingRestrictions(PrivacyRestriction newRestriction) {
        setRestriction(DETAINING_RESTRICTION, newRestriction);
    }

    public PrivacyRestriction getNicknamingRestrictions() {
        return getRestriction(NICKNAMING_RESTRICTION);
    }
    public void setNicknamingRestrictions(PrivacyRestriction newRestriction) {
        setRestriction(NICKNAMING_RESTRICTION, newRestriction);
    }

    public PrivacyRestriction getRestrainingRestrictions() {
        return getRestriction(RESTRAINING_RESTRICTION);
    }
    public void setRestrainingRestrictions(PrivacyRestriction newRestriction) {
        setRestriction(RESTRAINING_RESTRICTION, newRestriction);
    }

    public void setRestriction(String key, PrivacyRestriction newRestriction) {
        this.privacyRestrictions.put(key, newRestriction);
    }
    public PrivacyRestriction getRestriction(String key) {
        return this.privacyRestrictions.get(key);
    }

    public void deserializeRestrictions(CompoundTag tag) {
        if(tag.contains(TAG_RESTRICTIONS)) {
            privacyRestrictions.clear();
            if(tag.size() == 0) {
                ListTag list = tag.getList(TAG_RESTRICTIONS, 10);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag t = list.getCompound(i);
                    privacyRestrictions.put(t.getString(TAG_RESTRICTION_ID), PrivacyRestriction.fromInteger(t.getInt(TAG_RESTRICTION)));
                }
                return;
            }
        }
        
        writeDefaultRestrictions();
    }
    public ListTag serializeRestrictions() {
        ListTag list = new ListTag();
        if(privacyRestrictions.size() == 0)
            writeDefaultRestrictions();

        for (Entry<String, PrivacyRestriction> r : privacyRestrictions.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt(TAG_RESTRICTION, r.getValue().toInteger());
            tag.putString(TAG_RESTRICTION_ID, r.getKey());
            list.add(tag);
        }

        return list;
    }

    public void writeDefaultRestrictions() {
        privacyRestrictions.clear();
        privacyRestrictions.put(ANCHORING_RESTRICTION, PrivacyRestriction.NEVER);
        privacyRestrictions.put(DETAINING_RESTRICTION, PrivacyRestriction.NEVER);
        privacyRestrictions.put(NICKNAMING_RESTRICTION, PrivacyRestriction.NEVER);
        privacyRestrictions.put(RESTRAINING_RESTRICTION, PrivacyRestriction.NEVER);
    }
    //#endregion

    @Shadow
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }
    @Shadow
    public ItemStack getItemBySlot(@Nonnull EquipmentSlot p_21127_) {
        return null;
    }
    @Shadow
    public void setItemSlot(@Nonnull EquipmentSlot p_21036_, @Nonnull ItemStack p_21037_) {
    }
    @Shadow
    public HumanoidArm getMainArm() {
        return null;
    }
}
