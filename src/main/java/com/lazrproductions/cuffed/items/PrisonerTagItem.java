package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.lazrproductions.cuffed.init.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PrisonerTagItem extends Item {
    public PrisonerTagItem(Properties p) {
        super(p);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
      if (entity instanceof Player other) {
         if (!player.level().isClientSide() ) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(other);
            boolean flag = !CuffedMod.CONFIG.handcuffSettings.requireHandcuffedToNickname;
            boolean flag2 = !flag && cap.isHandcuffed();
            if(flag || flag2)
               if(stack.hasCustomHoverName())
                  cap.server_setNickname(stack.getHoverName());
               else
                  cap.server_setNickname(null); // reset the nickname if there is no custom name on this item
            else
                player.displayClientMessage(Component.literal("This player needs to be handcuffed to be nicknamed."), true);

            
            player.awardStat(Stats.ITEM_USED.get(ModItems.PRISONER_TAG.get()));    
            stack.shrink(1);
         }

         return InteractionResult.sidedSuccess(player.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }
}
