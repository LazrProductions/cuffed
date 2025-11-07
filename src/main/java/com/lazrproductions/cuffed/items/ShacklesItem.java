package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ShacklesItem extends AbstractRestraintItem {

    public ShacklesItem(Properties p) {
        super(p);
    } 

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CuffedMod.SERVER_CONFIG.RESTRAINT_DURABILITY_SHACKLES.get();
    }
    
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> components,
            @Nonnull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("info.cuffed.restraint_type.arm").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("info.cuffed.restraint_type.leg").withStyle(ChatFormatting.GRAY));
        
        components.add(Component.empty());
        components.add(
            Component.translatable("info.cuffed.restraint_type.my_key")
            .withStyle(ChatFormatting.GRAY)
            .append(" ")
            .append(
                ModItems.SHACKLES_KEY.get().getDefaultInstance().getHoverName().copy()
                .withStyle(ChatFormatting.WHITE)));
                
        super.appendHoverText(stack, level, components, tooltipFlag);
    }
}
