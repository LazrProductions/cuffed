package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.lazrslib.common.tag.TagUtilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingCreateRecipe extends CustomRecipe {
    public KeyRingCreateRecipe(ResourceLocation idIn) {
        super(idIn);
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
    public ItemStack assemble(@Nonnull CraftingContainer inv) {

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
                if(stack.getOrCreateTag().contains(KeyItem.TAG_BOUND_BLOCK)) {
                    KeyRingItem.addBoundBlock(newStack, TagUtilities.fromTag(stack.getOrCreateTag().getCompound(KeyItem.TAG_BOUND_BLOCK)));
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

    public static class Serializer implements RecipeSerializer<KeyRingCreateRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(CuffedMod.MODID, "key_ring_create");

        @Override
        public KeyRingCreateRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            return new KeyRingCreateRecipe(pRecipeId);
        }

        @Override
        public @Nullable KeyRingCreateRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
            // NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            // for (int i = 0; i < inputs.size(); i++) {
            //     inputs.set(i, Ingredient.fromNetwork(buf));
            // }

            // ItemStack output = buf.readItem();
            return new KeyRingCreateRecipe(id);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull KeyRingCreateRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }
    }
}
