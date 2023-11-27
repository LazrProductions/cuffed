package com.lazrproductions.cuffed.cap.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.lazrproductions.cuffed.cap.CuffedCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CuffedCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<CuffedCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<CuffedCapability>() {});

    CuffedCapability cap = null;
    final LazyOptional<CuffedCapability> optional = LazyOptional.of(this::getCap);

    private CuffedCapability getCap() {
        if(this.cap==null) {
            this.cap = new CuffedCapability();
        }
        return this.cap;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CAPABILITY) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return getCap().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getCap().deserializeNBT(nbt);
    }
}