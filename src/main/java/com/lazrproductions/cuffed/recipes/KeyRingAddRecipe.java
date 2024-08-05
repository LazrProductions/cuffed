package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.lazrslib.common.tag.TagUtilities;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingAddRecipe extends CustomRecipe {
    public KeyRingAddRecipe(ResourceLocation idIn, CraftingBookCategory category) {
        super(idIn, category);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level level) {
        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStack = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (!inv.getItem(i).isEmpty())
                if (inv.getItem(i).is(ModItems.KEY.get()) || inv.getItem(i).is(ModItems.KEY_RING.get())) {

                    ItemStack stack = inv.getItem(i);
                    Item item = stack.getItem();
                    if (ringStack == null && item == ModItems.KEY_RING.get()) {
                        ringStack = stack;
                    }

                    if (item == ModItems.KEY.get()) {
                        keyStack.add(stack);
                    }

                } else
                    return false;

        }

        if (ringStack != null)
            if ((ringStack.getOrCreateTag().getInt("Keys")) + keyStack.size() > CuffedMod.SERVER_CONFIG.MAX_KEYS_PER_RING.get())
                return false;

        if (ringStack == null || keyStack.size() == 0)
            return false;

        return ringStack != null && keyStack.size() > 0;
    }

    @SuppressWarnings("null")
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv, @Nonnull RegistryAccess access) {

        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStacks = new ArrayList<ItemStack>(0);

        if (matches(inv, null)) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                Item item = stack.getItem();

                if (ringStack == null) {
                    if (item == ModItems.KEY_RING.get()) {
                        ringStack = stack;
                    }
                }

                if (item == ModItems.KEY.get()) {
                    keyStacks.add(stack);
                }
            }

            if (ringStack != null) {
                ItemStack newStack = ringStack.copy();
                newStack.setCount(1);
                int keys = 2;
                if (newStack.getTag() != null)
                    keys = newStack.getOrCreateTag().getInt("Keys");

                for (ItemStack stack : keyStacks) {
                    if(stack.getOrCreateTag().contains(KeyItem.TAG_BOUND_BLOCK)) {
                        KeyRingItem.addBoundBlock(newStack, TagUtilities.fromTag(stack.getOrCreateTag().getCompound(KeyItem.TAG_BOUND_BLOCK)));
                    }
                }
                
                newStack.getOrCreateTag().putInt("Keys", keys + keyStacks.size());

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
