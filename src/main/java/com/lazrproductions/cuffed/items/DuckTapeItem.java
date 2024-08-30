package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


public class DuckTapeItem extends AbstractRestraintItem {

    public DuckTapeItem(Properties p) {
        super(p);
    }
    
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> components,
            @Nonnull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("info.cuffed.restraint_type.head").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("info.cuffed.restraint_type.arm").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("info.cuffed.restraint_type.leg").withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, level, components, tooltipFlag);
    }
}
