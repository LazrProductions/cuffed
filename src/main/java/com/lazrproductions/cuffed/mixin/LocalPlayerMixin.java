package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(at = @At("RETURN"), method = "canStartSprinting", cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> callback) {
        if (this instanceof IRestrainableEntity entity) {
            ResourceLocation headKey = entity.getLegRestraintId();
            if (headKey != null) {
                AbstractRestraint restraint =  RestraintAPI.getNewRestraintByKey(headKey);
                if (restraint != null && !restraint.AllowSprinting()) {
                    callback.setReturnValue(false);
                    callback.cancel();
                    return;
                }
            }
            ResourceLocation armKey = entity.getLegRestraintId();
            if (armKey != null) {
                AbstractRestraint restraint =  RestraintAPI.getNewRestraintByKey(armKey);
                if (restraint != null && !restraint.AllowSprinting()) {
                    callback.setReturnValue(false);
                    callback.cancel();
                    return;
                }
            }
            ResourceLocation legKey = entity.getLegRestraintId();
            if (legKey != null) {
                AbstractRestraint restraint =  RestraintAPI.getNewRestraintByKey(legKey);
                if (restraint != null && !restraint.AllowSprinting()) {
                    callback.setReturnValue(false);
                    callback.cancel();
                    return;
                }
            }
        }
    }
}
