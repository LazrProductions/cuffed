package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.Random;

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

public class BakedKeyMoldItem extends Item {
    
    public static final String TAG_QUALITY = "Quality";

    public BakedKeyMoldItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createFromRawMold(ItemStack oldMold) {
        ItemStack newMold = new ItemStack(ModItems.BAKED_KEY_MOLD.get(), 1);
        
        CompoundTag oldTag = oldMold.getOrCreateTagElement(KeyMoldItem.TAG_COPIED_KEY);
        CompoundTag tag = new CompoundTag();
        tag.putUUID(KeyItem.TAG_ID, oldTag.getUUID(KeyItem.TAG_ID));
        tag.putString(KeyMoldItem.TAG_NAME, oldTag.getString(KeyMoldItem.TAG_NAME));
        
        newMold.getOrCreateTag().put(KeyMoldItem.TAG_COPIED_KEY, tag);
        Random r = new Random();
        newMold.getOrCreateTag().putInt(TAG_QUALITY, r.nextInt(1) + 4);

        return newMold;
    }

    public static ItemStack createKeyFrom(ItemStack moldStack, int amount) {
        ItemStack newKey = new ItemStack(ModItems.KEY.get(), amount);

        if(!moldStack.getOrCreateTag().contains(KeyMoldItem.TAG_COPIED_KEY))
            return newKey;

        CompoundTag moldTag = moldStack.getOrCreateTag().getCompound(KeyMoldItem.TAG_COPIED_KEY);
        newKey.getOrCreateTag().putUUID(KeyItem.TAG_ID, moldTag.getUUID(KeyItem.TAG_ID));

        if(moldTag.contains(KeyMoldItem.TAG_NAME))
            newKey.getOrCreateTagElement("display").putString("Name", moldTag.getString(KeyMoldItem.TAG_NAME));

        return newKey;
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        itemstack.getOrCreateTag().putInt(TAG_QUALITY, 5);
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        int quality = stack.getOrCreateTag().getInt(TAG_QUALITY);
        pTooltipComponents.add(Component.translatable("item.cuffed.baked_key_mold.description.quality_"+quality).withStyle(ChatFormatting.DARK_GRAY));
    }
}
