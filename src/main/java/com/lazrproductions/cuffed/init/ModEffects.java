package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.effect.RestrainedEffect;
import com.lazrproductions.cuffed.effect.WoundedEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT,
            CuffedMod.MODID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<MobEffect, MobEffect> RESTRAINED_EFFECT = MOB_EFFECTS.register("restrained",
            () -> new RestrainedEffect(MobEffectCategory.HARMFUL, 0x000000));
            
    public static final net.neoforged.neoforge.registries.DeferredHolder<MobEffect, MobEffect> WOUNDED_EFFECT = MOB_EFFECTS.register("wounded",
            () -> new WoundedEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}
