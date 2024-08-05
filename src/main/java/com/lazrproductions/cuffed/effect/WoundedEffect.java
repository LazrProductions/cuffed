package com.lazrproductions.cuffed.effect;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModParticleTypes;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class WoundedEffect extends MobEffect {

    static final UUID ATTRIBUTE_HEALTH_UUID = UUID.fromString("b4d14f56-f45a-4966-8b05-59d1862caa5f");
    static final UUID ATTRIBUTE_SLOWNESS_UUID = UUID.fromString("e14e9c5e-e0cc-4414-aa0d-a59afacd67b1");

    public WoundedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    
    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        
        if(entity.getRandom().nextFloat() < ((double)amplifier / 100D)) {
            double x = entity.position().x() + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            double y = entity.position().y() + 1 + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            double z = entity.position().z() + (entity.getRandom().nextFloat() * 0.8D) -0.4D;
            entity.level().addParticle(ModParticleTypes.BLOOD_DRIP_FALL_PARTICLE.get(), x, y, z, 0, 0, 0);
        }
    }

    @Override
    public void removeAttributeModifiers(@Nonnull LivingEntity entity, @Nonnull AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);

        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance slowness = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if(health != null) {
            health.removeModifier(ATTRIBUTE_HEALTH_UUID);
            entity.hurt(entity.damageSources().magic(), 1);
        }
        
        if(slowness != null) {
            slowness.removeModifier(ATTRIBUTE_SLOWNESS_UUID);
        }
    }
    @Override
    public void addAttributeModifiers(@Nonnull LivingEntity entity, @Nonnull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);

        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        double reductionAmount = -(double)amplifier / 100D;
        if(health != null) {
            health.removeModifier(ATTRIBUTE_HEALTH_UUID);
            health.addPermanentModifier(new AttributeModifier(ATTRIBUTE_HEALTH_UUID, "woundedHealthReduction", reductionAmount, Operation.MULTIPLY_BASE));

            entity.hurt(entity.damageSources().magic(), 1);
        }

        AttributeInstance slowness = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if(slowness != null) {
            slowness.removeModifier(ATTRIBUTE_SLOWNESS_UUID);
            slowness.addPermanentModifier(new AttributeModifier(ATTRIBUTE_SLOWNESS_UUID, "woundedSpeedReduction", reductionAmount * 0.8D, Operation.MULTIPLY_TOTAL));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


    public static void woundEntity(@Nonnull LivingEntity entity, int percentage, boolean overwrite) {
        MobEffectInstance inst = entity.getEffect(ModEffects.WOUNDED_EFFECT.get());
        
        int amplifier = percentage;
        if(inst != null) {
            if(!overwrite) {
                int oldPercentage = inst.getAmplifier();
                amplifier = Mth.clamp(oldPercentage + percentage, 0, 100);
            }
        }

        MobEffectInstance newInst = new MobEffectInstance(ModEffects.WOUNDED_EFFECT.get(), -1, amplifier); 
        entity.removeEffect(ModEffects.WOUNDED_EFFECT.get());
        entity.addEffect(newInst);
    }
    public static void woundEntity(@Nonnull LivingEntity entity, int percentage) {
        woundEntity(entity, percentage, false);
    }
}
