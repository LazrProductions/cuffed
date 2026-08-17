package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BakedKeyMoldItem extends Item {
    
    public static final String TAG_QUALITY = "Quality";

    public BakedKeyMoldItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createFromRawMold(ItemStack oldMold) {
        ItemStack newMold = new ItemStack(ModItems.BAKED_KEY_MOLD.get(), 1);
        
        CompoundTag oldRoot = ItemTagUtils.getOrCreateTag(oldMold);
        CompoundTag oldTag = oldRoot.getCompound(KeyMoldItem.TAG_COPIED_KEY);
        CompoundTag tag = new CompoundTag();
        tag.putUUID(KeyItem.TAG_ID, oldTag.getUUID(KeyItem.TAG_ID));
        tag.putString(KeyMoldItem.TAG_NAME, oldTag.getString(KeyMoldItem.TAG_NAME));
        
        Random r = new Random();
        final int q = r.nextInt(1) + 4;
        ItemTagUtils.updateTag(newMold, newMoldTag -> {
            newMoldTag.put(KeyMoldItem.TAG_COPIED_KEY, tag);
            newMoldTag.putInt(TAG_QUALITY, q);
        });

        return newMold;
    }

    public static ItemStack createKeyFrom(ItemStack moldStack, int amount) {
        ItemStack newKey = new ItemStack(ModItems.KEY.get(), amount);

        CompoundTag moldRoot = ItemTagUtils.getTag(moldStack);
        if(moldRoot == null || !moldRoot.contains(KeyMoldItem.TAG_COPIED_KEY))
            return newKey;

        CompoundTag moldTag = moldRoot.getCompound(KeyMoldItem.TAG_COPIED_KEY);
        
        ItemTagUtils.updateTag(newKey, keyTag -> {
            keyTag.putUUID(KeyItem.TAG_ID, moldTag.getUUID(KeyItem.TAG_ID));
            if(moldTag.contains(KeyMoldItem.TAG_NAME)) {
                CompoundTag display = new CompoundTag();
                display.putString("Name", moldTag.getString(KeyMoldItem.TAG_NAME));
                keyTag.put("display", display);
            }
        });

        return newKey;
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);
        ItemTagUtils.updateTag(itemstack, tag -> tag.putInt(TAG_QUALITY, 5));
        return itemstack;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext pLevel, @Nonnull List<Component> pTooltipComponents,
            @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);

        int quality = ItemTagUtils.getOrCreateTag(stack).getInt(TAG_QUALITY);
        pTooltipComponents.add(Component.translatable("item.cuffed.baked_key_mold.description.quality_"+quality).withStyle(ChatFormatting.DARK_GRAY));
    }
}
