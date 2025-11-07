package com.lazrproductions.cuffed.blocks.entity.renderer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.model.GuillotineBlockEntityModel;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GuillotineBlockEntityRenderer implements BlockEntityRenderer<GuillotineBlockEntity> {
        private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID,
            "textures/block/guillotine.png");

    GuillotineBlockEntityModel<GuillotineBlockEntity> model;

    public GuillotineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new GuillotineBlockEntityModel<>(ctx.bakeLayer(ModModelLayers.GUILLOTINE_LAYER));
    }

    @Override
    public void render(@Nonnull GuillotineBlockEntity entity, float partialTick, @Nonnull PoseStack stack,
        @Nonnull MultiBufferSource buffer, int light, int overlay) {
        model.setupAnim(entity, partialTick);
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION)), light, overlay, 1, 1, 1, 1);

    }

}
