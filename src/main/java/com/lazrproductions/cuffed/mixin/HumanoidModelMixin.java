package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.lazrproductions.cuffed.client.HumanoidAnimationHelper;

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
    @Inject(at =@At("HEAD"), method = "setupAnim", cancellable = true)
    private void setupAnim(T entity, float f1, float f2, float f3, float headYRot, float headXRot, CallbackInfo callback) {
        if(entity instanceof Player p ) {
            CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(p);
            boolean isHandcuffed = c.isHandcuffed();
            int detained = c.isDetained();
            boolean isChained = c.isAnchored();
            boolean shouldCancel = false;;

            HumanoidModel<LivingEntity> t = (HumanoidModel<LivingEntity>)(Object)this;

            head.z = 0;
            body.z = 0;

            if(p.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                if(detained==0) {
                    HumanoidAnimationHelper.renderPilloryDetainedAnimation(entity, (HumanoidModel<LivingEntity>)(Object)this, f1, f2, f3, headYRot, headXRot, c.getDetainedRotation());
                    shouldCancel = true;
                } else if(isChained) {
                    HumanoidAnimationHelper.renderChainedAnimation(entity, (HumanoidModel<LivingEntity>)(Object)this, f1, f2, f3, headYRot, headXRot);
                    shouldCancel = true;
                } else if(isHandcuffed) {
                    HumanoidAnimationHelper.renderFirstPersonHandcuffedAnimation(entity, (HumanoidModel<LivingEntity>)(Object)this, f1, f2, f3, headYRot, headXRot);
                    shouldCancel = true;
                }
            } else {
                if(detained==0) {
                    HumanoidAnimationHelper.renderPilloryDetainedAnimation(entity, t, f1, f2, f3, headYRot, headXRot, c.getDetainedRotation());
                    shouldCancel = true;
                } else if(isChained) {
                    HumanoidAnimationHelper.renderChainedAnimation(entity, t, f1, f2, f3, headYRot, headXRot);
                    shouldCancel = true;
                } else if(isHandcuffed) {
                    HumanoidAnimationHelper.renderHandcuffedAnimation(entity, t, f1, f2, f3, headYRot, headXRot);
                    shouldCancel = true;
                }
            }
            
            if(shouldCancel)
                callback.cancel();
            
        }
    }
}
