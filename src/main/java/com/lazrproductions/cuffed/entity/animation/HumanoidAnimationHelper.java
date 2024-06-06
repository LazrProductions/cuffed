package com.lazrproductions.cuffed.entity.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;

@SuppressWarnings("unused")
public class HumanoidAnimationHelper {
    public static void animateArmsTiedBack(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1,
            float f2, float f3, float headYRot, float headXRot) {
        animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);

        ModelPart head = model.head;
        ModelPart body = model.body;
        ModelPart leftLeg = model.leftLeg;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftArm = model.leftArm;
        ModelPart rightArm = model.rightArm;

        if (!entity.isCrouching()) {
            rightArm.yRot = 40 * Mth.DEG_TO_RAD;
            rightArm.xRot = 40 * Mth.DEG_TO_RAD;

            leftArm.yRot = -40 * Mth.DEG_TO_RAD;
            leftArm.xRot = 40 * Mth.DEG_TO_RAD;
        } else {
            rightArm.yRot = 20 * Mth.DEG_TO_RAD;
            rightArm.xRot = 73 * Mth.DEG_TO_RAD;

            leftArm.yRot = -20 * Mth.DEG_TO_RAD;
            leftArm.xRot = 73 * Mth.DEG_TO_RAD;
        }
        
        model.hat.copyFrom(head);
    }

    public static void animateArmsTiedFront(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1,
            float f2, float f3, float headYRot, float headXRot) {
        animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);

        ModelPart head = model.head;
        ModelPart body = model.body;
        ModelPart leftLeg = model.leftLeg;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftArm = model.leftArm;
        ModelPart rightArm = model.rightArm;

        if(!entity.isCrouching()) {
            rightArm.yRot = -30 * Mth.DEG_TO_RAD;
            rightArm.xRot = -40 * Mth.DEG_TO_RAD;

            leftArm.yRot = 30 * Mth.DEG_TO_RAD;
            leftArm.xRot = -40 * Mth.DEG_TO_RAD;
        } else {
            rightArm.yRot = -90 * Mth.DEG_TO_RAD;
            rightArm.xRot = -17 * Mth.DEG_TO_RAD;

            leftArm.yRot = 90 * Mth.DEG_TO_RAD;
            leftArm.xRot = -17 * Mth.DEG_TO_RAD;
        }

        model.hat.copyFrom(head);
    }

    public static void animatePilloryDetainedAnimation(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1,
            float f2, float f3, float headYRot, float headXRot, float forwardRotation) {
        entity.setYBodyRot(forwardRotation);
        entity.setYHeadRot(forwardRotation);
        entity.setYRot(forwardRotation);
        
        animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);

        ModelPart head = model.head;
        ModelPart body = model.body;
        ModelPart leftLeg = model.leftLeg;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftArm = model.leftArm;
        ModelPart rightArm = model.rightArm;

        

        rightLeg.z = -2.0F;
        rightLeg.y = 14.2F;
        rightLeg.xRot = 10 * Mth.DEG_TO_RAD;
        rightLeg.yRot = 5 * Mth.DEG_TO_RAD;
        rightLeg.zRot = 10 * Mth.DEG_TO_RAD;

  
        leftLeg.y = 16.2F;
        leftLeg.xRot = 40 * Mth.DEG_TO_RAD;
        leftLeg.yRot = 5 * Mth.DEG_TO_RAD;
        leftLeg.zRot = -10 * Mth.DEG_TO_RAD;

        head.z = -7F; //Not set traditionally
        head.y = 9.6F;
        head.xRot = 40F * Mth.DEG_TO_RAD;

        body.y = 7.5F; 
        body.z = -5.2F; //Not set traditionally
        body.xRot = 50 * Mth.DEG_TO_RAD;

        leftArm.y = 8.3F;
        leftArm.z = -3F;
        leftArm.xRot = -70 * Mth.DEG_TO_RAD;
        leftArm.zRot = -10 * Mth.DEG_TO_RAD;

        rightArm.y = 8.3F;
        rightArm.z = -3F;
        rightArm.xRot = -70 * Mth.DEG_TO_RAD;
        rightArm.zRot = 10 * Mth.DEG_TO_RAD;

        head.yRot = 0;

        model.hat.copyFrom(head);
    }


    public static void animateDefaultHumanoid(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1,
            float f2, float f3, float headYRot, float headXRot) {
        ModelPart head = model.head;
        ModelPart body = model.body;
        ModelPart leftLeg = model.leftLeg;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftArm = model.leftArm;
        ModelPart rightArm = model.rightArm;

        head.z = 0;
        body.z = 0;

        boolean flag = entity.getFallFlyingTicks() > 4;

        head.yRot = headYRot * ((float) Math.PI / 180F);
        head.xRot = headXRot * ((float) Math.PI / 180F);

        body.yRot = 0.0F;
        rightArm.z = 0.0F;
        rightArm.x = -5.0F;
        leftArm.z = 0.0F;
        leftArm.x = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        rightArm.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 2.0F * f2 * 0.5F / f;
        leftArm.xRot = Mth.cos(f1 * 0.6662F) * 2.0F * f2 * 0.5F / f;
        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;
        rightLeg.xRot = Mth.cos(f1 * 0.6662F) * 1.4F * f2 / f;
        leftLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 1.4F * f2 / f;
        rightLeg.yRot = 0.005F;
        leftLeg.yRot = -0.005F;
        rightLeg.zRot = 0.005F;
        leftLeg.zRot = -0.005F;

        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;

        if (entity.isCrouching()) {
            body.xRot = 0.5F;
            rightArm.xRot += 0.4F;
            leftArm.xRot += 0.4F;
            rightLeg.z = 4.0F;
            leftLeg.z = 4.0F;
            rightLeg.y = 12.2F;
            leftLeg.y = 12.2F;
            head.y = 4.2F;
            body.y = 3.2F;
            leftArm.y = 5.2F;
            rightArm.y = 5.2F;
        } else {
            body.xRot = 0.0F;
            rightLeg.z = 0.0F;
            leftLeg.z = 0.0F;
            rightLeg.y = 12.0F;
            leftLeg.y = 12.0F;
            head.y = 0.0F;
            body.y = 0.0F;
            leftArm.y = 2.0F;
            rightArm.y = 2.0F;
        }

        model.hat.copyFrom(head);
    }

    public static void animateArmsTiedFrontFirstPerson(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1,
            float f2, float f3, float headYRot, float headXRot) {
        //renderDefaultHumanoidAnimation(entity, model, f1, f2, f3, headYRot, headXRot);
        

        ModelPart head = model.head;
        ModelPart body = model.body;
        ModelPart leftLeg = model.leftLeg;
        ModelPart rightLeg = model.rightLeg;
        ModelPart leftArm = model.leftArm;
        ModelPart rightArm = model.rightArm;

        model.setAllVisible(false);
    }
}