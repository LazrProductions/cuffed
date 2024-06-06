package com.lazrproductions.cuffed.items;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.entity.WeightedAnchorEntity;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;

public class WeightedAnchorItem extends Item {

    public WeightedAnchorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext ctx) {
        if(!ctx.getLevel().isClientSide() && ctx.getHand() == InteractionHand.MAIN_HAND && ctx.getItemInHand().is(ModItems.WEIGHTED_ANCHOR_ITEM.get())) {
            WeightedAnchorEntity entity = WeightedAnchorEntity.createFromItem(ctx.getLevel(), ctx.getItemInHand(), ctx.getClickedPos().offset(ctx.getClickedFace().getNormal()));
            Player player = ctx.getPlayer();
            if(player != null)
                player.awardStat(Stats.ITEM_USED.get(ModItems.WEIGHTED_ANCHOR_ITEM.get()));
            ctx.getLevel().addFreshEntity(entity);
            ctx.getItemInHand().shrink(1);
        }

        return InteractionResult.PASS;
    }

    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == ModEnchantments.BUOYANT.get())
            return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return true;
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }
}
