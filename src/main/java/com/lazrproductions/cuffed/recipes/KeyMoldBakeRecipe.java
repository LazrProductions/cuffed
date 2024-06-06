package com.lazrproductions.cuffed.recipes;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.items.KeyMoldItem;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class KeyMoldBakeRecipe extends SmeltingRecipe {


    public KeyMoldBakeRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(id, group, CookingBookCategory.MISC, ingredient, result, experience, cookingTime);
    }

    @Override
    public boolean matches(@Nonnull Container inv, @Nonnull Level worldIn) {
        return inv.getItem(0).is(ModItems.KEY_MOLD.get()) && inv.getItem(0).getOrCreateTag().contains(KeyMoldItem.TAG_COPIED_KEY);
    }

    @Override
    public ItemStack assemble(@Nonnull Container inv, @Nonnull RegistryAccess access) {
        return BakedKeyMoldItem.createFromRawMold(inv.getItem(0));
    }



    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.KEY_MOLD_BAKE.get();
    }
}
