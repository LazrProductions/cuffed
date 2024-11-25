package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;

public interface IAnchorableEntity {
    public boolean isAnchored();
    public Entity getAnchor();
    public Entity getAnchorClientSide();
    /**
     * Set the anchor of this entity, if null than unanchor the player and drop a chain
     */
    public void setAnchoredTo(@Nullable Entity e);
    public void setAnchor(@Nullable Entity e);
}
