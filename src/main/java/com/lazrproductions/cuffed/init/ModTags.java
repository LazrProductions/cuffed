package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> LOCKABLE_BLOCKS = tag("lockable_blocks");
        public static final TagKey<Block> REINFORCED_BLOCKS = tag("reinforced_blocks");

        public static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(CuffedMod.MODID, name));
        }
    }
    public static class Entities {
        public static final TagKey<EntityType<?>> CHAINABLE_ENTITIES = tag("chainable_entities");
        
        public static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CuffedMod.MODID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> CAN_REINFORCE_PADLOCK = tag("can_reinforce_padlock");
        
        public static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(CuffedMod.MODID, name));
        }
    }
}
