package com.lazrproductions.cuffed.event;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.client.gui.screen.GenericScreen;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CuffedMod.MODID, value = Dist.CLIENT)
public class ModClientBusEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft inst = Minecraft.getInstance();

        if (inst != null) {
            if (inst.screen instanceof GenericScreen sc) {
                sc.handleKeyAction(event.getKey(), event.getAction());

                if (event.getKey() == 256) {
                    sc.onClose();
                    inst.setScreen(null);
                    inst.setOverlay(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Post event) {
        Minecraft inst = Minecraft.getInstance();

        if (inst != null) {
            if (inst.screen instanceof GenericScreen sc)
                sc.handleMouseAction(event.getButton(), event.getAction());
        }
    }
}
