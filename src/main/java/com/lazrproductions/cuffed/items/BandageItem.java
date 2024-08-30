package com.lazrproductions.cuffed.items;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.effect.WoundedEffect;
import com.lazrproductions.cuffed.init.ModEffects;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BandageItem extends Item {

    public BandageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if(player.isCrouching()) {
            if(player.hasEffect(ModEffects.WOUNDED_EFFECT.get())) {
                WoundedEffect.treatEntity(player);
                player.getItemInHand(hand).shrink(1);
                return InteractionResultHolder.consume(player.getItemInHand(hand));
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
            @Nonnull LivingEntity entity,
            @Nonnull InteractionHand hand) {
        if(entity.hasEffect(ModEffects.WOUNDED_EFFECT.get())) {
            WoundedEffect.treatEntity(entity);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        
        return super.interactLivingEntity(stack, player, entity, hand);
    }
}
