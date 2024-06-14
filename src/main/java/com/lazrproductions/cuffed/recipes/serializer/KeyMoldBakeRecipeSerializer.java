package com.lazrproductions.cuffed.recipes.serializer;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.recipes.KeyMoldBakeRecipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class KeyMoldBakeRecipeSerializer<T extends KeyMoldBakeRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> factory;

    public KeyMoldBakeRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        JsonElement ingredientElement = GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(ingredientElement);
        ItemStack result = ModItems.KEY_MOLD.get().getDefaultInstance();
        float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
        int cookingTime = GsonHelper.getAsInt(json, "cookingtime", 200);
        return factory.create(recipeId, group, ingredient, result, experience, cookingTime);
    }

    @Override
    public T fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStack result = buffer.readItem();
        float experience = buffer.readFloat();
        int cookingTime = buffer.readVarInt();
        return factory.create(recipeId, group, ingredient, result, experience, cookingTime);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.getIngredients().get(0).toNetwork(buffer);
        buffer.writeItem(ModItems.KEY_MOLD.get().getDefaultInstance());
        buffer.writeFloat(recipe.getExperience());
        buffer.writeVarInt(recipe.getCookingTime());
    }

    public interface RecipeFactory<T extends KeyMoldBakeRecipe> {
        T create(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}