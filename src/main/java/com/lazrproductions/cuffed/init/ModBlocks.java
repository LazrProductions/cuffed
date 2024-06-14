package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.GuillotineBlock;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.ReinforcedBarsBlock;
import com.lazrproductions.cuffed.blocks.SafeBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
                        CuffedMod.MODID);

        public static final RegistryObject<Block> CELL_DOOR = BLOCKS.register("cell_door",
                        () -> new CellDoor(
                                        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion()
                                                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()
                                                        .sound(SoundType.METAL).pushReaction(PushReaction.IGNORE),
                                        BlockSetType.IRON));

        public static final RegistryObject<Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)
                                        .pushReaction(PushReaction.IGNORE)));

        public static final RegistryObject<Block> REINFORCED_LAMP = BLOCKS.register("reinforced_lamp",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.GLASS).mapColor(MapColor.ICE)
                                        .requiresCorrectToolForDrops().strength(3.0F, 8.0F)
                                        .noOcclusion()
                                        .emissiveRendering((state, getter, pos) -> {
                                                return true;
                                        }).lightLevel((state) -> {
                                                return 15;
                                        })
                                        .pushReaction(PushReaction.IGNORE)));

        public static final RegistryObject<Block> REINFORCED_STONE_CHISELED = BLOCKS.register(
                        "chiseled_reinforced_stone",
                        () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops().strength(6.0F, 12.0F)
                                        .pushReaction(PushReaction.IGNORE)));

        public static final RegistryObject<Block> REINFORCED_STONE_SLAB = BLOCKS.register("reinforced_stone_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs",
                        () -> new StairBlock(() -> REINFORCED_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone",
                        () -> new Block(BlockBehaviour.Properties.copy(REINFORCED_STONE.get())));

        public static final RegistryObject<Block> REINFORCED_BARS = BLOCKS.register("reinforced_bars",
                        () -> new ReinforcedBarsBlock(
                                        BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL)
                                                        .noOcclusion().strength(6.0F, 18.0F)
                                                        .pushReaction(PushReaction.IGNORE)));

        public static final RegistryObject<Block> PILLORY = BLOCKS.register("pillory",
                        () -> new PilloryBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)
                                        .mapColor(MapColor.WOOD).noCollission().strength(1.25F)));
        public static final RegistryObject<Block> GUILLOTINE = BLOCKS.register("guillotine",
                        () -> new GuillotineBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)
                                        .mapColor(MapColor.WOOD).noCollission().strength(1.25F)));

        public static final RegistryObject<Block> SAFE = BLOCKS.register("safe", 
                        () -> new SafeBlock(BlockBehaviour.Properties.of().sound(SoundType.NETHERITE_BLOCK)
                                        .mapColor(MapColor.COLOR_GRAY).noOcclusion().strength(6.0F, 18.0F)));

        public static void register(IEventBus bus) {
                BLOCKS.register(bus);
        }
}
