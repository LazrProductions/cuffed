package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    public void onAppendHoverText(ItemStack stack, Level p_150750_, List<Component> lore, TooltipFlag p_150752_, CallbackInfo callback) {
        if(BundleItem.getFullnessDisplay(stack) <= 0)
            lore.add(Component.translatable("info.cuffed.restraint_type.head").withStyle(ChatFormatting.GRAY));
    }
}
