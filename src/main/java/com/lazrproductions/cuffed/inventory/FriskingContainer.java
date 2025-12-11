package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.items.PossessionsBox;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FriskingContainer implements Container {
	private final ServerPlayer player;
	private final ItemStack boxStack;
	private final Player frisker;

	private static final int EXTRACTION_DELAY_TICKS = 40;
	private static final double MAX_FRISKING_DISTANCE = 4.0;
	private long lastGlobalExtractionTime;

	public FriskingContainer(ServerPlayer player, ItemStack boxStack) {
		this(player, boxStack, null);
	}

	public FriskingContainer(ServerPlayer player, ItemStack boxStack, Player frisker) {
		this.player = player;
		this.boxStack = boxStack;
		this.frisker = frisker;
		this.lastGlobalExtractionTime = player.level().getGameTime();
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

	@Override
	public boolean canTakeItem(@Nonnull Container p_273520_, int p_272681_, @Nonnull ItemStack p_273702_) {
		return false;
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

		long currentTime = player.level().getGameTime();
		if ((currentTime - lastGlobalExtractionTime) < EXTRACTION_DELAY_TICKS) {
			return ItemStack.EMPTY;
		}

		if (frisker == null || !isValidFriskingState(frisker)) {
			return ItemStack.EMPTY;
		}

		int slot = getSlot(index);
		if (slot < 0) {
			return ItemStack.EMPTY;
		}

		ItemStack original = player.getInventory().getItem(slot);
		if (original.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack toTransfer = original.copy();

		player.getInventory().setItem(slot, ItemStack.EMPTY);

		PossessionsBox.add(boxStack, toTransfer);

		lastGlobalExtractionTime = currentTime;

		setChanged();

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (isInvalidSlot(index)) {
			return ItemStack.EMPTY;
		}

		long currentTime = player.level().getGameTime();
		if ((currentTime - lastGlobalExtractionTime) < EXTRACTION_DELAY_TICKS) {
			return ItemStack.EMPTY;
		}

		if (frisker == null || !isValidFriskingState(frisker)) {
			return ItemStack.EMPTY;
		}

		int slot = getSlot(index);
		if (slot < 0) {
			return ItemStack.EMPTY;
		}

		ItemStack original = player.getInventory().getItem(slot);
		if (original.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack toTransfer = original.copy();

		player.getInventory().setItem(slot, ItemStack.EMPTY);

		PossessionsBox.add(boxStack, toTransfer);

		lastGlobalExtractionTime = currentTime;

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
	public boolean stillValid(@Nonnull Player friskerPlayer) {
		return isValidFriskingState(friskerPlayer);
	}

	private boolean isValidFriskingState(Player friskerPlayer) {
		if (player == null || player.isRemoved()) {
			return false;
		}

		if (friskerPlayer == null || friskerPlayer.isRemoved()) {
			return false;
		}

		IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
		if (cap == null || !cap.armsRestrained()) {
			return false;
		}

		double distance = friskerPlayer.position().distanceTo(player.position());
		if (distance > MAX_FRISKING_DISTANCE) {
			return false;
		}

		return true;
	}

	@Override
	public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public void clearContent() {
	}
}
