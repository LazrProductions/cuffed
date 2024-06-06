package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.enchantment.BuoyantEnchantment;
import com.lazrproductions.cuffed.enchantment.DrainEnchantment;
import com.lazrproductions.cuffed.enchantment.ExhaustEnchantment;
import com.lazrproductions.cuffed.enchantment.FamineEnchantment;
import com.lazrproductions.cuffed.enchantment.ImbueEnchantment;
import com.lazrproductions.cuffed.enchantment.ShroudEnchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
        public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister
                        .create(ForgeRegistries.ENCHANTMENTS, CuffedMod.MODID);

        public static final RegistryObject<Enchantment> IMBUE = ENCHANTMENTS.register("imbue",
                        () -> new ImbueEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));
        public static final RegistryObject<Enchantment> FAMINE = ENCHANTMENTS.register("famine",
                        () -> new FamineEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));
        public static final RegistryObject<Enchantment> SHROUD = ENCHANTMENTS.register("shroud",
                        () -> new ShroudEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));
        public static final RegistryObject<Enchantment> EXHAUST = ENCHANTMENTS.register("exhaust",
                        () -> new ExhaustEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));
        public static final RegistryObject<Enchantment> SILENCE = ENCHANTMENTS.register("silence",
                        () -> new DrainEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));
        public static final RegistryObject<Enchantment> BUOYANT = ENCHANTMENTS.register("buoyant",
                        () -> new BuoyantEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));

        public static void register(IEventBus bus) {
                ENCHANTMENTS.register(bus);
        }
}
