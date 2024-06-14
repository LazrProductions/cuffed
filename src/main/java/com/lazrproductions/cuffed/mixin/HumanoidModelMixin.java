package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.HumanoidAnimationHelper;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.Restraints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow
    public ModelPart head;
    @Shadow
    public ModelPart hat;
    @Shadow
    public ModelPart body;
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;
    @Shadow
    public ModelPart rightLeg;
    @Shadow
    public ModelPart leftLeg;

    @SuppressWarnings("unchecked")
    @Inject(at = @At("HEAD"), method = "setupAnim", cancellable = true)
    private void setupAnim(T entity, float f1, float f2, float f3, float headYRot, float headXRot,
            CallbackInfo callback) {
        if (entity instanceof Player p) {
            IDetainableEntity detainableEntity = (IDetainableEntity)p;
            int detained = detainableEntity.getDetained();

            boolean shouldCancel = false;

            HumanoidModel<LivingEntity> model = (HumanoidModel<LivingEntity>)(Object) this;

            head.z = 0;
            body.z = 0;

            if (p.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                if (detained == 0) {
                    HumanoidAnimationHelper.animatePilloryDetainedAnimation(entity, model, f1, f2, f3, headYRot,
                            headXRot, detainableEntity.getDetainedRotation());
                    shouldCancel = true;
                } else {
                    if(p instanceof IRestrainableEntity restrainable) {
                        ArmRestraintAnimationFlags armAnimationFlags = Restraints.getArmAnimationFlagById(restrainable.getArmRestraintId());
                        switch (armAnimationFlags) {
                            case ARMS_TIED_FRONT:
                                HumanoidAnimationHelper.animateArmsTiedFrontFirstPerson(entity, model, f1, f2, f3, headYRot,
                                        headXRot);
                                shouldCancel = true;
                                break;
                            case ARMS_TIED_BEHIND:
                                HumanoidAnimationHelper.animateArmsTiedBack(entity, model, f1, f2, f3, headYRot, headXRot);
                                shouldCancel = true;
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                if (detained == 0) {
                    HumanoidAnimationHelper.animatePilloryDetainedAnimation(entity, model, f1, f2, f3, headYRot,
                            headXRot, detainableEntity.getDetainedRotation());
                    shouldCancel = true;
                } else {
                    if(p instanceof IRestrainableEntity restrainable) {
                        ArmRestraintAnimationFlags armAnimationFlags = Restraints.getArmAnimationFlagById(restrainable.getArmRestraintId());
                        switch (armAnimationFlags) {
                            case ARMS_TIED_FRONT:
                                HumanoidAnimationHelper.animateArmsTiedFront(entity, model, f1, f2, f3, headYRot, headXRot);
                                shouldCancel = true;
                                break;
                            case ARMS_TIED_BEHIND:
                                HumanoidAnimationHelper.animateArmsTiedBack(entity, model, f1, f2, f3, headYRot, headXRot);
                                shouldCancel = true;
                                break;
                            default:
                                break;

                        }
                    }
                }
            }

            if (shouldCancel)
                callback.cancel();
        }
    }

    
}
