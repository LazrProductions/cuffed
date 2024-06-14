package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CuffedMod.MODID);

    public static final RegistryObject<BlockEntityType<GuillotineBlockEntity>> GUILLOTINE =
            BLOCK_ENTITIES.register("guillotine_block_entity", () ->
                    BlockEntityType.Builder.of(GuillotineBlockEntity::new,
                            ModBlocks.GUILLOTINE.get()).build(null));
                            
    public static final RegistryObject<BlockEntityType<SafeBlockEntity>> SAFE_BLOCK_ENTITY =
    BLOCK_ENTITIES.register("safe_block_entity", () ->
            BlockEntityType.Builder.of(SafeBlockEntity::new,
                    ModBlocks.SAFE.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
