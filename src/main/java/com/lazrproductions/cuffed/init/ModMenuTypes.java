package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.inventory.FriskingMenu;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CuffedMod.MODID);

    public static final RegistryObject<MenuType<FriskingMenu>> FRISKING_MENU = MENU_TYPES.register("frisking_menu",
            () -> new MenuType<>(FriskingMenu::new, FeatureFlags.REGISTRY.allFlags()));
            
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
