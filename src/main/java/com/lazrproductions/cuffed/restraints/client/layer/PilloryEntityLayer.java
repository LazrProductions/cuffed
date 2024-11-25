package com.lazrproductions.cuffed.restraints.client.layer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.restraints.custom.PilloryRestraint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PilloryEntityLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;

    public PilloryEntityLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int lighting, @Nonnull T entity,
            float p_116987_,
            float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {

        IRestrainableEntity res = (IRestrainableEntity) entity;
        if (res.getHeadRestraintId().equals(PilloryRestraint.ID)) {
            
            stack.pushPose();
            ItemStack itemStack = ModItems.PILLORY_ITEM.get().getDefaultInstance();
            translateToHead(stack);
            if(entity.isCrouching())
                stack.translate(0, -0.5d, 0);
            this.itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.HEAD, false, stack, buffer, lighting);
            stack.popPose();
        }
    }

    public static void translateToHead(PoseStack p_174484_) {
        p_174484_.translate(0.0F, -0.25F, 0.0F);
        p_174484_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_174484_.scale(0.625F, -0.625F, -0.625F);
    }
}