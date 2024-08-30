package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.items.PossessionsBox;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FriskingContainer implements Container {
	private final ServerPlayer player;
	private final ItemStack boxStack;

	public FriskingContainer(ServerPlayer player, ItemStack boxStack) {
		this.player = player;
		this.boxStack = boxStack;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public int getContainerSize() {
		return 54;
	}

	@Override
	public boolean isEmpty() {
		return player.getInventory().isEmpty();
	}

	public boolean isInvalidSlot(int index) {
		return index >= 4 && index < 8;
	}

	public int getSlot(int index) {
		if (index == 8) {
			return 40;
		} else if (index >= 0 && index <= 3) {
			return 39 - index;
		} else if (index >= 9 && index <= 35) {
			return index;
		} else if (index >= 36 && index <= 44) {
			return index - 36;
		}

		return -1;
	}

	@Override
	public ItemStack getItem(int index) {
		if (isInvalidSlot(index)) {
			return ItemStack.EMPTY;
		}

		int slot = getSlot(index);
		return slot == -1 ? ItemStack.EMPTY : player.getInventory().getItem(slot);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (isInvalidSlot(index)) {
			return ItemStack.EMPTY;
		}

		int slot = getSlot(index);		
		if(slot > -1){
			if(!player.getInventory().getItem(slot).isEmpty()) {
				PossessionsBox.add(boxStack, player.getInventory().getItem(slot));
				count = player.getInventory().getItem(slot).getCount();
			}

			player.getInventory().removeItem(slot, count);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (isInvalidSlot(index)) {
			return ItemStack.EMPTY;
		}

		int slot = getSlot(index);		
		if(slot > -1){
			if(!player.getInventory().getItem(slot).isEmpty()) 
				PossessionsBox.add(boxStack, player.getInventory().getItem(slot));
		
			player.getInventory().removeItemNoUpdate(slot);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, @Nonnull ItemStack is) {
		if (isInvalidSlot(index)) {
			return;
		}

		int slot = getSlot(index);

		if (slot != -1) {
			player.getInventory().setItem(slot, is);
			setChanged();
		}
	}

	@Override
	public int getMaxStackSize() {
		return player.getInventory().getMaxStackSize();
	}

	@Override
	public void setChanged() {
		player.getInventory().setChanged();
		player.containerMenu.broadcastChanges();
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public void clearContent() {
		player.getInventory().clearContent();
	}
}