package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BakedKeyMoldCopyRecipe extends CustomRecipe {
    public BakedKeyMoldCopyRecipe(ResourceLocation idIn) {
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
            ItemStack moldStack = getMoldFromGrid(inv);
            return moldStack.getOrCreateTag().contains(KeyMoldItem.TAG_COPIED_KEY)
                    ? BakedKeyMoldItem.createKeyFrom(moldStack, 1)
                    : ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    public ArrayList<ItemStack> getAllMoldsInGrid(@Nonnull CraftingContainer inv) {
        ArrayList<ItemStack> moldsInGrid = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack checkingStack = inv.getItem(i);
            if (!checkingStack.isEmpty())
                if (checkingStack.is(ModItems.BAKED_KEY_MOLD.get()) || checkingStack.is(Items.IRON_INGOT)) {
                    Item item = checkingStack.getItem();
                    if (item == ModItems.BAKED_KEY_MOLD.get()
                            && checkingStack.getOrCreateTag().contains(KeyMoldItem.TAG_COPIED_KEY))
                        moldsInGrid.add(checkingStack);
                }
        }

        return moldsInGrid;
    }

    public ArrayList<ItemStack> getAllValidItemsInGrid(@Nonnull CraftingContainer inv) {
        ArrayList<ItemStack> validInGrid = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack checkingStack = inv.getItem(i);
            if (!checkingStack.isEmpty())
                if (checkingStack.is(ModItems.BAKED_KEY_MOLD.get()) || checkingStack.is(Items.IRON_INGOT))
                    validInGrid.add(checkingStack);
        }

        return validInGrid;
    }

    public ItemStack getMoldFromGrid(@Nonnull CraftingContainer inv) {
        ArrayList<ItemStack> moldsInGrid = getAllMoldsInGrid(inv);
        return moldsInGrid.get(0);
    }

    public boolean isGridValid(@Nonnull CraftingContainer inv) {
        int numofMolds = 0;
        int numOfIngots = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack checkingStack = inv.getItem(i);
            if (!checkingStack.isEmpty())
                if (checkingStack.is(ModItems.BAKED_KEY_MOLD.get())
                        && checkingStack.getOrCreateTag().contains(KeyMoldItem.TAG_COPIED_KEY))
                    numofMolds++;
                else if (checkingStack.is(Items.IRON_INGOT))
                    numOfIngots++;
                else
                    return false;
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
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer container) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = container.getItem(i);
            if (item.is(ModItems.BAKED_KEY_MOLD.get())) {
                int oldQuality = item.getOrCreateTag().getInt(BakedKeyMoldItem.TAG_QUALITY);
                if(oldQuality > 1) {
                    item.getOrCreateTag().putInt("Quality", oldQuality - 1);
                    nonnulllist.set(i, item.copy());
                }
            }
        }
        return nonnulllist;
    }
}
