package com.lazrproductions.cuffed.restraints.base;

import java.util.ArrayList;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.lazrslib.client.font.FontUtilities;
import com.lazrproductions.lazrslib.client.gui.GuiGraphics;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractArmRestraint extends AbstractRestraint {

    static final ResourceLocation WIDGETS = new ResourceLocation(CuffedMod.MODID, "textures/gui/widgets.png");

    static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 76, 24, 16, 16, 192, 192);

    public AbstractArmRestraint(){}
    public AbstractArmRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        super(stack, player, captor);
    }

    public RestraintType getType() {
        return RestraintType.Arm;
    }

public ArrayList<Integer> getBlockedKeyCodes() {
        ArrayList<Integer> b = new ArrayList<Integer>();
        Minecraft inst = Minecraft.getInstance();
        
        if(inst == null || inst.options == null)
        return b;

        b.add(inst.options.keyAttack.getKey().getValue());
        b.add(inst.options.keyUse.getKey().getValue());
        b.add(inst.options.keyInventory.getKey().getValue());
        b.add(inst.options.keyDrop.getKey().getValue());
        for (var i : inst.options.keyHotbarSlots) {
            b.add(i.getKey().getValue());
        }
        b.add(inst.options.keyInventory.getKey().getValue());
        b.add(inst.options.keyPickItem.getKey().getValue());
        b.add(inst.options.keySwapOffhand.getKey().getValue());

        for (KeyMapping mapping : inst.options.keyMappings) {
            switch (mapping.getName()) {
                case "key.parcool.Crawl":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Breakfall":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.WallSlide":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Vault":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Flipping":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.FastRun":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.ClingToCliff":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.HangDown":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.WallJump":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.HorizontalWallRun":
                    b.add(mapping.getKey().getValue());
                    break;
            }
        }

        return b;
    }

    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        graphics.setColor(1, 1, 1, 1);

        int screenWidth = (int) (16 * 1.75f);
        int screenHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (screenWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (screenHeight) - 65;


        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
        ArrayList<Component> c = new ArrayList<>();
        c.add(Component.translatable(getActionBarLabel()));
        FontUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
    }
    
    public abstract ArmRestraintAnimationFlags getAnimationFlags();
}
