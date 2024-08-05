package com.lazrproductions.cuffed.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.blocks.PosterBlock;
import com.lazrproductions.cuffed.blocks.base.PosterType;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PosterBlockItem extends BlockItem {
    public static final String POSTER_TYPE_TAG = "Poster"; 
    
    public PosterBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
     }

    @Override
    @Nullable
    protected BlockState getPlacementState(@Nonnull BlockPlaceContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if(state != null && state.is(ModBlocks.POSTER.get()))
            state = state.setValue(PosterBlock.POSTER_TYPE, getPosterType(ctx.getItemInHand()));
        return state;
    }

    public static PosterType getPosterType(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(POSTER_TYPE_TAG))
            return PosterType.fromString(tag.getString(POSTER_TYPE_TAG));
        return PosterType.NONE;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines,
            @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, lines, tooltipFlag);

        lines.add(Component.translatable(getDescriptionId() +"."+getPosterType(stack).getSerializedName()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    public static ItemStack newItemFromType(PosterType type) {
        ItemStack stack = ModItems.POSTER_ITEM.get().getDefaultInstance().copyWithCount(1);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(POSTER_TYPE_TAG, type.getSerializedName());
        return stack;
    }
}
