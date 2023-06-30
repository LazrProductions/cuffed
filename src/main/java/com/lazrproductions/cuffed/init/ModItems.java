package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.CellKey;
import com.lazrproductions.cuffed.items.CellKeyRing;
import com.lazrproductions.cuffed.items.Handcuffs;
import com.lazrproductions.cuffed.items.HandcuffsKey;
import com.lazrproductions.cuffed.items.PossessionsBox;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        CuffedMod.MODID);

        // Normal Items
        public static final RegistryObject<Item> CELL_KEY = ITEMS.register("cell_key",
                        () -> new CellKey(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> CELL_KEY_RING = ITEMS.register("cell_key_ring",
                        () -> new CellKeyRing(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> HANDCUFFS_KEY = ITEMS.register("handcuffs_key",
                        () -> new HandcuffsKey(new Item.Properties().stacksTo(16)));
        public static final RegistryObject<Item> HANDCUFFS = ITEMS.register("handcuffs",
                        () -> new Handcuffs(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> POSSESSIONSBOX = ITEMS.register("possessions_box",
                        () -> new PossessionsBox(new Item.Properties().stacksTo(1)));

        // Block Items
        public static final RegistryObject<Item> CELL_DOOR_ITEM = ITEMS.register("cell_door",
                        () -> new BlockItem(ModBlocks.CELL_DOOR.get(), new Item.Properties()));

        public static void register(IEventBus bus) {
                ITEMS.register(bus);
        }
}
