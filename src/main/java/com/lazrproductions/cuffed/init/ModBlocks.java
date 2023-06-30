package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.CellDoor;

import net.minecraft.world.level.block.Block;
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
            () -> new CellDoor(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().requiresCorrectToolForDrops().strength(5.0F).pushReaction(PushReaction.DESTROY),
                    BlockSetType.IRON));


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
