package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface INicknamable {
    public Component getNickname();
    public void setNickname(@Nullable Component value);
    public String serializeNickname();
    public void deserializeNickname(CompoundTag tag);
    public void deserializeNickname(String nickTag);
}
