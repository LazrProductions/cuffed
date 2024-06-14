package com.lazrproductions.cuffed.restraints.model;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unused")
public class ShacklesModel<T extends LivingEntity> extends HumanoidModel<T> {
	private final ModelPart _root;

	public ShacklesModel(ModelPart root) {
		super(root);
		this._root = root;
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


		PartDefinition rightarm = partdefinition.getChild("right_arm");
		PartDefinition rightcuff = rightarm.addOrReplaceChild("rightcuff", CubeListBuilder.create().texOffs(0, 0).addBox(-8.5F, -1.0F, -0.5F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-9.5F, -0.5F, 0.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 7.0F, -3.0F));
		PartDefinition Chain_r1 = rightcuff.addOrReplaceChild("Chain_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-8.75F, 2.75F, 1.0F, 12.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.726F, 0.3931F, 0.3554F));
		
		PartDefinition leftarm = partdefinition.getChild("left_arm");
		PartDefinition leftcuff = leftarm.addOrReplaceChild("leftcuff", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 6.0F, -3.5F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-2.5F, 6.5F, -3.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
	
	@Override
	public void renderToBuffer(@Nonnull PoseStack stack, @Nonnull VertexConsumer buffer, int packedLight, int blockLight,
			float partialTick, float r, float g, float b) {
		_root.render(stack, buffer, packedLight, blockLight);

		super.renderToBuffer(stack, buffer, packedLight, blockLight, partialTick, r, g, b);
	}
}