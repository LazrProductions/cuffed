package com.lazrproductions.cuffed.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

public class WeightedAnchorEntity extends LivingEntity {

    private static final EntityDataAccessor<Boolean> DATA_ENCHANTED = SynchedEntityData.defineId(WeightedAnchorEntity.class, EntityDataSerializers.BOOLEAN);


    public WeightedAnchorEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }
    public WeightedAnchorEntity(Level world, BlockPos pos) {
        super(ModEntityTypes.WEIGHTED_ANCHOR.get(), world);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    public static WeightedAnchorEntity createFromItem(@Nonnull Level level, @Nonnull ItemStack stack, @Nonnull BlockPos pos) {
        WeightedAnchorEntity entity = new WeightedAnchorEntity(level, pos);

        entity.setEnchantments(stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY));

        return entity;
    }

    public ItemStack getDroppedItem() {
        ItemStack stack = new ItemStack(ModItems.WEIGHTED_ANCHOR_ITEM.get(), 1);
        stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, getEnchantments());
        return stack;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ENCHANTED, false);
    }
    @Override
    public void tick() {
        if(!level().isClientSide()) {
            entityData.set(DATA_ENCHANTED, getEnchantments().size() > 0);
            if(this.isInWaterOrBubble() && getEnchantmentLevel(ModEnchantments.BUOYANT) >= 1)
                this.addDeltaMovement(new Vec3(0f, 0.023f, 0f));
         }
  
        super.tick();

    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }
    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {

        if(source.is(DamageTypes.GENERIC_KILL))
            return false;
        return true;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_FALL);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return getDroppedItem();
    }

    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0.0D, 8D / 16D, 0);
    }
    @Override
    public Vec3 getRopeHoldPosition(float p_20347_) {
        return position().add(getLeashOffset());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {

        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if(hand == InteractionHand.MAIN_HAND) {
            if(!interactor.isCrouching() && CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS.get()) {
                boolean flag = false;
                double maxDist = CuffedMod.SERVER_CONFIG.ANCHORING_SUFFOCATION_LENGTH.get() + 5;
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class,
                        new AABB(this.getX() - maxDist - 2.0D, this.getY() - maxDist - 2.0D, this.getZ() - maxDist - 2.0D,
                                this.getX() + maxDist + 2.0D, this.getY() + maxDist + 2.0D, this.getZ() + maxDist + 2.0D));

                for (LivingEntity entity : list) {
                    IAnchorableEntity anchorableEntity = (IAnchorableEntity) entity;
                    if (anchorableEntity.getAnchor() == interactor) {
                        anchorableEntity.setAnchoredTo(this);
                        flag = true;
                    }
                }

                boolean flag1 = false;
                if (!flag) {
                    for (LivingEntity entity : list) {
                        IAnchorableEntity anchorableEntity = (IAnchorableEntity) entity;
                        if (anchorableEntity.isAnchored() && anchorableEntity.getAnchor() == this) {
                            anchorableEntity.setAnchoredTo(null);
                            flag1 = true;
                        }
                    }
                    if(flag1)
                        level().playSound(null, blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7f, 1);
                }

                if(flag)
                    level().playSound(null, blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7f, 1);
                if(flag1)
                    level().playSound(null, blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7f, 1);

                if (flag || flag1) {
                    this.gameEvent(GameEvent.BLOCK_ATTACH, interactor);
                }

                return InteractionResult.CONSUME;
            } else {
                ItemStack stack = getDroppedItem();
                ItemEntity entity = new ItemEntity(level(), position().x(), position().y(), position().z(), stack); 
                level().addFreshEntity(entity);
                
                level().playSound(null, blockPosition(), SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 0.7f, 1);
                this.discard();
            }
        }

        return InteractionResult.FAIL;
    }


    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        tag.put("Enchantments", net.minecraft.world.item.enchantment.ItemEnchantments.CODEC.encodeStart(this.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), this.enchantments).getOrThrow());
        super.addAdditionalSaveData(tag);
    }
    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        if(tag.contains("Enchantments")) {
            this.enchantments = net.minecraft.world.item.enchantment.ItemEnchantments.CODEC.parse(this.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), tag.get("Enchantments")).getOrThrow();
        } else {
            this.enchantments = net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY;
        }
        super.readAdditionalSaveData(tag);
    }


    //#region Enchantment Management

    net.minecraft.world.item.enchantment.ItemEnchantments enchantments = net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY;

    public net.minecraft.world.item.enchantment.ItemEnchantments getEnchantments() {
        return enchantments;
    }
    public void setEnchantments(net.minecraft.world.item.enchantment.ItemEnchantments tag) {
        enchantments = tag;
    }

    public boolean hasEnchantment(net.minecraft.core.Holder<Enchantment> enchantment) {
        return this.getEnchantmentLevel(enchantment) > 0;
    }

    public boolean hasEnchantment(net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey) {
        return this.getEnchantmentLevel(enchantmentKey) > 0;
    }

    public int getEnchantmentLevel(net.minecraft.core.Holder<Enchantment> enchantment) {
        return this.enchantments.getLevel(enchantment);
    }

    public int getEnchantmentLevel(net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey) {
        var opt = this.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolder(enchantmentKey);
        if (opt.isPresent()) {
            return this.getEnchantmentLevel(opt.get());
        }
        return 0;
    }

    public void enchant(net.minecraft.core.Holder<Enchantment> enchantment, int value) {
        net.minecraft.world.item.enchantment.ItemEnchantments.Mutable mutable = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(this.enchantments);
        mutable.set(enchantment, value);
        this.enchantments = mutable.toImmutable();
    }

    public boolean getIsEnchanted() {
        return entityData.get(DATA_ENCHANTED);
    }

    //#endregion


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0F);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>(0);
    }

    @Override
    public ItemStack getItemBySlot(@Nonnull EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@Nonnull EquipmentSlot p_21036_, @Nonnull ItemStack p_21037_) {
        
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
