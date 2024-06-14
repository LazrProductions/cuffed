package com.lazrproductions.cuffed.restraints.model;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unused")
public class LegShacklesModel<T extends LivingEntity> extends HumanoidModel<T> {
	private final ModelPart _root;

	public LegShacklesModel(ModelPart root) {
		super(root);
		_root = root;
	}

	public static LayerDefinition createArmorLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition right_leg = partdefinition.getChild("right_leg");
		PartDefinition left_leg = partdefinition.getChild("left_leg");

		PartDefinition right_shackle = right_leg.addOrReplaceChild("right_shackle", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -3.5F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-4.0F, -1.5F, -3.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.0154F, -0.1739F, -0.0886F));

		PartDefinition left_shackle = left_leg.addOrReplaceChild("left_shackle", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -3.5F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-4.0F, -1.5F, -3.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.023F, 0.173F, 0.1329F));
		PartDefinition cube_r1 = left_shackle.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-5.875F, -0.1F, -0.7F, 12.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8799F, -0.9079F, 2.3232F, 0.4876F, -0.1701F, -0.0895F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	
	@Override
	public void renderToBuffer(@Nonnull PoseStack stack, @Nonnull VertexConsumer buffer, int packedLight, int blockLight,
			float partialTick, float r, float g, float b) {
		_root.render(stack, buffer, packedLight, blockLight);

		super.renderToBuffer(stack, buffer, packedLight, blockLight, partialTick, r, g, b);
	}
}