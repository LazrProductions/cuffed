package com.lazrproductions.cuffed.effect;

import javax.annotation.Nonnull;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class RestrainedEffect extends MobEffect {

    public RestrainedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void removeAttributeModifiers(@Nonnull LivingEntity entity, @Nonnull AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);

        AttributeInstance attackSpeed = attributes.getInstance(Attributes.ATTACK_SPEED);
        AttributeInstance moveSpeed = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimSpeed = attributes.getInstance(ForgeMod.SWIM_SPEED.get());

        if(attackSpeed != null) attackSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_UUID);
        if(moveSpeed != null) moveSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_UUID);
        if(swimSpeed != null) swimSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_UUID);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
