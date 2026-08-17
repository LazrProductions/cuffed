package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingDisassembleRecipe extends CustomRecipe {
    public KeyRingDisassembleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        ItemStack ringStack = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.KEY_RING.get())) {
                    ringStack = stack;
                } else {
                    return false;
                }
            }
        }

        if (ringStack != null) {
            CompoundTag tag = ItemTagUtils.getTag(ringStack);
            if (tag != null && tag.getInt("Keys") > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        ItemStack ringStack = null;

        if (matches(input, null)) {
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (!stack.isEmpty()) {
                    if (stack.is(ModItems.KEY_RING.get())) {
                        ringStack = stack;
                    }
                }
            }

            if (ringStack != null) {
                CompoundTag tag = ItemTagUtils.getOrCreateTag(ringStack);
                if (tag.contains(KeyRingItem.TAG_BOUND_LOCKS)) {
                    ListTag boundKeysTag = tag.getList(KeyRingItem.TAG_BOUND_LOCKS, 10);
                    if (boundKeysTag.size() > 0) {
                        ItemStack stack = ModItems.KEY.get().getDefaultInstance();
                        stack.setCount(1);

                        CompoundTag keyOnRingTag = boundKeysTag.getCompound(boundKeysTag.size() - 1);

                        KeyItem.setBoundId(stack, keyOnRingTag.getUUID(KeyItem.TAG_ID));
                        if(keyOnRingTag.contains(KeyMoldItem.TAG_NAME)) {
                            CompoundTag t = new CompoundTag();
                            t.putString("Name", keyOnRingTag.getString(KeyMoldItem.TAG_NAME));
                            ItemTagUtils.updateTag(stack, keyTag -> keyTag.put("display", t));
                        }
                        return stack;
                    }
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
        return ModRecipes.KEY_RING_DISASSEMBLE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack stackCopy = stack.copy();
                CompoundTag tag = ItemTagUtils.getOrCreateTag(stackCopy);
                
                if (stackCopy.is(ModItems.KEY_RING.get()) 
                        && tag.contains(KeyRingItem.TAG_KEYS) 
                        && tag.getInt(KeyRingItem.TAG_KEYS) > 1) {
                    int numOfKeys = tag.getInt(KeyRingItem.TAG_KEYS);
                    if (tag.contains(KeyRingItem.TAG_BOUND_LOCKS)) {
                        ListTag boundKeysTag = tag.getList(KeyRingItem.TAG_BOUND_LOCKS, 10);
                        if(boundKeysTag.size() > 0) {
                            boundKeysTag.remove(boundKeysTag.size() - 1);
                            tag.put(KeyRingItem.TAG_BOUND_LOCKS, boundKeysTag);
                            tag.putInt(KeyRingItem.TAG_KEYS, numOfKeys - 1);
                        }
                    }
                    ItemTagUtils.setTag(stackCopy, tag);
                    nonnulllist.set(i, stackCopy);
                }
            }
        }

        return nonnulllist;
    }
}
