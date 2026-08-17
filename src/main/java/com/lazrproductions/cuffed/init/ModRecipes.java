package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.recipes.BakedKeyMoldCopyRecipe;
import com.lazrproductions.cuffed.recipes.KeyMoldBakeRecipe;
import com.lazrproductions.cuffed.recipes.KeyMoldCopyRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingAddRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingCreateRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingDisassembleRecipe;
import com.lazrproductions.cuffed.recipes.PosterChangeRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
                        .create(Registries.RECIPE_SERIALIZER, CuffedMod.MODID);

        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KeyRingCreateRecipe>> KEY_RING_CREATE = RECIPE_SERIALIZERS
                        .register("key_ring_create", () -> new SimpleCraftingRecipeSerializer<>(KeyRingCreateRecipe::new));
        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KeyRingAddRecipe>> KEY_RING_ADD = RECIPE_SERIALIZERS
                        .register("key_ring_add", () -> new SimpleCraftingRecipeSerializer<>(KeyRingAddRecipe::new));
        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KeyRingDisassembleRecipe>> KEY_RING_DISASSEMBLE = RECIPE_SERIALIZERS
                        .register("key_ring_disassemble", () -> new SimpleCraftingRecipeSerializer<>(KeyRingDisassembleRecipe::new));


        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KeyMoldCopyRecipe>> KEY_MOLD_COPY = RECIPE_SERIALIZERS
                        .register("key_mold_copy", () -> new SimpleCraftingRecipeSerializer<>(KeyMoldCopyRecipe::new));
        public static final DeferredHolder<RecipeSerializer<?>, net.minecraft.world.item.crafting.SimpleCookingSerializer<KeyMoldBakeRecipe>> KEY_MOLD_BAKE = RECIPE_SERIALIZERS
                        .register("key_mold_bake", () -> new net.minecraft.world.item.crafting.SimpleCookingSerializer<>(KeyMoldBakeRecipe::new, 200));

        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BakedKeyMoldCopyRecipe>> BAKED_KEY_MOLD_COPY = RECIPE_SERIALIZERS
                        .register("baked_key_mold_copy", () -> new SimpleCraftingRecipeSerializer<>(BakedKeyMoldCopyRecipe::new));
                        
        public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PosterChangeRecipe>> POSTER_CHANGE = RECIPE_SERIALIZERS
                        .register("poster_change", () -> new SimpleCraftingRecipeSerializer<>(PosterChangeRecipe::new));

        public static void register(IEventBus bus) {
                RECIPE_SERIALIZERS.register(bus);
        }
}
