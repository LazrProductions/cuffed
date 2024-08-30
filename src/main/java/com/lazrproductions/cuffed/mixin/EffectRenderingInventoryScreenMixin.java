package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lazrproductions.cuffed.effect.RestrainedEffect;
import com.lazrproductions.cuffed.effect.WoundedEffect;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {
    @Inject(method = "getEffectName", at = @At("HEAD"), cancellable = true)
    private void getEffectName(MobEffectInstance effect, CallbackInfoReturnable<Component> callback) {
        if (effect.getEffect() instanceof WoundedEffect) {
            MutableComponent c = effect.getEffect().getDisplayName().copy();
            c.append(CommonComponents.EMPTY).append(Component.literal(effect.getAmplifier() + "%"));
            callback.setReturnValue(c);
        } else if(effect.getEffect() instanceof RestrainedEffect) {
            callback.setReturnValue(effect.getEffect().getDisplayName().copy());
        }
    }
}
