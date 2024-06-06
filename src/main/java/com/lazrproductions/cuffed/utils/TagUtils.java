package com.lazrproductions.cuffed.utils;

import com.lazrproductions.cuffed.items.KeyItem;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class TagUtils {
    public static BlockPos getBlockPos(CompoundTag tag) {
        int x = tag.getIntArray(KeyItem.TAG_POSITION)[0];
        int y = tag.getIntArray(KeyItem.TAG_POSITION)[1];
        int z = tag.getIntArray(KeyItem.TAG_POSITION)[2];
        return new BlockPos(x, y, z);
    }

    public static CompoundTag toTag(BlockPos pos) {
        CompoundTag compoundtag1 = new CompoundTag();
        compoundtag1.putIntArray(KeyItem.TAG_POSITION, new int[] { pos.getX(), pos.getY(), pos.getZ() });
        return compoundtag1;
    }
}
