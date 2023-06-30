package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.inventory.tooltip.PossessionsBoxTooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class PossessionsBox extends Item {

   public PossessionsBox(Properties properties) {
      super(properties);
   }

   @Override
   public boolean overrideStackedOnOther(ItemStack thisStack, Slot slot, ClickAction click, Player player) {
      if (thisStack.getCount() != 1 || click != ClickAction.SECONDARY) {
         return false;
      } else {
         ItemStack otherStack = slot.getItem();
         if (otherStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeOne(thisStack).ifPresent((p_150740_) -> {
               add(thisStack, slot.safeInsert(p_150740_));
            });
         }

         return true;
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (dropContents(itemstack, player) && player.isCrouching()) {
         this.playDropContentsSound(player);
         return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   private static int add(ItemStack otherStack, ItemStack thisStack) {
      if (!thisStack.isEmpty() && thisStack.getItem().canFitInsideContainerItems()) {
         CompoundTag compoundtag = otherStack.getOrCreateTag();
         if (!compoundtag.contains("Items")) {
            compoundtag.put("Items", new ListTag());
         }

         int k = thisStack.getCount();
         if (k == 0) {
            return 0;
         } else {
            ListTag listtag = compoundtag.getList("Items", 10);

            ItemStack itemstack1 = thisStack.copyWithCount(k);
            CompoundTag compoundtag2 = new CompoundTag();
            itemstack1.save(compoundtag2);
            listtag.add(0, (Tag) compoundtag2);

            return k;
         }
      } else {
         return 0;
      }
   }

   private static Optional<ItemStack> removeOne(ItemStack p_150781_) {
      CompoundTag compoundtag = p_150781_.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         return Optional.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         if (listtag.isEmpty()) {
            return Optional.empty();
         } else {
            CompoundTag compoundtag1 = listtag.getCompound(0);
            ItemStack itemstack = ItemStack.of(compoundtag1);
            listtag.remove(0);
            if (listtag.isEmpty()) {
               p_150781_.removeTagKey("Items");
            }

            return Optional.of(itemstack);
         }
      }
   }

   private static boolean dropContents(ItemStack stack, Player player) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (player.isCrouching()) {
         if (!compoundtag.contains("Items")) {
            return false;
         } else {
            if (player instanceof ServerPlayer) {
               ListTag listtag = compoundtag.getList("Items", 10);

               for (int i = 0; i < listtag.size(); ++i) {
                  CompoundTag compoundtag1 = listtag.getCompound(i);
                  ItemStack itemstack = ItemStack.of(compoundtag1);
                  player.drop(itemstack, true);
               }
            }

            stack.removeTagKey("Items");
            return true;
         }
      } else {
         return false;
      }
   }

   public static Stream<ItemStack> getContents(ItemStack p_150783_) {
      CompoundTag compoundtag = p_150783_.getTag();
      if (compoundtag == null) {
         return Stream.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
      }
   }

   @Override
   public Component getName(ItemStack stack) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (compoundtag.contains("Items")) {
         ListTag listtag = compoundtag.getList("Items", 10);
         if (listtag.size() > 0)
            return Component.translatable(this.getDescriptionId(stack) + ".full");
      }
      return Component.translatable(this.getDescriptionId(stack) + ".empty");
   }

   @Override
   public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      getContents(p_150775_).forEach(nonnulllist::add);
      if (nonnulllist.size() > 0) {
         return Optional.of(new PossessionsBoxTooltip(nonnulllist));
      } else {
         return Optional.empty();
      }
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level p_150750_, List<Component> component,
         TooltipFlag p_150752_) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         component.add(Component.translatable("Empty").withStyle(ChatFormatting.GRAY));
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         component.add(Component.translatable("Contains " + listtag.size() + " stacks of items.")
               .withStyle(ChatFormatting.GRAY));
      }

   }

   @Override
   public void onDestroyed(ItemEntity p_150728_) {
      ItemUtils.onContainerDestroyed(p_150728_, getContents(p_150728_.getItem()));
   }

   private void playRemoveOneSound(Entity entity) {
      entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity entity) {
      entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   public void FillFromInventory(ItemStack stack, Inventory inventory) {
      NonNullList<ItemStack> _items = inventory.items;
      NonNullList<ItemStack> _armor = inventory.armor;
      NonNullList<ItemStack> _offhand = inventory.offhand;

      for (int i = 0; i < _items.size(); i++) {
         add(stack, _items.get(i));
      }
      for (int i = 0; i < _armor.size(); i++) {
         add(stack, _armor.get(i));
      }
      for (int i = 0; i < _offhand.size(); i++) {
         add(stack, _offhand.get(i));
      }

      inventory.clearContent();
   }

   public void FillFromInventory(ItemStack stack, Inventory inventory, boolean clear) {
      NonNullList<ItemStack> _items = inventory.items;
      NonNullList<ItemStack> _armor = inventory.armor;
      NonNullList<ItemStack> _offhand = inventory.offhand;

      for (int i = 0; i < _items.size(); i++) {
         add(stack, _items.get(i));
      }
      for (int i = 0; i < _armor.size(); i++) {
         add(stack, _armor.get(i));
      }
      for (int i = 0; i < _offhand.size(); i++) {
         add(stack, _offhand.get(i));
      }

      if (clear)
         inventory.clearContent();
   }
}
