package com.lazrproductions.cuffed.restraints.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RestraintModelInterface {
    public abstract Class<? extends HumanoidModel<? extends LivingEntity>> getRenderedModel();
    public abstract ModelLayerLocation getRenderedModelLayer();
    public abstract ResourceLocation getRenderedModelTexture();
}
