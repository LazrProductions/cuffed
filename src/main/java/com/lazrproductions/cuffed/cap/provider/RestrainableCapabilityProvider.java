package com.lazrproductions.cuffed.cap.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.lazrproductions.cuffed.cap.RestrainableCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class RestrainableCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>{
    public static Capability<RestrainableCapability> CAP = CapabilityManager.get(new CapabilityToken<RestrainableCapability>() { });

    RestrainableCapability cap = null;
    final LazyOptional<RestrainableCapability> optional = LazyOptional.of(this::getCap);

    private RestrainableCapability getCap() {
        if(this.cap==null) {
            this.cap = new RestrainableCapability();
        }
        return this.cap;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CAP) {
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
