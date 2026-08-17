package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> IMBUE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "imbue"));
    public static final ResourceKey<Enchantment> FAMINE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "famine"));
    public static final ResourceKey<Enchantment> SHROUD = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "shroud"));
    public static final ResourceKey<Enchantment> EXHAUST = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "exhaust"));
    public static final ResourceKey<Enchantment> SILENCE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "silence"));
    public static final ResourceKey<Enchantment> BUOYANT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "buoyant"));
}
