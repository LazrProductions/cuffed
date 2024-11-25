package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class KeyMoldItem extends Item {
    
    public static final String TAG_COPIED_KEY = "CopiedKey";
    public static final String TAG_NAME = "Name";

    public KeyMoldItem(Properties properties) {
        super(properties);
    }


    public static ItemStack createFromKey(ItemStack keyStack) {
        ItemStack newMold = new ItemStack(ModItems.KEY_MOLD.get(), 1);
        
        if(!keyStack.getOrCreateTag().contains(KeyItem.TAG_ID))
            return newMold;

        CompoundTag tag = new CompoundTag();
        if(keyStack.getOrCreateTag().contains(KeyItem.TAG_ID))
            tag.putUUID(KeyItem.TAG_ID, keyStack.getOrCreateTag().getUUID(KeyItem.TAG_ID));
        if(keyStack.getOrCreateTag().contains("display"))
            tag.putString(TAG_NAME, keyStack.getOrCreateTag().getCompound("display").getString("Name"));
        newMold.getOrCreateTag().put(TAG_COPIED_KEY, tag);

        return newMold;
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        CompoundTag tag = stack.getOrCreateTag().getCompound(TAG_COPIED_KEY);
        if (tag != null) {
            pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.bound").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.unbound").withStyle(ChatFormatting.GRAY));
        }
    }
}
