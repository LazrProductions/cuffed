package com.lazrproductions.cuffed.blocks.entity.renderer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.blocks.TrayBlock;
import com.lazrproductions.cuffed.blocks.entity.TrayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class TrayBlockEntityRenderer implements BlockEntityRenderer<TrayBlockEntity> {
    public TrayBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        
    }

    @Override
    public void render(@Nonnull TrayBlockEntity entity, float partialTick, @Nonnull PoseStack stack,
        @Nonnull MultiBufferSource buffer, int light, int overlay) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack foodStack = entity.getFoodStack();
        if(!foodStack.isEmpty()) {

            stack.pushPose();
            Direction facing = entity.getBlockState().getValue(TrayBlock.FACING);
            switch (facing) {
                case NORTH:
                    stack.translate(0.66f, 0.05f, 0.52f);
                    break;
                case SOUTH:
                    stack.translate(0.34f, 0.05f, 0.48f);
                    break;
                case WEST:
                    stack.translate(0.52f, 0.05f, 0.34f);
                    break;
                default:
                    stack.translate(0.48f, 0.05f, 0.66f);
                    break;
            }
            stack.scale(0.35f, 0.35f, 0.35f);
            stack.mulPose(Vector3f.YN.rotationDegrees(facing.getOpposite().toYRot()));
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

            itemRenderer.renderStatic(foodStack, ItemTransforms.TransformType.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, stack, buffer, 1);
            stack.popPose();
        }
    
        ItemStack forkStack = entity.getForkStack();
        if(!forkStack.isEmpty()) {

            stack.pushPose();
            Direction facing = entity.getBlockState().getValue(TrayBlock.FACING);
            switch (facing) {
                case NORTH:
                    stack.translate(0.44f, 0.05f, 0.52f);
                    break;
                case SOUTH:
                    stack.translate(0.56f, 0.05f, 0.48f);
                    break;
                case WEST:
                    stack.translate(0.52f, 0.05f, 0.56f);
                    break;
                default:
                    stack.translate(0.48f, 0.05f, 0.44f);
                    break;
            }
            stack.scale(0.28f, 0.28f, 0.28f);
            stack.mulPose(Vector3f.YN.rotationDegrees(facing.getOpposite().toYRot() - 45));
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

            itemRenderer.renderStatic(forkStack, ItemTransforms.TransformType.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, stack, buffer, 1);
            stack.popPose();
        }

        ItemStack spoonStack = entity.getSpoonStack();
        if(!spoonStack.isEmpty()) {

            stack.pushPose();
            Direction facing = entity.getBlockState().getValue(TrayBlock.FACING);
            switch (facing) {
                case NORTH:
                    stack.translate(0.365f, 0.05f, 0.52f);
                    break;
                case SOUTH:
                    stack.translate(0.635f, 0.05f, 0.48f);
                    break;
                case WEST:
                    stack.translate(0.52f, 0.05f, 0.635f);
                    break;
                default:
                    stack.translate(0.48f, 0.05f, 0.365f);
                    break;
            }
            stack.scale(0.28f, 0.28f, 0.28f);
            stack.mulPose(Vector3f.YN.rotationDegrees(facing.getOpposite().toYRot() - 45));
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

            itemRenderer.renderStatic(spoonStack, ItemTransforms.TransformType.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, stack, buffer, 1);
            stack.popPose();
        }
        
        ItemStack knifeStack = entity.getKnifeStack();
        if(!knifeStack.isEmpty()) {

            stack.pushPose();
            Direction facing = entity.getBlockState().getValue(TrayBlock.FACING);
            switch (facing) {
                case NORTH:
                    stack.translate(0.27f, 0.05f, 0.52f);
                    break;
                case SOUTH:
                    stack.translate(0.73f, 0.05f, 0.48f);
                    break;
                case WEST:
                    stack.translate(0.52f, 0.05f, 0.73f);
                    break;
                default:
                    stack.translate(0.48f, 0.05f, 0.27f);
                    break;
            }
            stack.scale(0.28f, 0.28f, 0.28f);
            stack.mulPose(Vector3f.YN.rotationDegrees(facing.getOpposite().toYRot() - 45));
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

            itemRenderer.renderStatic(knifeStack, ItemTransforms.TransformType.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, stack, buffer, 1);
            stack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
