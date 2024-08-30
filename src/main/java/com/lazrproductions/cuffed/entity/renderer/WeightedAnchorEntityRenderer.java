package com.lazrproductions.cuffed.entity.renderer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.WeightedAnchorEntity;
import com.lazrproductions.cuffed.entity.model.WeightedAnchorModel;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedAnchorEntityRenderer extends EntityRenderer<WeightedAnchorEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CuffedMod.MODID, "textures/entity/weighted_anchor.png");
    private final WeightedAnchorModel<WeightedAnchorEntity> model;

    public WeightedAnchorEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WeightedAnchorModel<>(context.bakeLayer(ModModelLayers.WEIGHTED_ANCHOR_LAYER));
    }

    public static PoseStack POSESTACK;
    public static MultiBufferSource BUFFER;


   protected void setupRotations(WeightedAnchorEntity entity, PoseStack stack, float rotX, float rotY, float rotZ) {
      stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotY));
   }


    public void render(@Nonnull WeightedAnchorEntity entity, float yaw, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer,
            int light) {
        super.render(entity, yaw, partialTicks, stack, buffer, light);

        this.setupRotations(entity, stack, 0, entity.getYRot(), 0);

        POSESTACK = stack;
        BUFFER = buffer;
        
        stack.pushPose();
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        
        if(entity.getIsEnchanted())
            renderGlint(stack, buffer, light, this.model);

        stack.popPose();        

    }

    private void renderGlint(PoseStack stack, MultiBufferSource buffer, int partialTick,
            net.minecraft.client.model.Model model) {
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityGlintDirect()), partialTick, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull WeightedAnchorEntity entity) {
        return TEXTURE_LOCATION;
    }
}