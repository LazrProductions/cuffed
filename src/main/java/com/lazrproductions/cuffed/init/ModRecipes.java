package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.recipes.BakedKeyMoldCopyRecipe;
import com.lazrproductions.cuffed.recipes.KeyMoldBakeRecipe;
import com.lazrproductions.cuffed.recipes.KeyMoldCopyRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingAddRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingCreateRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingDisassembleRecipe;
import com.lazrproductions.cuffed.recipes.serializer.KeyMoldBakeRecipeSerializer;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
                        .create(ForgeRegistries.RECIPE_SERIALIZERS, CuffedMod.MODID);

        public static final RegistryObject<RecipeSerializer<KeyRingCreateRecipe>> KEY_RING_CREATE = RECIPE_SERIALIZERS
                        .register("key_ring_create", () -> new SimpleCraftingRecipeSerializer<>(KeyRingCreateRecipe::new));
        public static final RegistryObject<RecipeSerializer<KeyRingAddRecipe>> KEY_RING_ADD = RECIPE_SERIALIZERS
                        .register("key_ring_add", () -> new SimpleCraftingRecipeSerializer<>(KeyRingAddRecipe::new));
        public static final RegistryObject<RecipeSerializer<KeyRingDisassembleRecipe>> KEY_RING_DISASSEMBLE = RECIPE_SERIALIZERS
                        .register("key_ring_disassemble", () -> new SimpleCraftingRecipeSerializer<>(KeyRingDisassembleRecipe::new));


        public static final RegistryObject<RecipeSerializer<KeyMoldCopyRecipe>> KEY_MOLD_COPY = RECIPE_SERIALIZERS
                        .register("key_mold_copy", () -> new SimpleCraftingRecipeSerializer<>(KeyMoldCopyRecipe::new));
        public static final RegistryObject<RecipeSerializer<KeyMoldBakeRecipe>> KEY_MOLD_BAKE = RECIPE_SERIALIZERS
                        .register("key_mold_bake", () -> new KeyMoldBakeRecipeSerializer<>(KeyMoldBakeRecipe::new));

        public static final RegistryObject<RecipeSerializer<BakedKeyMoldCopyRecipe>> BAKED_KEY_MOLD_COPY = RECIPE_SERIALIZERS
                        .register("baked_key_mold_copy", () -> new SimpleCraftingRecipeSerializer<>(BakedKeyMoldCopyRecipe::new));

        public static void register(IEventBus bus) {
                RECIPE_SERIALIZERS.register(bus);
        }
}
