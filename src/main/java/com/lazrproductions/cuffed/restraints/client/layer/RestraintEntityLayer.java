package com.lazrproductions.cuffed.restraints.client.layer;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class RestraintEntityLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private final HashMap<String, HumanoidModel<T>> restraintModels = new HashMap<>();
    private final HashMap<String, RestraintModelInterface> restraintModelInterfaces = new HashMap<>();

    @SuppressWarnings("unchecked")
    public RestraintEntityLayer(RenderLayerParent<T, M> parent, EntityRendererProvider.Context context) {
        super(parent);

        

        restraintModels.clear();
        for (AbstractRestraint res : RestraintAPI.Registries.getAllRestraints()) {
            try {
                RestraintModelInterface in = res.getModelInterface();
                if (in != null) {
                    if (in.getRenderedModel() != null)
                        restraintModels.put(res.getId().toString(), (HumanoidModel<T>)(in.getRenderedModel().getConstructor(ModelPart.class).newInstance(context.bakeLayer(in.getRenderedModelLayer()))));
                    restraintModelInterfaces.put(res.getId().toString(), in);
                }
            } catch (Exception e) {
                CuffedMod.LOGGER.error("The restraint " + res.getId() + " uses a model that is not valid!");
            }
        }
    }

    public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int lighting, @Nonnull T entity,
            float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {


        IRestrainableEntity res = (IRestrainableEntity) entity;
        
        if(res != null) {
            // RENDER HEAD RESTRAINT
            var headRestraint = RestraintAPI.Registries.get(res.getHeadRestraintId());
            if (headRestraint != null) {
                if(restraintModels.containsKey(headRestraint.getId().toString())) {
                    var model = restraintModels.get(headRestraint.getId().toString());
                    var in = restraintModelInterfaces.get(headRestraint.getId().toString());
                    if(model != null) {
                        this.getParentModel().copyPropertiesTo(model);

                        model.body.visible = true;

                        this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
                        
                        if (res.getHeadIsEnchanted())
                            renderGlint(stack, buffer, lighting, model);
                    }
                }
            }
            
            // RENDER ARM RESTRAINT
            var armRestraint = RestraintAPI.Registries.get(res.getArmRestraintId());
            if (armRestraint != null) {
                if(restraintModels.containsKey(armRestraint.getId().toString())) {
                    var model = restraintModels.get(armRestraint.getId().toString());
                    var in = restraintModelInterfaces.get(armRestraint.getId().toString());
                    if(model != null) {
                        this.getParentModel().copyPropertiesTo(model);

                        model.body.visible = true;

                        this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
                        if (res.getArmsAreEnchanted())
                            renderGlint(stack, buffer, lighting, model);
                    }
                }
            }
            
            // RENDER ARM RESTRAINT
            var legRestraint = RestraintAPI.Registries.get(res.getLegRestraintId());
            if (legRestraint != null) {
                if(restraintModels.containsKey(legRestraint.getId().toString())) {
                    var model = restraintModels.get(legRestraint.getId().toString());
                    var in = restraintModelInterfaces.get(legRestraint.getId().toString());
                    if(model != null) {
                        this.getParentModel().copyPropertiesTo(model);

                        model.body.visible = true;

                        this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
                        if (res.getArmsAreEnchanted())
                            renderGlint(stack, buffer, lighting, model);
                    }
                }
            }
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
}