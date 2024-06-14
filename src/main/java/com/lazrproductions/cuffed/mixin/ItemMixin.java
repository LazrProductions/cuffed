package com.lazrproductions.cuffed.mixin;

import javax.annotation.Nullable;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.init.ModTags;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(at = @At("TAIL"), method = "appendHoverText")
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag, CallbackInfo callback) {
        Block b = Block.byItem(stack.getItem());
        if(b.defaultBlockState().is(ModTags.Blocks.REINFORCED_BLOCKS))
            components.add(Component.translatable("info.cuffed.reinforced_item").withStyle(ChatFormatting.GRAY));
   }
}
