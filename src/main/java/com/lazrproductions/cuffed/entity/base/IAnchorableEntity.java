package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;

public interface IAnchorableEntity {
    public boolean isAnchored();
    public Entity getAnchor();
    public Entity getAnchorClientSide();
    public void setAnchoredTo(@Nullable Entity e);
    public void setAnchor(@Nullable Entity e);
}
