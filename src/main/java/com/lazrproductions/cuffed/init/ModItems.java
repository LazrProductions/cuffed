package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.Key;
import com.lazrproductions.cuffed.items.KeyRing;
import com.lazrproductions.cuffed.items.Handcuffs;
import com.lazrproductions.cuffed.items.HandcuffsKey;
import com.lazrproductions.cuffed.items.Padlock;
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
        public static final RegistryObject<Item> KEY = ITEMS.register("key",
                        () -> new Key(new Item.Properties().stacksTo(16)));
        public static final RegistryObject<Item> KEY_RING = ITEMS.register("key_ring",
                        () -> new KeyRing(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> HANDCUFFS_KEY = ITEMS.register("handcuffs_key",
                        () -> new HandcuffsKey(new Item.Properties().stacksTo(16)));
        public static final RegistryObject<Item> HANDCUFFS = ITEMS.register("handcuffs",
                        () -> new Handcuffs(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> POSSESSIONSBOX = ITEMS.register("possessions_box",
                        () -> new PossessionsBox(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> PADLOCK = ITEMS.register("padlock",
                        () -> new Padlock(new Item.Properties().stacksTo(16)));
        public static final RegistryObject<Item> LOCKPICK = ITEMS.register("lockpick",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(3)));
                


        // Block Items
        public static final RegistryObject<Item> CELL_DOOR_ITEM = ITEMS.register("cell_door",
                        () -> new BlockItem(ModBlocks.CELL_DOOR.get(), new Item.Properties()));
                        
        public static final RegistryObject<Item> REINFORCED_STONE_ITEM = ITEMS.register("reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_CHISELED_ITEM = ITEMS.register("chiseled_reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_CHISELED.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_SLAB_ITEM = ITEMS.register("reinforced_stone_slab",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_SLAB.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_STAIRS_ITEM = ITEMS.register("reinforced_stone_stairs",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_STAIRS.get(), new Item.Properties()));

        
        public static void register(IEventBus bus) {
                ITEMS.register(bus);
        }
}
