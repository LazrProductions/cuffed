package com.lazrproductions.cuffed.recipes;

import java.util.ArrayList;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CellKeyRingAddRecipe extends CustomRecipe {
    public CellKeyRingAddRecipe(ResourceLocation idIn, CraftingBookCategory category) {
        super(idIn, category);
    }

    
    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStack = new ArrayList<ItemStack>(0);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (ringStack == null) {
                ItemStack stack = inv.getItem(i);
                Item item = stack.getItem();
                if (item == ModItems.CELL_KEY_RING.get()) {
                    ringStack = stack;
                }
            } else {
                ItemStack stack = inv.getItem(i);
                Item item = stack.getItem();
                if (item == ModItems.CELL_KEY.get()) {
                    keyStack.add(stack);
                }
            }
        }

        if(ringStack!=null)
            if((ringStack.getOrCreateTag().getInt("Keys"))+keyStack.size()>16) //TODO: Config value for max keys on ring
                return false;

        if(ringStack==null||keyStack.size()==0)
            return false;

        return ringStack != null && keyStack.size()>0;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        
        ItemStack ringStack = null;
        ArrayList<ItemStack> keyStacks = new ArrayList<ItemStack>(0); 

        if (matches(inv, null)) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                Item item = stack.getItem();
                
                if (ringStack == null) {
                    if (item == ModItems.CELL_KEY_RING.get()) {
                        ringStack = stack;
                    }
                }

                if(item == ModItems.CELL_KEY.get()){
                    keyStacks.add(stack);
                }
            }

            if(ringStack!=null)
            {
                ItemStack s = ringStack.copy();
                s.setCount(1);
                int keys = 2;
                if (s.getTag() != null) {
                    keys = s.getOrCreateTag().getInt("Keys");
                }
                s.getOrCreateTag().putInt("Keys", keys + keyStacks.size());

            return s;
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
      return CuffedMod.CELL_KEY_RING_ADD.get();
    }
    
}
