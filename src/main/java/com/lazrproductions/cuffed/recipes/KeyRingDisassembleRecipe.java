package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;
import com.lazrproductions.cuffed.items.KeyRingItem;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level level) {
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

    @SuppressWarnings("null")
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer inv, @Nonnull RegistryAccess access) {

        ItemStack ringStack = null;

        if (matches(inv, null)) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty())
                    if (inv.getItem(i).is(ModItems.KEY_RING.get()))
                        ringStack = inv.getItem(i);
            }

            if (ringStack != null) {
                CompoundTag tag = ringStack.getOrCreateTag();
                if (tag.contains(KeyRingItem.TAG_BOUND_LOCKS)) {
                    ListTag boundKeysTag = tag.getList(KeyRingItem.TAG_BOUND_LOCKS, 10);
                    if (boundKeysTag.size() > 0) {
                        ItemStack stack = ModItems.KEY.get().getDefaultInstance();
                        stack.setCount(1);

                        CompoundTag keyOnRingTag = boundKeysTag.getCompound(boundKeysTag.size() - 1);

                        KeyItem.setBoundId(stack, keyOnRingTag.getUUID(KeyItem.TAG_ID));
                        if(keyOnRingTag.contains(KeyMoldItem.TAG_NAME))
                        {
                            CompoundTag t = new CompoundTag();
                            t.putString("Name", keyOnRingTag.getString(KeyMoldItem.TAG_NAME));
                            stack.getOrCreateTag().put("display", t);
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
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer container) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);



        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack stack = container.getItem(i);
            CompoundTag tag = stack.getOrCreateTag();
            
            if (stack.is(ModItems.KEY_RING.get()) 
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

                nonnulllist.set(i, stack.copy());
            }
        }

        return nonnulllist;
    }
}
