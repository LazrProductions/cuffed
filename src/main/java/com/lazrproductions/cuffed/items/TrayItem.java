package com.lazrproductions.cuffed.items;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.inventory.tooltip.TrayTooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class TrayItem extends BlockItem {
    public static final String TAG_ITEMS = "Items";
    public static final int MAX_WEIGHT = 4;

    public TrayItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public boolean overrideStackedOnOther(@Nonnull ItemStack stack, @Nonnull Slot slot,
            @Nonnull ClickAction clickAction, @Nonnull Player player) {
        if (stack.getCount() != 1 || clickAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack = slot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeOne(stack).ifPresent((stackInSlot) -> {
                    add(stack, slot.safeInsert(stackInSlot));
                });
            } else if (canFitInTray(stack, itemstack)) {
                int j = add(stack, slot.safeTake(itemstack.getCount(), 1, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
    }

    public boolean overrideOtherStackedOnMe(@Nonnull ItemStack stack, @Nonnull ItemStack otherStack, @Nonnull Slot slot,
            @Nonnull ClickAction clickAction, @Nonnull Player player, @Nonnull SlotAccess slotAccess) {
        if (stack.getCount() != 1)
            return false;
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (otherStack.isEmpty()) {
                removeOne(stack).ifPresent((s) -> {
                    this.playRemoveOneSound(player);
                    slotAccess.set(s);
                });
            } else {
                int i = add(stack, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.shrink(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
            @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isCrouching() && dropContents(itemstack, player)) {
            this.playDropContentsSound(player);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            return super.use(level, player, hand);
        }
    }


    private static int add(@Nonnull ItemStack stack, @Nonnull ItemStack stackToAdd) {
        if (!stackToAdd.isEmpty() && canFitInTray(stack, stackToAdd)) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains(TAG_ITEMS)) {
                tag.put(TAG_ITEMS, saveItemToTagList(NonNullList.withSize(MAX_WEIGHT, ItemStack.EMPTY)));
            }


            ListTag listtag = tag.getList(TAG_ITEMS, 10);


            int slotToAddTo = -1;
            if(itemIsFood(stackToAdd))
                slotToAddTo = 0;
            if(itemIsFork(stackToAdd))
                slotToAddTo = 1;
            if(itemIsSpoon(stackToAdd))
                slotToAddTo = 2;
            if(itemIsKnife(stackToAdd))
                slotToAddTo = 3;
                

            if(slotToAddTo > -1) {
                if(listtag.getCompound(slotToAddTo).isEmpty()) {
                    ItemStack itemToAddCopy = stackToAdd.copy();
                    itemToAddCopy.setCount(1);
                    CompoundTag itemToAddData = new CompoundTag();
                    itemToAddCopy.save(itemToAddData);
                    listtag.set(slotToAddTo, (Tag) itemToAddData);
                    return 1;
                }
            }
        }

        return 0;
    }

    private static Optional<ItemStack> removeOne(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        if (compoundtag.contains(TAG_ITEMS)) {
            int toRemove = getNextStackIndex(stack);
            if(toRemove > -1) {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
            
                CompoundTag compoundtag1 = listtag.getCompound(toRemove);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.set(toRemove, new CompoundTag());

                return Optional.of(itemstack);
            }
        }

        return Optional.empty();
    }

    private static boolean dropContents(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TAG_ITEMS)) {
            if (player instanceof ServerPlayer) {
                ListTag listtag = tag.getList(TAG_ITEMS, 10);

                for (int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    player.drop(itemstack, true);
                }

                for (int i = 0; i < listtag.size(); i++)
                    listtag.set(i, new CompoundTag());
                
                return true;
            }
            
        }

        return false;
    }



    public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(stack).forEach(nonnulllist::add);
        return Optional.of(new TrayTooltip(nonnulllist));
    }



    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.LANTERN_STEP, 0.8F,
                0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }
    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.LANTERN_HIT, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }
    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F,
                0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }


    public static NonNullList<ItemStack> getContents(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        var list = NonNullList.withSize(MAX_WEIGHT, ItemStack.EMPTY);
        
        if(tag.contains(TAG_ITEMS)) {
            ListTag listtag = tag.getList(TAG_ITEMS, 10);

        
            CompoundTag food = listtag.getCompound(0);
            CompoundTag fork = listtag.getCompound(1);
            CompoundTag spoon = listtag.getCompound(2);
            CompoundTag knife = listtag.getCompound(3);

            if (!food.isEmpty())
                list.set(0, ItemStack.of(food));
            else
                list.set(0, ItemStack.EMPTY);

            if (!fork.isEmpty())
                list.set(1, ItemStack.of(fork));
            else
                list.set(1, ItemStack.EMPTY);

            if (!spoon.isEmpty())
                list.set(2, ItemStack.of(spoon));
            else
                list.set(2, ItemStack.EMPTY);

            if (!knife.isEmpty())
                list.set(3, ItemStack.of(knife));
            else
                list.set(3, ItemStack.EMPTY);
        }
        return list;
    }
    public static NonNullList<ItemStack> getContents(@Nonnull ListTag listtag) {
        var list = NonNullList.withSize(MAX_WEIGHT, ItemStack.EMPTY);
        
        CompoundTag food = listtag.getCompound(0);
        CompoundTag fork = listtag.getCompound(1);
        CompoundTag spoon = listtag.getCompound(2);
        CompoundTag knife = listtag.getCompound(3);

        if (!food.isEmpty())
            list.set(0, ItemStack.of(food));
        else
            list.set(0, ItemStack.EMPTY);

        if (!fork.isEmpty())
            list.set(1, ItemStack.of(fork));
        else
            list.set(1, ItemStack.EMPTY);

        if (!spoon.isEmpty())
            list.set(2, ItemStack.of(spoon));
        else
            list.set(2, ItemStack.EMPTY);

        if (!knife.isEmpty())
            list.set(3, ItemStack.of(knife));
        else
            list.set(3, ItemStack.EMPTY);
        
        return list;
    }
    

    public static int getNextStackIndex(@Nonnull ItemStack stack) {
        var list = getContents(stack);
        for (int i = 0; i < list.size(); i++) {
            if(!list.get(i).isEmpty())
                return i;
        }
        return -1;
    }
    public static int getNextAvailableIndex(@Nonnull ItemStack stack) {
        var list = getContents(stack);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isEmpty())
                return i;
        }
        return -1;
    }

    



    public static ListTag saveItemToTagList(NonNullList<ItemStack> items) {
        ListTag listtag = new ListTag();
        
        ItemStack food = items.get(0);
        ItemStack fork = items.get(1);
        ItemStack spoon = items.get(2);
        ItemStack knife = items.get(3);

        if(!food.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            listtag.add(food.save(tag));
        } else {
            listtag.add(new CompoundTag());
        }
        if(!fork.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            listtag.add(fork.save(tag));
        } else {
            listtag.add(new CompoundTag());
        }
        if(!spoon.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            listtag.add(spoon.save(tag));
        } else {
            listtag.add(new CompoundTag());
        }
        if(!knife.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            listtag.add(knife.save(tag));
        } else {
            listtag.add(new CompoundTag());
        }

        return listtag;
    }
    
    


    public static ItemStack createTrayFrom(NonNullList<ItemStack> stacks) {
        ItemStack stack = ModItems.TRAY.get().getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(TAG_ITEMS, saveItemToTagList(stacks));
        return stack;
    }





    private static boolean canFitInTray(@Nonnull ItemStack tray, @Nonnull ItemStack stack) {

        boolean trayHasFood = trayHasFoodItem(tray);
        boolean trayHasSpoon = trayHasSpoon(tray);
        boolean trayHasFork = trayHasFork(tray);
        boolean trayHasKnife = trayHasKnife(tray);

        boolean isFood = stack.isEdible();
        if (isFood)
            return !trayHasFood;
        else if (stack.is(ModItems.SPOON.get()))
            return !trayHasSpoon;
        else if (stack.is(ModItems.FORK.get()))
            return !trayHasFork;
        else if (stack.is(ModItems.KNIFE.get()))
            return !trayHasKnife;
        return false;
    }
    public static boolean trayHasFoodItem(@Nonnull ItemStack stack) {
        var stacks = getContents(stack);
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEdible())
                return true;
        }
        return false;
    }
    public static boolean trayHasSpoon(@Nonnull ItemStack stack) {
        var stacks = getContents(stack);
        for (ItemStack itemStack : stacks)
            if (itemStack.is(ModItems.SPOON.get()))
                return true;
        return false;
    }
    public static boolean trayHasFork(@Nonnull ItemStack stack) {
        var stacks = getContents(stack);
        for (ItemStack itemStack : stacks)
            if (itemStack.is(ModItems.FORK.get()))
                return true;
        return false;
    }
    public static boolean trayHasKnife(@Nonnull ItemStack stack) {
        var stacks = getContents(stack);
        for (ItemStack itemStack : stacks)
            if (itemStack.is(ModItems.KNIFE.get()))
                return true;
        return false;
    }
    public static boolean itemIsFood(@Nonnull ItemStack stack) {
        return stack.isEdible();
    }
    public static boolean itemIsSpoon(@Nonnull ItemStack stack) {
        return stack.is(ModItems.SPOON.get());
    }
    public static boolean itemIsFork(@Nonnull ItemStack stack) {
        return stack.is(ModItems.FORK.get());
    }
    public static boolean itemIsKnife(@Nonnull ItemStack stack) {
        return stack.is(ModItems.KNIFE.get());
    }
}
