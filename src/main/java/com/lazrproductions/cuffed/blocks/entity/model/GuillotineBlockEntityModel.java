package com.lazrproductions.cuffed.blocks.entity.model;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.blocks.GuillotineBlock;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class GuillotineBlockEntityModel<T extends GuillotineBlockEntity> {
	private final ModelPart guillotine;
	//private final ModelPart blade;
	//private final ModelPart dry_blade;
	//private final ModelPart bloody_blade;

	public GuillotineBlockEntityModel(ModelPart root) {
		this.guillotine = root.getChild("guillotine");
		//this.blade = root.getChild("blade");
		//this.dry_blade = root.getChild("dry_blade");
		//this.bloody_blade = root.getChild("bloody_blade");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition guillotine = partdefinition.addOrReplaceChild("guillotine", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition blade = guillotine.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(26, 25).addBox(-5.0F, -2.0F, -3.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(26, 25).addBox(3.0F, -2.0F, -3.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 7).addBox(-6.0F, -4.0F, -3.0F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 7).mirror().addBox(6.0F, -4.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 7).addBox(-10.0F, -4.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		blade.addOrReplaceChild("dry_blade", CubeListBuilder.create().texOffs(0, 21).addBox(-6.0F, 4.0F, -3.0F, 12.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		blade.addOrReplaceChild("bloody_blade", CubeListBuilder.create().texOffs(0, 10).addBox(-6.0F, 4.0F, -3.0F, 12.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		blade.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	float prevDownProgress = 0;
	public void setupAnim(@Nonnull GuillotineBlockEntity entity, float partialTick) {
		guillotine.setRotation(0, (entity.getBlockState().getValue(GuillotineBlock.FACING).toYRot() - 180) * Mth.DEG_TO_RAD, Mth.PI);
		
		float offset = 0;
		if(entity.isDown)
			prevDownProgress += (float)Math.pow(prevDownProgress + 0.08d, 2f)/1.32f;
		else
			prevDownProgress = Mth.lerp(partialTick * 0.1f, prevDownProgress, 0);
		prevDownProgress = Mth.clamp(prevDownProgress,0,1);
		offset = prevDownProgress * 12;
		guillotine.setPos(8, 8 - offset, 8);

		guillotine.getChild("blade").getChild("dry_blade").visible = !entity.isBloody;
		guillotine.getChild("blade").getChild("bloody_blade").visible = entity.isBloody;
	}

	public void renderToBuffer(@Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		guillotine.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

	}
}