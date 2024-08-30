package com.lazrproductions.cuffed.restraints.layer;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.DuckTapeArmsRestraint;
import com.lazrproductions.cuffed.restraints.DuckTapeHeadRestraint;
import com.lazrproductions.cuffed.restraints.DuckTapeLegsRestraint;
import com.lazrproductions.cuffed.restraints.model.DuckTapeArmsModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeHeadModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeLegsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class DuckTapeEntityLayer<T extends LivingEntity, P extends HumanoidModel<T>, H extends DuckTapeHeadModel<T>, A extends DuckTapeArmsModel<T>, L extends DuckTapeLegsModel<T>>
        extends RenderLayer<T, P> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CuffedMod.MODID,
            "textures/entity/duck_tape.png");
    private final H headModel;
    private final A armModel;
    private final L legModel;

    public DuckTapeEntityLayer(RenderLayerParent<T, P> parent, H head, A arm, L leg) {
        super(parent);
        this.headModel = head;
        this.armModel = arm;
        this.legModel = leg;
    }

    public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int lighting, @Nonnull T entity,
            float p_116987_,
            float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {

        this.getParentModel().copyPropertiesTo(headModel);
        this.getParentModel().copyPropertiesTo(armModel);
        this.getParentModel().copyPropertiesTo(legModel);

        headModel.body.visible = true;
        armModel.body.visible = true;
        legModel.body.visible = true;

        IRestrainableEntity res = (IRestrainableEntity) entity;
        if (res.getHeadRestraintId().equals(DuckTapeHeadRestraint.ID)) {
            this.renderModel(stack, buffer, lighting, headModel, false, 1.0F, 1.0F, 1.0F, TEXTURE_LOCATION);
            if (res.getHeadIsEnchanted())
                renderGlint(stack, buffer, lighting, headModel);
        }

        if (res.getArmRestraintId().equals(DuckTapeArmsRestraint.ID)) {
            this.renderModel(stack, buffer, lighting, armModel, false, 1.0F, 1.0F, 1.0F, TEXTURE_LOCATION);
            if (res.getArmsAreEnchanted())
                renderGlint(stack, buffer, lighting, armModel);
        }
        if (res.getLegRestraintId().equals(DuckTapeLegsRestraint.ID)) {
            this.renderModel(stack, buffer, lighting, legModel, false, 1.0F, 1.0F, 1.0F, TEXTURE_LOCATION);
            if (res.getLegsAreEnchanted())
                renderGlint(stack, buffer, lighting, legModel);
        }
    }

    private void renderModel(PoseStack stack, MultiBufferSource buffer, int partialTick,
            net.minecraft.client.model.Model model, boolean p_289668_, float p_289678_, float p_289674_,
            float p_289693_, ResourceLocation resource) {
        VertexConsumer vertexconsumer = net.minecraft.client.renderer.entity.ItemRenderer.getArmorFoilBuffer(buffer,
                RenderType.armorCutoutNoCull(resource), false, p_289668_);
        model.renderToBuffer(stack, vertexconsumer, partialTick, OverlayTexture.NO_OVERLAY, p_289678_, p_289674_,
                p_289693_, 1.0F);
    }

    private void renderGlint(PoseStack stack, MultiBufferSource buffer, int partialTick,
            net.minecraft.client.model.Model model) {
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.armorEntityGlint()), partialTick,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected ResourceLocation getTextureLocation() {
        return TEXTURE_LOCATION;
    }
}