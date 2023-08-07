package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingDisassembleRecipe extends CustomRecipe {
    public KeyRingDisassembleRecipe(ResourceLocation idIn, CraftingBookCategory category) {
        super(idIn, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        ItemStack ringStack = null;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (!inv.getItem(i).isEmpty())
                if (inv.getItem(i).is(ModItems.KEY_RING.get()))
                    ringStack = inv.getItem(i);
                else
                    return false;

        }

        if (ringStack != null) {
            CompoundTag tag = ringStack.getOrCreateTag();
            if (tag != null && tag.getInt("Keys") > 0)
                return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {

        ItemStack ringStack = null;

        if (matches(inv, null)) {
              for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty())
                    if (inv.getItem(i).is(ModItems.KEY_RING.get()))
                        ringStack = inv.getItem(i);
            }

            if (ringStack != null) {
                CompoundTag tag = ringStack.getOrCreateTag();
                if (tag != null) {
                    ItemStack s = ModItems.KEY.get().getDefaultInstance();
                    s.setCount(tag.getInt("Keys"));
                    return s;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CuffedMod.KEY_RING_DISASSEMBLE.get();
    }

}
