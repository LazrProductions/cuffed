package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.entity.BunkBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.TrayBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, CuffedMod.MODID);

        public static final RegistryObject<BlockEntityType<GuillotineBlockEntity>> GUILLOTINE = BLOCK_ENTITIES.register(
                        "guillotine_block_entity", () -> BlockEntityType.Builder.of(GuillotineBlockEntity::new,
                                        ModBlocks.GUILLOTINE.get()).build(null));

        public static final RegistryObject<BlockEntityType<SafeBlockEntity>> SAFE_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("safe_block_entity", () -> BlockEntityType.Builder.of(SafeBlockEntity::new,
                                        ModBlocks.SAFE.get()).build(null));

        public static final RegistryObject<BlockEntityType<BunkBlockEntity>> BUNK_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("bunk_block_entity", () -> BlockEntityType.Builder.of(BunkBlockEntity::new,
                                        ModBlocks.BUNK.get()).build(null));
                                        
        public static final RegistryObject<BlockEntityType<TrayBlockEntity>> TRAY = BLOCK_ENTITIES
                        .register("tray_block_entity", () -> BlockEntityType.Builder.of(TrayBlockEntity::new,
                                        ModBlocks.TRAY.get()).build(null));
        
        //public static final RegistryObject<BlockEntityType<ToiletBlockEntity>> TOILET = BLOCK_ENTITIES
        //                .register("toilet_block_entity", () -> BlockEntityType.Builder.of(ToiletBlockEntity::new,
        //                                ModBlocks.TOILET.get()).build(null));

        public static void register(IEventBus eventBus) {
                BLOCK_ENTITIES.register(eventBus);
        }
}
