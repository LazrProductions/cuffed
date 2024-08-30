package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.PilloryRestraint;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderArmWithItem(AbstractClientPlayer player, float p_109373_, float p_109374_, InteractionHand hand, float f2, ItemStack stackInHand, float f1, PoseStack poseStack, MultiBufferSource buffer, int lighting, CallbackInfo callback) {
        
        if(player.isLocalPlayer()) {
            if(player instanceof IDetainableEntity det && det.getDetained() > -1) {
                player.setYBodyRot(det.getDetainedRotation());
                player.setYHeadRot(det.getDetainedRotation());
                player.setYRot(det.getDetainedRotation());
                player.setXRot(45);

                boolean isMainHand = hand == InteractionHand.MAIN_HAND;
                HumanoidArm humanoidarm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
                poseStack.pushPose();
                this.renderPlayerArm(poseStack, buffer, lighting, f1, f2, humanoidarm);
                poseStack.popPose();
                callback.cancel();
            }

            if(player instanceof IRestrainableEntity res && res.getHeadRestraintId() == PilloryRestraint.ID) {
                player.setXRot(10);
            }
        }
    }

    @Shadow
    private void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_) {
    }
}
