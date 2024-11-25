package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingCreateRecipe extends CustomRecipe {
    public KeyRingCreateRecipe(ResourceLocation idIn, CraftingBookCategory category) {
        super(idIn, category);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level level) {
        ArrayList<ItemStack> keyStack = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (!inv.getItem(i).isEmpty())
                if (inv.getItem(i).is(ModItems.KEY.get())) {
                    ItemStack stack = inv.getItem(i);
                    Item item = stack.getItem();
                    if (item == ModItems.KEY.get()) {
                        keyStack.add(stack);
                    }

                } else
                    return false;

        }

        return keyStack.size() > 0;
    }

    @SuppressWarnings("null")
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv, @Nonnull RegistryAccess access) {

        ArrayList<ItemStack> keyStacks = new ArrayList<ItemStack>(0);

        if (matches(inv, null)) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                Item item = stack.getItem();

                if (item == ModItems.KEY.get()) {
                    keyStacks.add(stack);
                }
            }


            ItemStack newStack = new ItemStack(ModItems.KEY_RING.get());
            newStack.setCount(1);
            //int keys = 2;
                
            for (ItemStack stack : keyStacks) {
                if(stack.getOrCreateTag().contains(KeyItem.TAG_ID)) {
                    KeyRingItem.addKey(newStack, stack);
                }
            }
                        
            newStack.getOrCreateTag().putInt("Keys", keyStacks.size());

            return newStack;
            
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
