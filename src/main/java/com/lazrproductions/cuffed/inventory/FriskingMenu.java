package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.init.ModMenuTypes;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FriskingMenu extends AbstractContainerMenu {
    private final Container container;
    private final int containerRows;

    private FriskingMenu(MenuType<?> type, int containerId, Inventory otherPlayerInv, int rows) {
        this(type, containerId, otherPlayerInv, new SimpleContainer(9 * rows), rows);
    }

    public FriskingMenu(MenuType<?> type, int containerId, Inventory playerInv, Container container, int rows) {
        super(type, containerId);
        checkContainerSize(container, rows * 9);
        this.container = container;
        this.containerRows = rows;
        container.startOpen(playerInv.player);

        // create other's armor slots
        this.addSlot(new FriskingSlot(container, 0, 8, 8 + (18 * 0)));
        this.addSlot(new FriskingSlot(container, 1, 8, 8 + (18 * 1)));
        this.addSlot(new FriskingSlot(container, 2, 8, 8 + (18 * 2)));
        this.addSlot(new FriskingSlot(container, 3, 8, 8 + (18 * 3)));

        // create other's offhand
        this.addSlot(new FriskingSlot(container, 8, 5 + (18 * 4), 8 + (18 * 3)));

        // create other's inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new FriskingSlot(container, column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18));
            }
        }

        // create other's hotbar
        for (int column = 0; column < 9; column++) {
            this.addSlot(new FriskingSlot(container, 36 + column,
                    8 + column * 18,
                    142));
        }

        // create frisker inventory slots
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + l * 9 + 9,
                        8 + j1 * 18,
                        174 + l * 18));
            }
        }

        // Create frisker hotbar slots
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, 232));
        }

    }

    public FriskingMenu(int containerId, Inventory inv) {
        this(ModMenuTypes.FRISKING_MENU.get(), containerId, inv, 5);
    }


    public boolean stillValid(@Nonnull Player player) {
        return this.container.stillValid(player);
    }

    public ItemStack quickMoveStack(@Nonnull Player player, int count) {
        return ItemStack.EMPTY;
    }

    public boolean canDragTo(@Nonnull Slot slot) {
        return false;
    }

    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }
}