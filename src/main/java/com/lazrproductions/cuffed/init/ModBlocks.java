package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.BunkBlock;
import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.GuillotineBlock;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.PosterBlock;
import com.lazrproductions.cuffed.blocks.ReinforcedBarsBlock;
import com.lazrproductions.cuffed.blocks.ReinforcedBarsGappedBlock;
import com.lazrproductions.cuffed.blocks.SafeBlock;
import com.lazrproductions.cuffed.blocks.TrayBlock;

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
                                        BlockBehaviour.Properties.of(Material.HEAVY_METAL).color(MaterialColor.METAL)
                                                        .noOcclusion()
                                                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()
                                                        .sound(SoundType.METAL)));

        public static final RegistryObject<Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE)
                                        .color(MaterialColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)));

        public static final RegistryObject<Block> REINFORCED_LAMP = BLOCKS.register("reinforced_lamp",
                        () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS)
                                        .color(MaterialColor.METAL)
                                        .requiresCorrectToolForDrops().strength(3.0F, 8.0F)
                                        .noOcclusion()
                                        .emissiveRendering((state, getter, pos) -> {
                                                return true;
                                        }).lightLevel((state) -> {
                                                return 15;
                                        })));

        public static final RegistryObject<Block> REINFORCED_STONE_CHISELED = BLOCKS.register(
                        "chiseled_reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE)
                                        .color(MaterialColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)));

        public static final RegistryObject<Block> REINFORCED_STONE_SLAB = BLOCKS.register("reinforced_stone_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs",
                        () -> new StairBlock(() -> REINFORCED_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone",
                        () -> new Block(BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_BARS = BLOCKS.register("reinforced_bars",
                        () -> new ReinforcedBarsBlock(
                                        BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.METAL)
                                                        .color(MaterialColor.METAL)
                                                        .noOcclusion().strength(6.0F, 18.0F)));

        public static final RegistryObject<Block> REINFORCED_BARS_GAPPED = BLOCKS.register("reinforced_bars_gap",
                        () -> new ReinforcedBarsGappedBlock(
                                        BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.METAL)
                                                        .color(MaterialColor.METAL)
                                                        .noOcclusion().strength(6.0F, 18.0F)));

        public static final RegistryObject<Block> PILLORY = BLOCKS.register("pillory",
                        () -> new PilloryBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.WOOD)
                                        .color(MaterialColor.METAL).noCollission().strength(1.25F)));
        public static final RegistryObject<Block> GUILLOTINE = BLOCKS.register("guillotine",
                        () -> new GuillotineBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.WOOD)
                                        .color(MaterialColor.METAL).noCollission().strength(1.25F)));

        public static final RegistryObject<Block> SAFE = BLOCKS.register("safe",
                        () -> new SafeBlock(BlockBehaviour.Properties.of(Material.STONE)
                                        .sound(SoundType.NETHERITE_BLOCK)
                                        .color(MaterialColor.METAL).noOcclusion().strength(6.0F, 18.0F)));

        public static final RegistryObject<Block> BUNK = BLOCKS.register("bunk",
                        () -> new BunkBlock(BlockBehaviour.Properties.of(Material.STONE)
                                        .sound(SoundType.NETHERITE_BLOCK)
                                        .color(MaterialColor.METAL).noOcclusion().strength(6.0F, 18.0F)));

        public static final RegistryObject<Block> POSTER = BLOCKS.register("poster",
                        () -> new PosterBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.SCAFFOLDING)
                                        .color(MaterialColor.METAL).noOcclusion().instabreak()));

        public static final RegistryObject<Block> TRAY = BLOCKS.register("tray",
                        () -> new TrayBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.LANTERN)
                                        .color(MaterialColor.METAL).noOcclusion()));

        // public static final RegistryObject<Block> TOILET = BLOCKS.register("toilet",
        // () -> new
        // ToiletBlock(BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK)
        // .mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(0.75F)));

        public static void register(IEventBus bus) {
                BLOCKS.register(bus);
        }
}
