package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.entity.BunkBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.LockableBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.TrayBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CuffedMod.MODID);

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GuillotineBlockEntity>> GUILLOTINE = BLOCK_ENTITIES
                        .register("guillotine_block_entity", () -> BlockEntityType.Builder
                                        .of(GuillotineBlockEntity::new, ModBlocks.GUILLOTINE.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SafeBlockEntity>> SAFE_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("safe_block_entity", () -> BlockEntityType.Builder
                                        .of(SafeBlockEntity::new, ModBlocks.SAFE.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BunkBlockEntity>> BUNK_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("bunk_block_entity", () -> BlockEntityType.Builder
                                        .of(BunkBlockEntity::new, ModBlocks.BUNK.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrayBlockEntity>> TRAY = BLOCK_ENTITIES
                        .register("tray_block_entity", () -> BlockEntityType.Builder
                                        .of(TrayBlockEntity::new, ModBlocks.TRAY.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockableBlockEntity>> CELL_DOOR_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("cell_door_block_entity", () -> BlockEntityType.Builder
                                        .of(LockableBlockEntity::new, ModBlocks.CELL_DOOR.get()).build(null));

        public static void register(IEventBus eventBus) {
                BLOCK_ENTITIES.register(eventBus);
        }
}