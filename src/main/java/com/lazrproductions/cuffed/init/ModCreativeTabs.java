package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, CuffedMod.MODID);

    public static final RegistryObject<CreativeModeTab> CUFFED_TAB = CREATIVE_MODE_TABS.register("cuffed_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cuffed"))
                    .icon(() -> ModItems.HANDCUFFS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HANDCUFFS.get());
                        output.accept(ModItems.SHACKLES.get());
                        output.accept(ModItems.FUZZY_HANDCUFFS.get());

                        output.accept(ModItems.LEGCUFFS.get());
                        output.accept(ModItems.LEG_SHACKLES.get());

                        output.accept(ModItems.HANDCUFFS_KEY.get());
                        output.accept(ModItems.SHACKLES_KEY.get());

                        output.accept(ModItems.POSSESSIONSBOX.get());
                        output.accept(ModItems.PRISONER_TAG.get());

                        output.accept(ModItems.DUCK_TAPE.get());
                        output.accept(ModItems.BANDAGE.get());
                        
                        output.accept(ModItems.TRAY.get());
                        output.accept(ModItems.FORK.get());
                        output.accept(ModItems.SPOON.get());
                        output.accept(ModItems.KNIFE.get());

                        output.accept(ModItems.PADLOCK.get());
                        output.accept(ModItems.KEY.get());
                        output.accept(ModItems.KEY_RING.get());
                        output.accept(ModItems.KEY_MOLD.get());
                        output.accept(ModItems.BAKED_KEY_MOLD.get());
                        output.accept(ModItems.LOCKPICK.get());

                        
                        output.accept(ModItems.WEIGHTED_ANCHOR_ITEM.get());


                        output.accept(ModItems.CELL_DOOR_ITEM.get());
                        output.accept(ModItems.REINFORCED_BARS_ITEM.get());
                        output.accept(ModItems.REINFORCED_SMOOTH_STONE_ITEM.get());
                        output.accept(ModItems.REINFORCED_STONE_ITEM.get());
                        output.accept(ModItems.REINFORCED_STONE_SLAB_ITEM.get());
                        output.accept(ModItems.REINFORCED_STONE_STAIRS_ITEM.get());
                        output.accept(ModItems.REINFORCED_STONE_CHISELED_ITEM.get());
                        output.accept(ModItems.REINFORCED_LAMP_ITEM.get());


                        output.accept(ModItems.PILLORY_ITEM.get());
                        output.accept(ModItems.GUILLOTINE_ITEM.get());


                        output.accept(ModItems.SAFE_ITEM.get());
                        
                        
                        output.accept(ModItems.INFORMATION_BOOKLET.get());
                    }).build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
