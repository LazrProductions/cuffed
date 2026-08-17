package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingAddRecipe extends CustomRecipe {
    public KeyRingAddRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStack = new ArrayList<ItemStack>(0);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.KEY.get()) || stack.is(ModItems.KEY_RING.get())) {
                    if (ringStack == null && stack.is(ModItems.KEY_RING.get())) {
                        ringStack = stack;
                    }
                    if (stack.is(ModItems.KEY.get())) {
                        keyStack.add(stack);
                    }
                } else {
                    return false;
                }
            }
        }

        if (ringStack != null) {
            if ((ItemTagUtils.getOrCreateTag(ringStack).getInt("Keys")) + keyStack.size() > CuffedMod.SERVER_CONFIG.MAX_KEYS_PER_RING.get()) {
                return false;
            }
        }

        if (ringStack == null || keyStack.size() == 0) {
            return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStacks = new ArrayList<ItemStack>(0);

        if (matches(input, null)) {
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (!stack.isEmpty()) {
                    if (stack.is(ModItems.KEY_RING.get()) && ringStack == null) {
                        ringStack = stack;
                    }
                    if (stack.is(ModItems.KEY.get())) {
                        keyStacks.add(stack);
                    }
                }
            }

            if (ringStack != null) {
                ItemStack newStack = ringStack.copy();
                newStack.setCount(1);
                int keys = 0;
                var tag = ItemTagUtils.getTag(newStack);
                if (tag != null) {
                    keys = tag.getInt("Keys");
                }

                for (ItemStack stack : keyStacks) {
                    if(ItemTagUtils.getOrCreateTag(stack).contains(KeyItem.TAG_ID)) {
                        KeyRingItem.addKey(newStack, stack);
                    }
                }
                
                final int finalKeys = keys;
                ItemTagUtils.updateTag(newStack, t -> t.putInt("Keys", finalKeys + keyStacks.size()));

                return newStack;
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
        return ModRecipes.KEY_RING_ADD.get();
    }
}
