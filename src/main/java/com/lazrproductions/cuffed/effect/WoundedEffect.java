package com.lazrproductions.cuffed.effect;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModParticleTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class WoundedEffect extends MobEffect {

    static final ResourceLocation ATTRIBUTE_HEALTH_RL = ResourceLocation.fromNamespaceAndPath("cuffed", "wounded_health_reduction");
    static final ResourceLocation ATTRIBUTE_SLOWNESS_RL = ResourceLocation.fromNamespaceAndPath("cuffed", "wounded_speed_reduction");

    public WoundedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    
    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        if(entity.getRandom().nextFloat() < ((double)amplifier / 100D)) {
            double x = entity.position().x() + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            double y = entity.position().y() + 1 + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            double z = entity.position().z() + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            entity.level().addParticle(ModParticleTypes.BLOOD_DRIP_FALL_PARTICLE.get(), x, y, z, 0, 0, 0);
        }
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), 1);
    }

    @Override
    public void removeAttributeModifiers(@Nonnull AttributeMap attributes) {
        super.removeAttributeModifiers(attributes);

        AttributeInstance health = attributes.getInstance(Attributes.MAX_HEALTH);
        AttributeInstance slowness = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        if(health != null) {
            health.removeModifier(ATTRIBUTE_HEALTH_RL);
        }
        
        if(slowness != null) {
            slowness.removeModifier(ATTRIBUTE_SLOWNESS_RL);
        }
    }

    @Override
    public void addAttributeModifiers(@Nonnull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);

        AttributeInstance health = attributeMap.getInstance(Attributes.MAX_HEALTH);
        double reductionAmount = -(double)amplifier / 100D;
        if(health != null) {
            health.removeModifier(ATTRIBUTE_HEALTH_RL);
            health.addPermanentModifier(new AttributeModifier(ATTRIBUTE_HEALTH_RL, reductionAmount, Operation.ADD_MULTIPLIED_BASE));
        }

        AttributeInstance slowness = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if(slowness != null) {
            slowness.removeModifier(ATTRIBUTE_SLOWNESS_RL);
            slowness.addPermanentModifier(new AttributeModifier(ATTRIBUTE_SLOWNESS_RL, reductionAmount * 0.8D, Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }


    public static void woundEntity(@Nonnull LivingEntity entity, int percentage, boolean overwrite) {
        MobEffectInstance inst = entity.getEffect(ModEffects.WOUNDED_EFFECT);
        
        int amplifier = percentage;
        if(inst != null) {
            if(!overwrite) {
                int oldPercentage = inst.getAmplifier();
                amplifier = Mth.clamp(oldPercentage + percentage, 0, 100);
            }
        }

        MobEffectInstance newInst = new MobEffectInstance(ModEffects.WOUNDED_EFFECT, -1, amplifier); 
        entity.removeEffect(ModEffects.WOUNDED_EFFECT);
        entity.addEffect(newInst);
    }
    public static void woundEntity(@Nonnull LivingEntity entity, int percentage) {
        woundEntity(entity, percentage, false);
    }
    public static void treatEntity(@Nonnull LivingEntity entity) {
        if(entity.hasEffect(ModEffects.WOUNDED_EFFECT))
            entity.removeEffect(ModEffects.WOUNDED_EFFECT);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 2));
    }
}
