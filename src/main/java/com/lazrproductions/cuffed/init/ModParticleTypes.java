package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.client.particle.BloodDripParticle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CuffedMod.MODID);

    public static final RegistryObject<SimpleParticleType> BLOOD_DRIP_FALL_PARTICLE = PARTICLE_TYPES.register("blood_drip", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    public static void registerSprites(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.BLOOD_DRIP_FALL_PARTICLE.get(), BloodDripParticle.Provider::new);
    }
}
