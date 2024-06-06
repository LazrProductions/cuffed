package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nonnull;

import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDetainableEntity {
    public void detainToBlock(@Nonnull Level level, Vector3f detainPos, @Nonnull BlockPos pos, int detaintType, float facingRotation);
    public void undetain();

    public int getDetained();
    public void setDetained(int value);

    public float getDetainedRotation();
    public void setDetainedRotation(float value);
    
    public BlockState getBlockDetainedTo(@Nonnull Level level);
    public void setBlockDetainedTo(@Nonnull BlockPos pos);

    public Vector3f getDetainedPosition();
    public void setDetainedPosition(Vector3f value);
}
