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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
                        CuffedMod.MODID);

        public static final DeferredHolder<Block, Block> CELL_DOOR = BLOCKS.register("cell_door",
                        () -> new CellDoor(
                                        BlockSetType.IRON,
                                        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion()
                                                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()
                                                        .sound(SoundType.METAL).pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)
                                        .pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> REINFORCED_LAMP = BLOCKS.register("reinforced_lamp",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.GLASS).mapColor(MapColor.ICE)
                                        .requiresCorrectToolForDrops().strength(3.0F, 8.0F)
                                        .noOcclusion()
                                        .emissiveRendering((state, getter, pos) -> {
                                                return true;
                                        }).lightLevel((state) -> {
                                                return 15;
                                        })
                                        .pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> REINFORCED_STONE_CHISELED = BLOCKS.register(
                        "chiseled_reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)
                                        .pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> REINFORCED_STONE_SLAB = BLOCKS.register("reinforced_stone_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(REINFORCED_STONE.get())));

        public static final DeferredHolder<Block, Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs",
                        () -> new StairBlock(REINFORCED_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.ofFullCopy(REINFORCED_STONE.get())));

        public static final DeferredHolder<Block, Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(REINFORCED_STONE.get())));

        public static final DeferredHolder<Block, Block> REINFORCED_BARS = BLOCKS.register("reinforced_bars",
                        () -> new ReinforcedBarsBlock(
                                        BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL)
                                                        .noOcclusion().strength(6.0F, 18.0F)
                                                        .pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> REINFORCED_BARS_GAPPED = BLOCKS.register("reinforced_bars_gap",
                        () -> new ReinforcedBarsGappedBlock(
                                        BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL)
                                                        .noOcclusion().strength(6.0F, 18.0F)
                                                        .pushReaction(PushReaction.IGNORE)));

        public static final DeferredHolder<Block, Block> PILLORY = BLOCKS.register("pillory",
                        () -> new PilloryBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)
                                        .mapColor(MapColor.WOOD).noCollission().strength(1.25F)));
        public static final DeferredHolder<Block, Block> GUILLOTINE = BLOCKS.register("guillotine",
                        () -> new GuillotineBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)
                                        .mapColor(MapColor.WOOD).noCollission().strength(1.25F)));

        public static final DeferredHolder<Block, Block> SAFE = BLOCKS.register("safe",
                        () -> new SafeBlock(BlockBehaviour.Properties.of().sound(SoundType.NETHERITE_BLOCK)
                                        .mapColor(MapColor.COLOR_GRAY).noOcclusion().strength(6.0F, 18.0F)));

        public static final DeferredHolder<Block, Block> BUNK = BLOCKS.register("bunk",
                        () -> new BunkBlock(BlockBehaviour.Properties.of().sound(SoundType.NETHERITE_BLOCK)
                                        .mapColor(MapColor.COLOR_GRAY).noOcclusion().strength(6.0F, 18.0F)));

        public static final DeferredHolder<Block, Block> POSTER = BLOCKS.register("poster",
                        () -> new PosterBlock(BlockBehaviour.Properties.of().sound(SoundType.SCAFFOLDING)
                                        .mapColor(MapColor.COLOR_RED).noOcclusion().instabreak()));

        public static final DeferredHolder<Block, Block> TRAY = BLOCKS.register("tray",
                        () -> new TrayBlock(BlockBehaviour.Properties.of().sound(SoundType.LANTERN)
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion()));

        // public static final DeferredHolder<Block, Block> TOILET = BLOCKS.register("toilet",
        // () -> new
        // ToiletBlock(BlockBehaviour.Properties.of().sound(SoundType.BONE_BLOCK)
        // .mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(0.75F)));

        public static void register(IEventBus bus) {
                BLOCKS.register(bus);
        }
}