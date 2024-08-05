package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.CrumblingBlockEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.entity.WeightedAnchorEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.ENTITY_TYPES, CuffedMod.MODID);

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

        public static RegistryObject<EntityType<WeightedAnchorEntity>> WEIGHTED_ANCHOR = ENTITY_TYPES.register(
                        "weighted_anchor",
                        () -> EntityType.Builder.<WeightedAnchorEntity>of(WeightedAnchorEntity::new, MobCategory.MISC)
                                        .sized(8 / 16f, 0.5f).canSpawnFarFromPlayer().fireImmune()
                                        .clientTrackingRange(10)
                                        .build(new ResourceLocation(CuffedMod.MODID, "weighted_anchor").toString()));

        public static RegistryObject<EntityType<CrumblingBlockEntity>> CRUMBLING_BLOCK = ENTITY_TYPES.register(
                        "crumbling_block",
                        () -> EntityType.Builder.<CrumblingBlockEntity>of(CrumblingBlockEntity::new, MobCategory.MISC)
                                        .sized(0.3F, 0.3F).build("crumbling_block"));

        public static void register(IEventBus bus) {
                ENTITY_TYPES.register(bus);
        }

        public static void registerAttributes(EntityAttributeCreationEvent event) {
                event.put(ModEntityTypes.WEIGHTED_ANCHOR.get(), WeightedAnchorEntity.createAttributes().build());
        }
}