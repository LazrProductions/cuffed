package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.recipes.KeyRingAddRecipe;
import com.lazrproductions.cuffed.recipes.KeyRingDisassembleRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, CuffedMod.MODID);

    public static final RegistryObject<RecipeSerializer<KeyRingAddRecipe>> KEY_RING_ADD = RECIPE_SERIALIZERS
            .register("key_ring_add", () -> new SimpleCraftingRecipeSerializer<>(KeyRingAddRecipe::new));
    public static final RegistryObject<RecipeSerializer<KeyRingDisassembleRecipe>> KEY_RING_DISASSEMBLE = RECIPE_SERIALIZERS
            .register("key_ring_disassemble",
                    () -> new SimpleCraftingRecipeSerializer<>(KeyRingDisassembleRecipe::new));

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
