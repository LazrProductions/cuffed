package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, CuffedMod.MODID);

    // public static EntityType<ChainKnotEntity> CHAIN_KNOT;
    public static RegistryObject<EntityType<ChainKnotEntity>> CHAIN_KNOT = ENTITY_TYPES.register("chain_knot",
            () -> EntityType.Builder.<ChainKnotEntity>of(ChainKnotEntity::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(6 / 16f, 0.5f).canSpawnFarFromPlayer().fireImmune()
                    .build(new ResourceLocation(CuffedMod.MODID, "chain_knot").toString()));

    public static RegistryObject<EntityType<PadlockEntity>> PADLOCK = ENTITY_TYPES.register("padlock",
            () -> EntityType.Builder.<PadlockEntity>of(PadlockEntity::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(6 / 16f, 0.1f).canSpawnFarFromPlayer().fireImmune()
                    .build(new ResourceLocation(CuffedMod.MODID, "padlock").toString()));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}