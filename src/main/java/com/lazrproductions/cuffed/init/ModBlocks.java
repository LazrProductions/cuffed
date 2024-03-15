package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.ReinforcedBarsBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
                        CuffedMod.MODID);

        public static final RegistryObject<Block> CELL_DOOR = BLOCKS.register("cell_door",
                        () -> new CellDoor(
                                        BlockBehaviour.Properties.of(Material.METAL).color(MaterialColor.METAL).noOcclusion()
                                                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()
                                                        .sound(SoundType.METAL)));

                                        
        public static final RegistryObject<Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.STONE).color(MaterialColor.METAL)
                                        .noOcclusion().requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

        public static final RegistryObject<Block> REINFORCED_STONE_CHISELED = BLOCKS.register(
                        "chiseled_reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.STONE).color(MaterialColor.METAL)
                                        .noOcclusion().requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

        public static final RegistryObject<Block> REINFORCED_STONE_SLAB = BLOCKS.register("reinforced_stone_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.STONE).color(MaterialColor.METAL)
                                                        .noOcclusion().requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

        public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs",
                        () -> new StairBlock(() -> REINFORCED_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));
        
        public static final RegistryObject<Block> REINFORCED_BARS = BLOCKS.register("reinforced_bars",
                () -> new ReinforcedBarsBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).color(MaterialColor.METAL)
                                .noOcclusion().strength(5.0F, 6.0F)));


        public static final RegistryObject<Block> PILLORY = BLOCKS.register("pillory",
                        () -> new PilloryBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD)
                                        .color(MaterialColor.WOOD).noCollission().strength(1.25F)));

        public static void register(IEventBus bus) {
                BLOCKS.register(bus);
        }
}
