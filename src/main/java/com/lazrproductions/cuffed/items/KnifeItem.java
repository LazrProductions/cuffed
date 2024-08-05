package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.effect.WoundedEffect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class KnifeItem extends Item {
    public KnifeItem(Properties p) {
        super(p);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if(entity instanceof LivingEntity living) {
            WoundedEffect.woundEntity(living, 20);
        }

        return false;
    }
}
