package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.effect.RestrainedEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            CuffedMod.MODID);

    public static final RegistryObject<MobEffect> RESTRAINED_EFFECT = MOB_EFFECTS.register("restrained",
            () -> new RestrainedEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}
