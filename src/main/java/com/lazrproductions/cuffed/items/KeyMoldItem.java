package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class KeyMoldItem extends Item {
    
    public static final String TAG_COPIED_KEY = "CopiedKey";
    public static final String TAG_NAME = "Name";

    public KeyMoldItem(Properties properties) {
        super(properties);
    }


    public static ItemStack createFromKey(ItemStack keyStack) {
        ItemStack newMold = new ItemStack(ModItems.KEY_MOLD.get(), 1);
        CompoundTag keyTag = ItemTagUtils.getTag(keyStack);
        if(keyTag == null || !keyTag.contains(KeyItem.TAG_ID))
            return newMold;

        CompoundTag tag = new CompoundTag();
        tag.putUUID(KeyItem.TAG_ID, keyTag.getUUID(KeyItem.TAG_ID));
        if(keyTag.contains("display"))
            tag.putString(TAG_NAME, keyTag.getCompound("display").getString("Name"));
        
        ItemTagUtils.updateTag(newMold, moldTag -> moldTag.put(TAG_COPIED_KEY, tag));

        return newMold;
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext pLevel, @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        CompoundTag rootTag = ItemTagUtils.getTag(stack);
        CompoundTag tag = rootTag != null ? rootTag.getCompound(TAG_COPIED_KEY) : null;
        if (tag != null && !tag.isEmpty()) {
            pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.bound").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.unbound").withStyle(ChatFormatting.GRAY));
        }
    }
}
