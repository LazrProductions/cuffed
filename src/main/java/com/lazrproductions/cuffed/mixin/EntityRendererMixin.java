package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.utils.ChainUtils;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(T entity, float p_114486_, float p_114487_, PoseStack stack, MultiBufferSource buffer,
            int light, CallbackInfo callback) {
        if(entity instanceof IAnchorableEntity anchorable && anchorable.getAnchorClientSide() != null )
            ChainUtils.renderChainTo(entity, p_114487_, stack, buffer, anchorable.getAnchorClientSide());
    }
}