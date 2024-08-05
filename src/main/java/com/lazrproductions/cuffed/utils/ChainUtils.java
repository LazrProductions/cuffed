package com.lazrproductions.cuffed.utils;

import java.util.Random;

import org.joml.Math;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class ChainUtils {
	public static void renderChainTo(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom) {
		if(entityFrom == null)
			return;

		float maxLength = 12;

		float distance = entity.distanceTo(entityFrom) / maxLength;
		if(distance>maxLength)
			distance = maxLength;

		renderVerticalTo(entity, partialTicks, poseStack, bufferSource, entityFrom, distance);
		renderHorizontalTo(entity, partialTicks, poseStack, bufferSource, entityFrom, distance);
	}

	public static void renderChainFrom(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo) {
		if(entityTo == null)
			return;
		float maxLength = 12;
		float distance = entity.distanceTo(entityTo) / maxLength;
			if(distance>maxLength)
				distance = maxLength;

		renderVerticalFrom(entity, partialTicks, poseStack, bufferSource, entityTo, distance);
		renderHorizontalFrom(entity, partialTicks, poseStack, bufferSource, entityTo, distance);
	}


	public static void renderVerticalFrom(Entity entityTo, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom, float distance) {
		if(entityTo == null)
			return;
		poseStack.pushPose(); 


		Vec3 startPos = entityTo.getPosition(partialTicks).add(new Vec3(0,1.4,0));
		
		double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);

		Vec3 endPosOffset = entityFrom.getRopeHoldPosition(partialTicks).subtract(entityFrom.position());

		double d1 = Math.cos(d0) * endPosOffset.z + Math.sin(d0) * endPosOffset.x;
		double d2 = Math.sin(d0) * endPosOffset.z - Math.cos(d0) * endPosOffset.x;

		double ePosX = Mth.lerp(partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
		double ePosY = Mth.lerp(partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
		double ePosZ = Mth.lerp(partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;

		poseStack.translate(d1, endPosOffset.y, d2);

		float f = (float)(startPos.x - ePosX);
		float f1 = (float)(startPos.y - ePosY);
		float f2 = (float)(startPos.z - ePosZ);

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f pose = poseStack.last().pose();
		float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
        Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
        Vec3 _hEye = entityTo.getEyePosition(partialTicks);
		BlockPos mobEyePosition = new BlockPos((int)Math.floor(_mEye.x), (int)Math.floor( _mEye.y), (int)Math.floor(_mEye.z));
		BlockPos holderEyePos = new BlockPos((int)Math.floor(_hEye.x), (int)Math.floor(_hEye.y), (int)Math.floor(_hEye.z));
		int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
		int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
		int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
		int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

		for(int i = 0; i <= 24; ++i) {
			addChainLink(vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.025F, f5, f6, i, false, distance);
		}
		poseStack.popPose();
	}
	
	public static void renderHorizontalFrom(Entity entityTo, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom, float distance) {
		if(entityTo == null)
			return;
		poseStack.pushPose();

		Vec3 startPos = entityTo.getPosition(partialTicks).add(new Vec3(0,1.4,0));
		
		double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);

		Vec3 endPosOffset = entityFrom.getRopeHoldPosition(partialTicks).subtract(entityFrom.position());

		double d1 = Math.cos(d0) * endPosOffset.z + Math.sin(d0) * endPosOffset.x;
		double d2 = Math.sin(d0) * endPosOffset.z - Math.cos(d0) * endPosOffset.x;

		double ePosX = Mth.lerp(partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
		double ePosY = Mth.lerp(partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
		double ePosZ = Mth.lerp(partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;

		poseStack.translate(d1, endPosOffset.y, d2);

		float x = (float)(startPos.x - ePosX);
		float y = (float)(startPos.y - ePosY);
		float z = (float)(startPos.z - ePosZ);

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f pose = poseStack.last().pose();
		float f4 = Mth.invSqrt(x * x + z * z) * 0.025F / 2.0F;
		float f5 = z * f4;
		float f6 = x * f4;
        Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
        Vec3 _hEye = entityTo.getEyePosition(partialTicks);
		BlockPos mobEyePosition = new BlockPos((int)Math.floor(_mEye.x), (int)Math.floor( _mEye.y), (int)Math.floor(_mEye.z));
		BlockPos holderEyePos = new BlockPos((int)Math.floor(_hEye.x), (int)Math.floor(_hEye.y), (int)Math.floor(_hEye.z));
		int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
		int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
		int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
		int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

		for(int i = 0; i <= 24; ++i) {
			addChainLink(vertexConsumer, pose, x, y, z, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0F, f5, f6, i, true, distance);
		}
		poseStack.popPose();
	}

	
	public static void renderVerticalTo(Entity entityFrom, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo, float distance) {
		if(entityTo == null)
			return;
		poseStack.pushPose(); 


		Vec3 startPos = entityTo.getRopeHoldPosition(partialTicks);
		
		double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);

		Vec3 endPosOffset = entityFrom.getLeashOffset(partialTicks);

		double d1 = Math.cos(d0) * endPosOffset.z + Math.sin(d0) * endPosOffset.x;
		double d2 = Math.sin(d0) * endPosOffset.z - Math.cos(d0) * endPosOffset.x;

		double ePosX = Mth.lerp(partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
		double ePosY = Mth.lerp(partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
		double ePosZ = Mth.lerp(partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;

		poseStack.translate(d1, endPosOffset.y, d2);

		float f = (float)(startPos.x - ePosX);
		float f1 = (float)(startPos.y - ePosY);
		float f2 = (float)(startPos.z - ePosZ);

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f pose = poseStack.last().pose();

		float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;

        Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
        Vec3 _hEye = entityTo.getEyePosition(partialTicks);

		BlockPos mobEyePosition = new BlockPos((int)Math.floor(_mEye.x), (int)Math.floor( _mEye.y), (int)Math.floor(_mEye.z));
		BlockPos holderEyePos = new BlockPos((int)Math.floor(_hEye.x), (int)Math.floor(_hEye.y), (int)Math.floor(_hEye.z));
		int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
		int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
		int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
		int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

		for(int i = 0; i <= 24; ++i) {
			addChainLink(vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.025F, f5, f6, i, false, distance);
		}
		poseStack.popPose();
	}
	
	public static void renderHorizontalTo(Entity entityFrom, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo, float distance) {
		if(entityTo == null)
			return;
		poseStack.pushPose();

		Vec3 startPos = entityTo.getRopeHoldPosition(partialTicks);
		
		double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);

		Vec3 endPosOffset = entityFrom.getLeashOffset(partialTicks);

		double d1 = Math.cos(d0) * endPosOffset.z + Math.sin(d0) * endPosOffset.x;
		double d2 = Math.sin(d0) * endPosOffset.z - Math.cos(d0) * endPosOffset.x;

		double ePosX = Mth.lerp(partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
		double ePosY = Mth.lerp(partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
		double ePosZ = Mth.lerp(partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;

		poseStack.translate(d1, endPosOffset.y, d2);

		float x = (float)(startPos.x - ePosX);
		float y = (float)(startPos.y - ePosY);
		float z = (float)(startPos.z - ePosZ);

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f pose = poseStack.last().pose();
		float f4 = Mth.invSqrt(x * x + z * z) * 0.025F / 2.0F;
		float f5 = z * f4;
		float f6 = x * f4;
        Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
        Vec3 _hEye = entityTo.getEyePosition(partialTicks);
		BlockPos mobEyePosition = new BlockPos((int)Math.floor(_mEye.x), (int)Math.floor( _mEye.y), (int)Math.floor(_mEye.z));
		BlockPos holderEyePos = new BlockPos((int)Math.floor(_hEye.x), (int)Math.floor(_hEye.y), (int)Math.floor(_hEye.z));
		int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
		int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
		int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
		int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

		for(int i = 0; i <= 24; ++i) {
			addChainLink(vertexConsumer, pose, x, y, z, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0F, f5, f6, i, true, distance);
		}
		poseStack.popPose();
	}


	protected static int getBlockLightLevel(Entity mob, BlockPos pos) {
		return mob.isOnFire() ? 15 : mob.level().getBrightness(LightLayer.BLOCK, pos);
	}

	/**
	* Creates a pair of vertex in the consumer given for a chain-like leash.
	*/
	private static void addChainLink(VertexConsumer vertexConsumer, Matrix4f pose, float _x, float _y, float _z,
		int mobLightLevel, int holderLightLevel, int mobBrightness, int holderBrightness, float endHeight,
		float startHeight, float xWidth, float zWidth, int index, boolean alternate, float distance) {
		
		float leashProgress = (float) index / 24.0F;
		
		int segmentLight = (int) Mth.lerp(leashProgress, (float) mobLightLevel, (float) holderLightLevel);
		int segmentBrightness = (int) Mth.lerp(leashProgress, (float) mobBrightness, (float) holderBrightness);
		int lightmap = LightTexture.pack(segmentLight, segmentBrightness);

		float[] rr = new float[] { 0.2862745098039216f, 0.2431372549019608f, 0.1450980392156863f }; 
		float[] rg = new float[] { 0.3137254901960784f, 0.2666666666666667f, 0.1725490196078431f };
		float[] rb = new float[] { 0.396078431372549f, 0.3254901960784314f, 0.2392156862745098f };
		Random random =new Random(index*1000);
		int colorI = Math.round(random.nextFloat() * 2.49f); 
		float r = rr[colorI];
		float g = rg[colorI];
		float b = rb[colorI];

		float vertX = _x * leashProgress;
		float vertY = (_y > 0.0F ? _y * leashProgress * leashProgress
				: _y - _y * (1.0F - leashProgress) * (1.0F - leashProgress))- ((float)Math.sin(leashProgress*Math.PI)*(.6f*-(distance-1)));
		float vertZ = _z * leashProgress;

		float nextLeashProgress = (float) (index + 1) / 24.0F;
		float vertX1 = _x * nextLeashProgress;
		float vertY1 = (_y > 0.0F ? _y * nextLeashProgress * nextLeashProgress
			: _y - _y * (1.0F - nextLeashProgress) * (1.0F - nextLeashProgress))- ((float)Math.sin(leashProgress*Math.PI)*(.6f*-(distance-1)));
		float vertZ1 = _z * nextLeashProgress;

		//Adjust scale of chain
		float xWidth1 = xWidth * 2;
		float zWidth1 = zWidth * 2;
		float height = startHeight * 2;
		float eHeight = endHeight * 2;

		vertexConsumer.vertex(pose,
			vertX - xWidth1,
			vertY + height,
			vertZ + zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();
		if ((index % 2) == (alternate ? 1 : 0)) {
			vertexConsumer.vertex(pose,
				vertX - xWidth1,
				vertY + height,
				vertZ + zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();

			vertexConsumer.vertex(pose,
					vertX + xWidth1,
					vertY + eHeight - height,
					vertZ - zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();

			vertexConsumer.vertex(pose,
				vertX1 - xWidth1,
				vertY1 + height,
				vertZ1 + zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();
			vertexConsumer.vertex(pose,
				vertX1 + xWidth1,
				vertY1 + eHeight - height,
				vertZ1 - zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();
		} else  {
			//Skip a link (make this "face" invisible )
			vertexConsumer.vertex(pose,
				vertX - xWidth1,
				vertY + height,
				vertZ + zWidth1).color(r, g, b, 0).uv2(lightmap).endVertex();
			
		}
	}
}