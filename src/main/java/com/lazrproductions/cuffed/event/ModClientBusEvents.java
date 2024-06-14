package com.lazrproductions.cuffed.event;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.client.gui.screen.GenericScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CuffedMod.MODID, value = Dist.CLIENT)
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
            
            LocalPlayer p = inst.player;
            if (p != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
                if(cap!=null)
                    cap.onKeyInput(p, event.getKey(), event.getAction());
            }
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Post event) {
        Minecraft inst = Minecraft.getInstance();

        if (inst != null) {
            if (inst.screen instanceof GenericScreen sc)
                sc.handleMouseAction(event.getButton(), event.getAction());

            LocalPlayer p = inst.player;
            if (p != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
                if(cap!=null)
                    cap.onMouseInput(p, event.getButton(), event.getAction());
            }   
        }
    }
}
