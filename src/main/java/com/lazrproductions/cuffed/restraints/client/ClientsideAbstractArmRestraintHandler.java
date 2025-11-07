package com.lazrproductions.cuffed.restraints.client;

import java.util.ArrayList;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class ClientsideAbstractArmRestraintHandler extends ClientsideAbstractRestraintHandler {

    static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "textures/gui/widgets.png");

    static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 76, 24, 16, 16, 192, 192);

    public ClientsideAbstractArmRestraintHandler(AbstractRestraint parent) {
        super(parent);
    }

    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        graphics.setColor(1, 1, 1, 1);

        int screenWidth = (int) (16 * 1.75f);
        int screenHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (screenWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (screenHeight) - 65;


        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
        ArrayList<Component> c = new ArrayList<>();
        c.add(Component.translatable(parent.getActionBarLabel()));
        ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
    }
}
