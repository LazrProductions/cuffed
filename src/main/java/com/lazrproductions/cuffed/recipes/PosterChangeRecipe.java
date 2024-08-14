package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.PosterBlockItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PosterChangeRecipe extends CustomRecipe {
    public PosterChangeRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level level) {
        return isGridValid(inv);
    }

    @SuppressWarnings("null")
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv) {

        if (matches(inv, null)) {
            ItemStack posterStack = getPosterFromGrid(inv);
            return PosterBlockItem.newItemFromType(PosterBlockItem.getPosterType(posterStack).next());
        }

        return ItemStack.EMPTY;
    }

    public ArrayList<ItemStack> getAllValidItemsInGrid(@Nonnull CraftingContainer inv) {
        ArrayList<ItemStack> validInGrid = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack checkingStack = inv.getItem(i);
            if (!checkingStack.isEmpty())
                if (checkingStack.is(ModItems.POSTER_ITEM.get()))
                    validInGrid.add(checkingStack);
        }

        return validInGrid;
    }

    public ItemStack getPosterFromGrid(@Nonnull CraftingContainer inv) {
        ArrayList<ItemStack> moldsInGrid = getAllValidItemsInGrid(inv);
        return moldsInGrid.get(0);
    }

    public boolean isGridValid(@Nonnull CraftingContainer inv) {
        int numOfPosters = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack checkingStack = inv.getItem(i);
            if (!checkingStack.isEmpty())
                if (checkingStack.is(ModItems.POSTER_ITEM.get()))
                    numOfPosters++;
                else
                    return false;
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
