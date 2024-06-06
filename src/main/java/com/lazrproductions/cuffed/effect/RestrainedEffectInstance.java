package com.lazrproductions.cuffed.effect;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;

public class RestrainedEffectInstance extends MobEffectInstance {

    public static final UUID NEGATIVE_MINE_SPEED_UUID = UUID.fromString("bc5de830-355c-419e-9b16-a54b74e8ebe4");
    public static final UUID NEGATIVE_MOVE_SPEED_UUID = UUID.fromString("a42a112a-a81f-43be-8e00-a35c1e646494");
    public static final UUID NEGATIVE_SWIM_SPEED_UUID = UUID.fromString("7ccc7ac4-bc59-4467-ad64-fca99c9e0413");

    public boolean noMining;
    public boolean noItemUse;
    public boolean noMovement;
    public boolean noJumping;

    public RestrainedEffectInstance(int duration, int restraintType) {
        super(ModEffects.RESTRAINED_EFFECT.get(), duration, restraintType);
        
        boolean[] decodedValues = decodeRestraintProperties(restraintType);
        this.noMining = decodedValues[0];
        this.noItemUse = decodedValues[1];
        this.noMovement = decodedValues[2];
        this.noJumping = decodedValues[3];
    }

    private boolean hasRemainingDuration() {
        return this.isInfiniteDuration() || this.getDuration() > 0;
    }

    @Override
    public void applyEffect(@Nonnull LivingEntity entity) {
        if (this.hasRemainingDuration()) {
            super.applyEffect(entity);

            AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
            AttributeInstance moveSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance swimSpeed = entity.getAttribute(ForgeMod.SWIM_SPEED.get());

            if(noMining&&!entity.getAttributes().hasModifier(Attributes.ATTACK_SPEED, NEGATIVE_MINE_SPEED_UUID) && attackSpeed != null)
                attackSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_MINE_SPEED_UUID, "restrainMineSpeed", -9.0, Operation.MULTIPLY_TOTAL));

            if(noMovement&&!entity.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED, NEGATIVE_MOVE_SPEED_UUID) && moveSpeed != null)
                moveSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_MOVE_SPEED_UUID, "restrainMovementSpeed", -9.0, Operation.MULTIPLY_TOTAL));
                
            if(noMovement&&!entity.getAttributes().hasModifier(ForgeMod.SWIM_SPEED.get(), NEGATIVE_SWIM_SPEED_UUID) && swimSpeed != null)
            swimSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_SWIM_SPEED_UUID, "restrainSwimSpeed", -9.0, Operation.MULTIPLY_TOTAL));
        } else {
            AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
            AttributeInstance moveSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance swimSpeed = entity.getAttribute(ForgeMod.SWIM_SPEED.get());

            if(noMining&&!entity.getAttributes().hasModifier(Attributes.ATTACK_SPEED, NEGATIVE_MINE_SPEED_UUID) && attackSpeed != null)
                attackSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_MINE_SPEED_UUID, "restrainMineSpeed", -9.0, Operation.MULTIPLY_TOTAL));

            if(noMovement&&!entity.getAttributes().hasModifier(Attributes.ATTACK_SPEED, NEGATIVE_MOVE_SPEED_UUID) && moveSpeed != null)
                moveSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_MOVE_SPEED_UUID, "restrainMovementSpeed", -9.0, Operation.MULTIPLY_TOTAL));
                
            if(noMovement&&!entity.getAttributes().hasModifier(ForgeMod.SWIM_SPEED.get(), NEGATIVE_SWIM_SPEED_UUID) && swimSpeed != null)
                swimSpeed.addTransientModifier(new AttributeModifier(NEGATIVE_SWIM_SPEED_UUID, "restrainSwimSpeed", -9.0, Operation.MULTIPLY_TOTAL));
        }
    }

    @Override
    public String getDescriptionId() {
        return "effect.cuffed.restrained";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    public static int encodeRestraintProperties(boolean noMining, boolean noItemUse, boolean noMovement, boolean noJumping) {
        int encodedValue = 0;
        if (noMining) encodedValue |= 1 << 0;
        if (noItemUse) encodedValue |= 1 << 1;
        if (noMovement) encodedValue |= 1 << 2;
        if (noJumping) encodedValue |= 1 << 3;
        return encodedValue;
    }
    
    public static boolean[] decodeRestraintProperties(int v) {
        boolean[] decodedValues = new boolean[4];
        decodedValues[0] = (v & (1 << 0)) != 0;
        decodedValues[1] = (v & (1 << 1)) != 0;
        decodedValues[2] = (v & (1 << 2)) != 0;
        decodedValues[3] = (v & (1 << 3)) != 0;
        return decodedValues;
    }
    
    public static boolean decodeNoMining(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[0];
    }
    public static boolean decodeNoItemUse(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[1];
    }
    public static boolean decodeNoMovement(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[2];
    }
    public static boolean decodeNoJumping(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[3];
    }
}
