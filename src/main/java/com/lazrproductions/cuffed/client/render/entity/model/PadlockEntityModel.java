package com.lazrproductions.cuffed.client.render.entity.model;

import com.lazrproductions.cuffed.CuffedMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PadlockEntityModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "padlock"), "main");
    private final ModelPart body;
    private final ModelPart north;


    public PadlockEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.north = this.body.getChild("padlock_N");
    }

    public static LayerDefinition getModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        /*PartDefinition padlock_N =*/ body.addOrReplaceChild("padlock_N", CubeListBuilder.create().texOffs(0, 2).addBox(-2.0F, -8.0F, -9.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(0.5F, -11.0F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(-1.5F, -12.0F, -9.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 12).addBox(-1.5F, -11.0F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {

            north.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

    }
}