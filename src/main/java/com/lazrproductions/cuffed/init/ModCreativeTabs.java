package com.lazrproductions.cuffed.init;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final CreativeModeTab CUFFED_TAB = new CreativeModeTab(CuffedMod.MODID) {
        @Override
       public ItemStack makeIcon() {
            return ModItems.HANDCUFFS.get().getDefaultInstance();
       }

       @Override
       public void fillItemList(@Nonnull NonNullList<ItemStack> output) {
                        output.add(ModItems.HANDCUFFS.get().getDefaultInstance());
                        output.add(ModItems.SHACKLES.get().getDefaultInstance());
                        output.add(ModItems.FUZZY_HANDCUFFS.get().getDefaultInstance());

                        output.add(ModItems.LEGCUFFS.get().getDefaultInstance());
                        output.add(ModItems.LEG_SHACKLES.get().getDefaultInstance());

                        output.add(ModItems.HANDCUFFS_KEY.get().getDefaultInstance());
                        output.add(ModItems.SHACKLES_KEY.get().getDefaultInstance());

                        output.add(ModItems.POSSESSIONSBOX.get().getDefaultInstance());
                        output.add(ModItems.PRISONER_TAG.get().getDefaultInstance());

                        output.add(ModItems.PADLOCK.get().getDefaultInstance());
                        output.add(ModItems.KEY.get().getDefaultInstance());
                        output.add(ModItems.KEY_RING.get().getDefaultInstance());
                        output.add(ModItems.KEY_MOLD.get().getDefaultInstance());
                        output.add(ModItems.BAKED_KEY_MOLD.get().getDefaultInstance());
                        output.add(ModItems.LOCKPICK.get().getDefaultInstance());

                        
                        output.add(ModItems.WEIGHTED_ANCHOR_ITEM.get().getDefaultInstance());


                        output.add(ModItems.CELL_DOOR_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_BARS_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_SMOOTH_STONE_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_STONE_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_STONE_SLAB_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_STONE_STAIRS_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_STONE_CHISELED_ITEM.get().getDefaultInstance());
                        output.add(ModItems.REINFORCED_LAMP_ITEM.get().getDefaultInstance());


                        output.add(ModItems.PILLORY_ITEM.get().getDefaultInstance());
                        output.add(ModItems.GUILLOTINE_ITEM.get().getDefaultInstance());


                        output.add(ModItems.SAFE_ITEM.get().getDefaultInstance());
                        
                        
                        output.add(ModItems.INFORMATION_BOOKLET.get().getDefaultInstance());
                }
            };
}
