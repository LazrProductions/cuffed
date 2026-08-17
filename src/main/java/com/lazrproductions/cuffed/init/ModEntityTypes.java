package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.CrumblingBlockEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.entity.WeightedAnchorEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntityTypes {

        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
                        .create(Registries.ENTITY_TYPE, CuffedMod.MODID);

        public static net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<ChainKnotEntity>> CHAIN_KNOT = ENTITY_TYPES.register("chain_knot",
                         () -> EntityType.Builder.<ChainKnotEntity>of(ChainKnotEntity::new, MobCategory.MISC)
                                        .clientTrackingRange(10)
                                        .updateInterval(Integer.MAX_VALUE)
                                        .setShouldReceiveVelocityUpdates(false)
                                        .sized(6 / 16f, 0.5f).canSpawnFarFromPlayer().fireImmune()
                                        .build("chain_knot"));

        public static net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<PadlockEntity>> PADLOCK = ENTITY_TYPES.register("padlock",
                         () -> EntityType.Builder.<PadlockEntity>of(PadlockEntity::new, MobCategory.MISC)
                                        .clientTrackingRange(10)
                                        .updateInterval(Integer.MAX_VALUE)
                                        .setShouldReceiveVelocityUpdates(false)
                                        .sized(6 / 16f, 0.1f).canSpawnFarFromPlayer().fireImmune()
                                        .build("padlock"));

        public static net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<WeightedAnchorEntity>> WEIGHTED_ANCHOR = ENTITY_TYPES.register(
                         "weighted_anchor",
                        () -> EntityType.Builder.<WeightedAnchorEntity>of(WeightedAnchorEntity::new, MobCategory.MISC)
                                        .sized(8 / 16f, 0.5f).canSpawnFarFromPlayer().fireImmune()
                                        .clientTrackingRange(10)
                                        .build("weighted_anchor"));

        public static net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<CrumblingBlockEntity>> CRUMBLING_BLOCK = ENTITY_TYPES.register(
                         "crumbling_block",
                        () -> EntityType.Builder.<CrumblingBlockEntity>of(CrumblingBlockEntity::new, MobCategory.MISC)
                                        .sized(0.3F, 0.3F)
                                        .build("crumbling_block"));

        public static void register(IEventBus bus) {
                ENTITY_TYPES.register(bus);
        }

        public static void registerAttributes(EntityAttributeCreationEvent event) {
                event.put(ModEntityTypes.WEIGHTED_ANCHOR.get(), WeightedAnchorEntity.createAttributes().build());
        }
}