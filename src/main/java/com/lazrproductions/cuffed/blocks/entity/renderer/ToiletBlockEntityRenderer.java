package com.lazrproductions.cuffed.blocks.entity.renderer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.blocks.TrayBlock;
import com.lazrproductions.cuffed.blocks.entity.ToiletBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ToiletBlockEntityRenderer implements BlockEntityRenderer<ToiletBlockEntity> {
    public ToiletBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        
    }

    @Override
    public void render(@Nonnull ToiletBlockEntity entity, float partialTick, @Nonnull PoseStack stack,
        @Nonnull MultiBufferSource buffer, int light, int overlay) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack topStack = entity.getNextStack();
        if(!topStack.isEmpty()) {
            stack.pushPose();
            Direction facing = entity.getBlockState().getValue(TrayBlock.FACING);
            stack.translate(0.5f, 0.3125f, 0.5f);
            stack.scale(0.35f, 0.35f, 0.35f);
            stack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot()));
            stack.mulPose(Axis.XP.rotationDegrees(90));

            itemRenderer.renderStatic(topStack, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, stack, buffer, entity.getLevel(), 1);
            stack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
