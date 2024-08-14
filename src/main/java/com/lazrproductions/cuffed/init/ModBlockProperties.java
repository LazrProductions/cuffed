package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.blocks.base.PosterType;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModBlockProperties {
   public static final EnumProperty<PosterType> POSTER_TYPE = EnumProperty.create("poster", PosterType.class);
}
