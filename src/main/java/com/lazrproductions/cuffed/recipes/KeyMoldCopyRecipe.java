package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyMoldCopyRecipe extends CustomRecipe {
    public KeyMoldCopyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return isGridValid(input);
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        if (matches(input, null)) {
            ItemStack keyStack = getKeyStackFromGrid(input);
            return ItemTagUtils.getOrCreateTag(keyStack).contains(KeyItem.TAG_ID) ? KeyMoldItem.createFromKey(keyStack) : Items.CLAY_BALL.getDefaultInstance();
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getKeyStackFromGrid(@Nonnull CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty() && checkingStack.is(ModItems.KEY.get())) {
                return checkingStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isGridValid(@Nonnull CraftingInput input) {
        int numOfClayOrMolds = 0;
        int numOfKeys = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty()) {
                if (checkingStack.is(ModItems.KEY.get()) && ItemTagUtils.getOrCreateTag(checkingStack).contains(KeyItem.TAG_ID)) {
                    numOfKeys++;
                } else if(checkingStack.is(ModItems.KEY_MOLD.get()) || checkingStack.is(Items.CLAY_BALL)) {
                    numOfClayOrMolds++;
                } else {
                    return false;
                }
            }
        }

        // there must be only 1 key and only 1 clay/molds
        return numOfKeys == 1 && numOfClayOrMolds == 1;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.KEY_MOLD_COPY.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);
    
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = input.getItem(i);
            if (!item.isEmpty() && item.is(ModItems.KEY.get())) {
                nonnulllist.set(i, item.copy());
            }
        }
    
        return nonnulllist;
    }
}
