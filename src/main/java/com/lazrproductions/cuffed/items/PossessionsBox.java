package com.lazrproductions.cuffed.items;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.init.ModMenuTypes;
import com.lazrproductions.cuffed.inventory.FriskingContainer;
import com.lazrproductions.cuffed.inventory.FriskingMenu;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class PossessionsBox extends Item {

   public final static String TAG_ITEMS = "Items";

   public PossessionsBox(Properties properties) {
      super(properties);
   }

   @Override
   public boolean overrideStackedOnOther(@Nonnull ItemStack thisStack, @Nonnull Slot slot, @Nonnull ClickAction click,
         @Nonnull Player player) {
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
   public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
         @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (dropContents(itemstack, player) && player.isCrouching()) {
         this.playDropContentsSound(player);
         return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public static ItemStack add(ItemStack stack, ItemStack stackToAdd) {
      if (!stackToAdd.isEmpty()) {
         CompoundTag compoundtag = stack.getOrCreateTag();
         if (!compoundtag.contains(TAG_ITEMS)) {
            compoundtag.put(TAG_ITEMS, new ListTag());
         }

         int k = stackToAdd.getCount();
         if (k == 0) {
            return stackToAdd;
         } else {
            ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);

            ItemStack itemstack1 = stackToAdd.copyWithCount(k);
            CompoundTag compoundtag2 = new CompoundTag();
            itemstack1.save(compoundtag2);
            listtag.add(0, (Tag) compoundtag2);
            return stackToAdd;
         }
      } else {
         return stackToAdd;
      }
   }

   private static Optional<ItemStack> removeOne(ItemStack stack) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (!compoundtag.contains(TAG_ITEMS)) {
         return Optional.empty();
      } else {
         ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
         if (listtag.isEmpty()) {
            return Optional.empty();
         } else {
            CompoundTag compoundtag1 = listtag.getCompound(0);
            ItemStack itemstack = ItemStack.of(compoundtag1);
            listtag.remove(0);
            if (listtag.isEmpty()) {
               stack.removeTagKey(TAG_ITEMS);
            }

            return Optional.of(itemstack);
         }
      }
   }

   private static boolean dropContents(ItemStack stack, Player player) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (player.isCrouching()) {
         if (!compoundtag.contains(TAG_ITEMS)) {
            return false;
         } else {
            if (player instanceof ServerPlayer) {
               ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);

               for (int i = 0; i < listtag.size(); ++i) {
                  CompoundTag compoundtag1 = listtag.getCompound(i);
                  ItemStack itemstack = ItemStack.of(compoundtag1);
                  player.drop(itemstack, true);
               }
            }

            stack.removeTagKey(TAG_ITEMS);
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
         ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
         return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
      }
   }

   @Override
   public Component getName(@Nonnull ItemStack stack) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (compoundtag.contains(TAG_ITEMS)) {
         ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
         if (listtag.size() > 0)
            return Component.translatable(this.getDescriptionId(stack) + ".full");
      }
      return Component.translatable(this.getDescriptionId(stack) + ".empty");
   }

   @Override
   public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack p_150775_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      getContents(p_150775_).forEach(nonnulllist::add);
      if (nonnulllist.size() > 0) {
         return Optional.of(new PossessionsBoxTooltip(nonnulllist));
      } else {
         return Optional.empty();
      }
   }

   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level p_150750_, @Nonnull List<Component> component,
         @Nonnull TooltipFlag p_150752_) {
      CompoundTag compoundtag = stack.getOrCreateTag();
      if (!compoundtag.contains(TAG_ITEMS)) {
         component.add(Component.translatable("item.cuffed.possessions_box.lore.empty").withStyle(ChatFormatting.GRAY));
      } else {
         ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
         component.add(Component.translatable("item.cuffed.possessions_box.lore.full", listtag.size())
               .withStyle(ChatFormatting.GRAY));
      }

   }

   @Override
   public void onDestroyed(@Nonnull ItemEntity p_150728_) {
      ItemUtils.onContainerDestroyed(p_150728_, getContents(p_150728_.getItem()));
   }

   private void playRemoveOneSound(Entity entity) {
      entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity entity) {
      entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
   }

   public static void frisk(@Nonnull ServerPlayer frisker, @Nonnull ServerPlayer player, @Nonnull ItemStack boxStack) {
      frisker.openMenu(new MenuProvider() {
         @Override
         public Component getDisplayName() {
            return player.getDisplayName();
         }

         @Override
         public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player p) {
            return new FriskingMenu(ModMenuTypes.FRISKING_MENU.get(), id, playerInventory, player.getId(), new FriskingContainer(player, boxStack, frisker), 5);
         }
      });
   }
}
