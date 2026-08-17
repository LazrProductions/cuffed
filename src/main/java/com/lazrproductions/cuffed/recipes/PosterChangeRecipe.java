package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.PosterBlockItem;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PosterChangeRecipe extends CustomRecipe {
    public PosterChangeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return isGridValid(input);
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        if (matches(input, null)) {
            ItemStack posterStack = getPosterFromGrid(input);
            return PosterBlockItem.newItemFromType(PosterBlockItem.getPosterType(posterStack).next());
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getPosterFromGrid(@Nonnull CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty() && checkingStack.is(ModItems.POSTER_ITEM.get())) {
                return checkingStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isGridValid(@Nonnull CraftingInput input) {
        int numOfPosters = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty()) {
                if (checkingStack.is(ModItems.POSTER_ITEM.get())) {
                    numOfPosters++;
                } else {
                    return false;
                }
            }
        }

        // there must be only 1 poster
        return numOfPosters == 1;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POSTER_CHANGE.get();
    }
}
