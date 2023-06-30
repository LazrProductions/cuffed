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


public class ChainKnotEntityModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "chain_knot"), "main");
	private final ModelPart fence;
	private final ModelPart hook;

	public ChainKnotEntityModel(ModelPart root) {
		this.fence = root.getChild("fence");
		this.hook = root.getChild("hook");
	}


	public static LayerDefinition getModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		//i hate yellow markers in my hierarchy :(
		/*PartDefinition fence =*/ partdefinition.addOrReplaceChild("fence", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		/*PartDefinition hook =*/ partdefinition.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 22).addBox(-1.5F, -9.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0F, 8.5F, 0F));

		
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	boolean onFence;
	public void setOnFence(boolean v) { onFence = v; }

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if(onFence)
			fence.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		else
			hook.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}