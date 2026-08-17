package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BakedKeyMoldCopyRecipe extends CustomRecipe {
    public BakedKeyMoldCopyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return isGridValid(input);
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        if (matches(input, null)) {
            ItemStack moldStack = getMoldFromGrid(input);
            return ItemTagUtils.getOrCreateTag(moldStack).contains(KeyMoldItem.TAG_COPIED_KEY)
                    ? BakedKeyMoldItem.createKeyFrom(moldStack, 1)
                    : ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getMoldFromGrid(@Nonnull CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty() && checkingStack.is(ModItems.BAKED_KEY_MOLD.get())) {
                return checkingStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isGridValid(@Nonnull CraftingInput input) {
        int numofMolds = 0;
        int numOfIngots = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack checkingStack = input.getItem(i);
            if (!checkingStack.isEmpty()) {
                if (checkingStack.is(ModItems.BAKED_KEY_MOLD.get())
                        && ItemTagUtils.getOrCreateTag(checkingStack).contains(KeyMoldItem.TAG_COPIED_KEY)) {
                    numofMolds++;
                } else if (checkingStack.is(Items.IRON_INGOT)) {
                    numOfIngots++;
                } else {
                    return false;
                }
            }
        }

        // there must be only 1 ingot and only 1 mold
        return numOfIngots == 1 && numofMolds == 1;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BAKED_KEY_MOLD_COPY.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);
    
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = input.getItem(i);
            if (!item.isEmpty() && item.is(ModItems.BAKED_KEY_MOLD.get())) {
                int oldQuality = ItemTagUtils.getOrCreateTag(item).getInt(BakedKeyMoldItem.TAG_QUALITY);
                if(oldQuality > 1) {
                    ItemStack copy = item.copy();
                    ItemTagUtils.updateTag(copy, tag -> tag.putInt(BakedKeyMoldItem.TAG_QUALITY, oldQuality - 1));
                    nonnulllist.set(i, copy);
                }
            }
        }
        return nonnulllist;
    }
}
