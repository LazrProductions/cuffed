package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.items.KnifeItem;
import com.lazrproductions.cuffed.items.LegShacklesItem;
import com.lazrproductions.cuffed.items.LegcuffsItem;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.items.HandcuffsItem;
import com.lazrproductions.cuffed.items.InformationBookletItem;
import com.lazrproductions.cuffed.items.Padlock;
import com.lazrproductions.cuffed.items.PossessionsBox;
import com.lazrproductions.cuffed.items.PosterBlockItem;
import com.lazrproductions.cuffed.items.PrisonerTagItem;
import com.lazrproductions.cuffed.items.ShacklesItem;
import com.lazrproductions.cuffed.items.TrayItem;
import com.lazrproductions.cuffed.items.WeightedAnchorItem;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;

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
                        () -> new KeyItem(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> KEY_RING = ITEMS.register("key_ring",
                        () -> new KeyRingItem(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> KEY_MOLD = ITEMS.register("key_mold",
                        () -> new KeyMoldItem(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> BAKED_KEY_MOLD = ITEMS.register("baked_key_mold",
                        () -> new BakedKeyMoldItem(new Item.Properties().stacksTo(1)));
                        
                        
        public static final RegistryObject<Item> HANDCUFFS_KEY = ITEMS.register("handcuffs_key",
                        () -> new AbstractRestraintKeyItem(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<Item> SHACKLES_KEY = ITEMS.register("shackles_key",
                        () -> new AbstractRestraintKeyItem(new Item.Properties().stacksTo(1)));


        public static final RegistryObject<Item> HANDCUFFS = ITEMS.register("handcuffs",
                        () -> new HandcuffsItem(new Item.Properties().stacksTo(1)
                                .durability(40)
                                .defaultDurability(40)));
        public static final RegistryObject<Item> FUZZY_HANDCUFFS = ITEMS.register("fuzzy_handcuffs",
                        () -> new HandcuffsItem(new Item.Properties().stacksTo(1)
                                .durability(30)
                                .defaultDurability(30)));
        public static final RegistryObject<Item> SHACKLES = ITEMS.register("shackles",
                        () -> new ShacklesItem(new Item.Properties().stacksTo(1)
                                .durability(15)
                                .defaultDurability(15)));
                        
        public static final RegistryObject<Item> LEGCUFFS = ITEMS.register("legcuffs",
                        () -> new LegcuffsItem(new Item.Properties().stacksTo(1)
                                .durability(40)
                                .defaultDurability(40)));
        public static final RegistryObject<Item> LEG_SHACKLES = ITEMS.register("leg_shackles",
                        () -> new LegShacklesItem(new Item.Properties().stacksTo(1)
                                .durability(15)
                                .defaultDurability(15)));


        public static final RegistryObject<Item> WEIGHTED_ANCHOR_ITEM = ITEMS.register("weighted_anchor",
                        () -> new WeightedAnchorItem(new Item.Properties().stacksTo(1)));

        public static final RegistryObject<Item> INFORMATION_BOOKLET = ITEMS.register("information_booklet",
                        () -> new InformationBookletItem(new Item.Properties().stacksTo(1)));

        public static final RegistryObject<Item> POSSESSIONSBOX = ITEMS.register("possessions_box",
                        () -> new PossessionsBox(new Item.Properties().stacksTo(1)));

        public static final RegistryObject<Item> PADLOCK = ITEMS.register("padlock",
                        () -> new Padlock(new Item.Properties().stacksTo(16)));
        public static final RegistryObject<Item> LOCKPICK = ITEMS.register("lockpick",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(3)));
        
        public static final RegistryObject<Item> PRISONER_TAG = ITEMS.register("prisoner_tag",
                        () -> new PrisonerTagItem(new Item.Properties().stacksTo(1)));


                        
        public static final RegistryObject<Item> FORK = ITEMS.register("fork",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(5)));
        public static final RegistryObject<Item> SPOON = ITEMS.register("spoon",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(5)));
        public static final RegistryObject<Item> KNIFE = ITEMS.register("knife",
                        () -> new KnifeItem(new Item.Properties().stacksTo(1).durability(5)));
                                

        // Block Items
        public static final RegistryObject<Item> CELL_DOOR_ITEM = ITEMS.register("cell_door",
                        () -> new BlockItem(ModBlocks.CELL_DOOR.get(), new Item.Properties()));
                        
        public static final RegistryObject<Item> REINFORCED_STONE_ITEM = ITEMS.register("reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_SMOOTH_STONE_ITEM = ITEMS.register("reinforced_smooth_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_SMOOTH_STONE.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_LAMP_ITEM = ITEMS.register("reinforced_lamp",
                        () -> new BlockItem(ModBlocks.REINFORCED_LAMP.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_CHISELED_ITEM = ITEMS.register("chiseled_reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_CHISELED.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_SLAB_ITEM = ITEMS.register("reinforced_stone_slab",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_SLAB.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_STONE_STAIRS_ITEM = ITEMS.register("reinforced_stone_stairs",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_STAIRS.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_BARS_ITEM = ITEMS.register("reinforced_bars",
                        () -> new BlockItem(ModBlocks.REINFORCED_BARS.get(), new Item.Properties()));
        public static final RegistryObject<Item> REINFORCED_BARS_GAPPED_ITEM = ITEMS.register("reinforced_bars_gap",
                        () -> new BlockItem(ModBlocks.REINFORCED_BARS_GAPPED.get(), new Item.Properties()));

        public static final RegistryObject<Item> PILLORY_ITEM = ITEMS.register("pillory",
                () -> new BlockItem(ModBlocks.PILLORY.get(), new Item.Properties()));
        public static final RegistryObject<Item> GUILLOTINE_ITEM = ITEMS.register("guillotine",
                () -> new BlockItem(ModBlocks.GUILLOTINE.get(), new Item.Properties()));
        
        public static final RegistryObject<Item> SAFE_ITEM = ITEMS.register("safe",
                () -> new BlockItem(ModBlocks.SAFE.get(), new Item.Properties()));
        
        public static final RegistryObject<Item> BUNK_ITEM = ITEMS.register("bunk",
                () -> new BlockItem(ModBlocks.BUNK.get(), new Item.Properties().stacksTo(1)));
                
        public static final RegistryObject<Item> POSTER_ITEM = ITEMS.register("poster",
                () -> new PosterBlockItem(ModBlocks.POSTER.get(), new Item.Properties().stacksTo(1)));
        
        public static final RegistryObject<Item> TRAY = ITEMS.register("tray",
                () -> new TrayItem(ModBlocks.TRAY.get(), new Item.Properties().stacksTo(1)));
                
        //public static final RegistryObject<Item> TOILET_ITEM = ITEMS.register("toilet",
        //        () -> new BlockItem(ModBlocks.TOILET.get(), new Item.Properties()));

        public static void register(IEventBus bus) {
                ITEMS.register(bus);
        }
}
