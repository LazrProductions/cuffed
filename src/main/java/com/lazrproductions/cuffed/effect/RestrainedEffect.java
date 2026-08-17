package com.lazrproductions.cuffed.effect;

import javax.annotation.Nonnull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RestrainedEffect extends MobEffect {

    public RestrainedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void removeAttributeModifiers(@Nonnull AttributeMap attributes) {
        super.removeAttributeModifiers(attributes);

        AttributeInstance attackSpeed = attributes.getInstance(Attributes.ATTACK_SPEED);
        AttributeInstance moveSpeed = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimSpeed = attributes.getInstance(net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED);

        if(attackSpeed != null) attackSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_ID);
        if(moveSpeed != null) moveSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_ID);
        if(swimSpeed != null) swimSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_ID);
    }

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        boolean[] decodedValues = RestrainedEffectInstance.decodeRestraintProperties(amplifier);
        boolean noMining = decodedValues[0];
        boolean noMovement = decodedValues[2];

        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance moveSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimSpeed = entity.getAttribute(net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED);

        if(noMining && attackSpeed != null && !attackSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_ID))
            attackSpeed.addTransientModifier(new AttributeModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_ID, -9.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if(!noMining && attackSpeed != null && attackSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_ID))
            attackSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MINE_SPEED_ID);

        if(noMovement && moveSpeed != null && !moveSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_ID))
            moveSpeed.addTransientModifier(new AttributeModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_ID, -9.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if(!noMovement && moveSpeed != null && moveSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_ID))
            moveSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_MOVE_SPEED_ID);
            
        if(noMovement && swimSpeed != null && !swimSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_ID))
            swimSpeed.addTransientModifier(new AttributeModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_ID, -9.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if(!noMovement && swimSpeed != null && swimSpeed.hasModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_ID))
            swimSpeed.removeModifier(RestrainedEffectInstance.NEGATIVE_SWIM_SPEED_ID);

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
