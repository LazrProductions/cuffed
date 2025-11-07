package com.lazrproductions.cuffed.restraints.base;

import java.util.ArrayList;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractHeadRestraint extends AbstractRestraint {

    static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "textures/gui/widgets.png");

    static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 60, 40, 16, 16, 192, 192);

    public AbstractHeadRestraint(){}
    public AbstractHeadRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        super(stack, player, captor);
    }

    public RestraintType getType() {
        return RestraintType.Head;
    }

    public ArrayList<Integer> getBlockedKeyCodes() {
        ArrayList<Integer> b = new ArrayList<Integer>();
        return b; // head restraints dont block key codes silly!
    }


    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        graphics.setColor(1, 1, 1, 1);

        int screenWidth = (int) (16 * 1.75f);
        int screenHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (screenWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (screenHeight) - 100;
        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);

        ArrayList<Component> c = new ArrayList<>();
        c.add(Component.translatable(getActionBarLabel()));
        ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
    }
}
