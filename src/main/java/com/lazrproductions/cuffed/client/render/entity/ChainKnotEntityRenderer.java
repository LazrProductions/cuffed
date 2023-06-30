package com.lazrproductions.cuffed.client.render.entity;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.client.render.entity.model.ChainKnotEntityModel;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.events.RenderChainKnotEntityEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChainKnotEntityRenderer extends EntityRenderer<ChainKnotEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CuffedMod.MODID,
            "textures/entity/chain_knot.png");
    private final ChainKnotEntityModel<ChainKnotEntity> model;

    public ChainKnotEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ChainKnotEntityModel<>(context.bakeLayer(ChainKnotEntityModel.LAYER_LOCATION));
    }

    public static PoseStack POSESTACK;
    public static MultiBufferSource BUFFER;

    public void render(ChainKnotEntity entity, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffer,
            int light) {
        super.render(entity, yaw, partialTicks, stack, buffer, light);

        boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new RenderChainKnotEntityEvent.Pre(entity, this, partialTicks, stack, buffer, light));

        //CuffedMod.LOGGER.info("entity.chainedPlayers.size();");

        if(!cancelled) {
        POSESTACK = stack;
        BUFFER = buffer;

        this.model.setOnFence(!entity.isOnFence());

        stack.pushPose();
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                1.0F);
        stack.popPose();
        }

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new RenderChainKnotEntityEvent.Post(entity, this, partialTicks, stack, buffer, light));
    }

    @Override
    public ResourceLocation getTextureLocation(ChainKnotEntity p_114482_) {
        return TEXTURE_LOCATION;
    }
}