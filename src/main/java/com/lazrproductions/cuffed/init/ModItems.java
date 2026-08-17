package com.lazrproductions.cuffed.init;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.items.KnifeItem;
import com.lazrproductions.cuffed.items.BandageItem;
import com.lazrproductions.cuffed.items.CreativeKey;
import com.lazrproductions.cuffed.items.CreativeRestraintCutter;
import com.lazrproductions.cuffed.items.DuckTapeItem;
import com.lazrproductions.cuffed.items.FuzzyHandcuffsItem;
import com.lazrproductions.cuffed.items.HandcuffsItem;
import com.lazrproductions.cuffed.items.Padlock;
import com.lazrproductions.cuffed.items.PossessionsBox;
import com.lazrproductions.cuffed.items.PosterBlockItem;
import com.lazrproductions.cuffed.items.PrisonerTagItem;
import com.lazrproductions.cuffed.items.ShacklesItem;
import com.lazrproductions.cuffed.items.TrayItem;
import com.lazrproductions.cuffed.items.WeightedAnchorItem;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM,
                        CuffedMod.MODID);

        // Normal Items
        public static final DeferredHolder<Item, Item> KEY = ITEMS.register("key",
                        () -> new KeyItem(new Item.Properties().stacksTo(1)));
        public static final DeferredHolder<Item, Item> KEY_RING = ITEMS.register("key_ring",
                        () -> new KeyRingItem(new Item.Properties().stacksTo(1)));
        public static final DeferredHolder<Item, Item> KEY_MOLD = ITEMS.register("key_mold",
                        () -> new KeyMoldItem(new Item.Properties().stacksTo(1)));
        public static final DeferredHolder<Item, Item> BAKED_KEY_MOLD = ITEMS.register("baked_key_mold",
                        () -> new BakedKeyMoldItem(new Item.Properties().stacksTo(1)));
                        
                        
        public static final DeferredHolder<Item, Item> HANDCUFFS_KEY = ITEMS.register("handcuffs_key",
                        () -> new AbstractRestraintKeyItem(new Item.Properties().stacksTo(1)));
        public static final DeferredHolder<Item, Item> SHACKLES_KEY = ITEMS.register("shackles_key",
                        () -> new AbstractRestraintKeyItem(new Item.Properties().stacksTo(1)));


        public static final DeferredHolder<Item, Item> HANDCUFFS = ITEMS.register("handcuffs",
                        () -> new HandcuffsItem(new Item.Properties().stacksTo(1)
                                .durability(999)));
        public static final DeferredHolder<Item, Item> FUZZY_HANDCUFFS = ITEMS.register("fuzzy_handcuffs",
                        () -> new FuzzyHandcuffsItem(new Item.Properties().stacksTo(1)
                                .durability(999)));
        public static final DeferredHolder<Item, Item> SHACKLES = ITEMS.register("shackles",
                        () -> new ShacklesItem(new Item.Properties().stacksTo(1)
                                .durability(999)));


        public static final DeferredHolder<Item, Item> WEIGHTED_ANCHOR_ITEM = ITEMS.register("weighted_anchor",
                        () -> new WeightedAnchorItem(new Item.Properties().stacksTo(1)));

        // public static final DeferredHolder<Item, Item> INFORMATION_BOOKLET = ITEMS.register("information_booklet",
        //                 () -> new InformationBookletItem(new Item.Properties().stacksTo(1)));

        public static final DeferredHolder<Item, Item> POSSESSIONSBOX = ITEMS.register("possessions_box",
                        () -> new PossessionsBox(new Item.Properties().stacksTo(1)));

        public static final DeferredHolder<Item, Item> PADLOCK = ITEMS.register("padlock",
                        () -> new Padlock(new Item.Properties().stacksTo(16)));
        public static final DeferredHolder<Item, Item> LOCKPICK = ITEMS.register("lockpick",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(3)));
        
        public static final DeferredHolder<Item, Item> PRISONER_TAG = ITEMS.register("prisoner_tag",
                        () -> new PrisonerTagItem(new Item.Properties().stacksTo(1)));


                        
        public static final DeferredHolder<Item, Item> FORK = ITEMS.register("fork",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(5)));
        public static final DeferredHolder<Item, Item> SPOON = ITEMS.register("spoon",
                        () -> new Item(new Item.Properties().stacksTo(1).durability(5)));
        public static final DeferredHolder<Item, Item> KNIFE = ITEMS.register("knife",
                        () -> new KnifeItem(new Item.Properties().stacksTo(1).durability(5)));
                                

        public static final DeferredHolder<Item, Item> DUCK_TAPE = ITEMS.register("duck_tape", () -> new DuckTapeItem(new Item.Properties()));
        public static final DeferredHolder<Item, Item> BANDAGE = ITEMS.register("bandage", () -> new BandageItem(new Item.Properties()));

        // Block Items
        public static final DeferredHolder<Item, Item> CELL_DOOR_ITEM = ITEMS.register("cell_door",
                        () -> new BlockItem(ModBlocks.CELL_DOOR.get(), new Item.Properties()));
                        
        public static final DeferredHolder<Item, Item> REINFORCED_STONE_ITEM = ITEMS.register("reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_SMOOTH_STONE_ITEM = ITEMS.register("reinforced_smooth_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_SMOOTH_STONE.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_LAMP_ITEM = ITEMS.register("reinforced_lamp",
                        () -> new BlockItem(ModBlocks.REINFORCED_LAMP.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_STONE_CHISELED_ITEM = ITEMS.register("chiseled_reinforced_stone",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_CHISELED.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_STONE_SLAB_ITEM = ITEMS.register("reinforced_stone_slab",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_SLAB.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_STONE_STAIRS_ITEM = ITEMS.register("reinforced_stone_stairs",
                        () -> new BlockItem(ModBlocks.REINFORCED_STONE_STAIRS.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_BARS_ITEM = ITEMS.register("reinforced_bars",
                        () -> new BlockItem(ModBlocks.REINFORCED_BARS.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> REINFORCED_BARS_GAPPED_ITEM = ITEMS.register("reinforced_bars_gap",
                        () -> new BlockItem(ModBlocks.REINFORCED_BARS_GAPPED.get(), new Item.Properties()));

        public static final DeferredHolder<Item, Item> PILLORY_ITEM = ITEMS.register("pillory",
                        () -> new BlockItem(ModBlocks.PILLORY.get(), new Item.Properties()));
        public static final DeferredHolder<Item, Item> GUILLOTINE_ITEM = ITEMS.register("guillotine",
                        () -> new BlockItem(ModBlocks.GUILLOTINE.get(), new Item.Properties()));
        
        public static final DeferredHolder<Item, Item> SAFE_ITEM = ITEMS.register("safe",
                        () -> new BlockItem(ModBlocks.SAFE.get(), new Item.Properties()));
        
        public static final DeferredHolder<Item, Item> BUNK_ITEM = ITEMS.register("bunk",
                        () -> new BlockItem(ModBlocks.BUNK.get(), new Item.Properties().stacksTo(1)));
                        
        public static final DeferredHolder<Item, Item> POSTER_ITEM = ITEMS.register("poster",
                        () -> new PosterBlockItem(ModBlocks.POSTER.get(), new Item.Properties().stacksTo(1)));
        
        public static final DeferredHolder<Item, Item> TRAY = ITEMS.register("tray",
                        () -> new TrayItem(ModBlocks.TRAY.get(), new Item.Properties().stacksTo(1)));
                        
        //public static final DeferredHolder<Item, Item> TOILET_ITEM = ITEMS.register("toilet",
        //        () -> new BlockItem(ModBlocks.TOILET.get(), new Item.Properties()));


        // Creative Items
        public static final DeferredHolder<Item, Item> CREATIVE_RESTRAINT_CUTTER = ITEMS.register("creative_restraint_cutter", () -> new CreativeRestraintCutter(new Item.Properties()));
        public static final DeferredHolder<Item, Item> CREATIVE_KEY = ITEMS.register("creative_key", () -> new CreativeKey(new Item.Properties().rarity(Rarity.EPIC)));
        public static final DeferredHolder<Item, Item> CREATIVE_BIND_BREAKER = ITEMS.register("creative_bind_breaker", () -> new CreativeKey(new Item.Properties().rarity(Rarity.EPIC)));


        public static void register(IEventBus bus) {
                ITEMS.register(bus);
        }
}