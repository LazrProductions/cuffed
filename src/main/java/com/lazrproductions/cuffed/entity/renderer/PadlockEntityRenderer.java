package com.lazrproductions.cuffed.entity.renderer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.entity.model.PadlockEntityModel;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PadlockEntityRenderer extends EntityRenderer<PadlockEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CuffedMod.MODID,
            "textures/entity/padlock.png");
    private static final ResourceLocation TEXTURE_REINFORCED_LOCATION = new ResourceLocation(CuffedMod.MODID,
            "textures/entity/reinforced_padlock.png");
    private final PadlockEntityModel<PadlockEntity> model;

    public PadlockEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PadlockEntityModel<>(context.bakeLayer(ModModelLayers.PADLOCK_LAYER));
    }

    public static PoseStack POSESTACK;
    public static MultiBufferSource BUFFER;


   protected void setupRotations(PadlockEntity entity, PoseStack stack, float rotX, float rotY, float rotZ) {
      stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotY));
   }


    public void render(@Nonnull PadlockEntity entity, float yaw, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer,
            int light) {
        super.render(entity, yaw, partialTicks, stack, buffer, light);

        this.setupRotations(entity, stack, 0, entity.getYRot(), 0);

        POSESTACK = stack;
        BUFFER = buffer;
        
        stack.pushPose();
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        if(entity.isReinforced())
                vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE_REINFORCED_LOCATION));
        this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                1.0F);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull PadlockEntity entity) {
        return TEXTURE_LOCATION;
    }
}