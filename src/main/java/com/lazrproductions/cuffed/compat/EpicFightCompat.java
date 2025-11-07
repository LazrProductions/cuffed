package com.lazrproductions.cuffed.compat;

import net.minecraft.world.entity.player.Player;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class EpicFightCompat {
    public static void load() {}

    static boolean wasEnabled = false;
    static boolean wasRecentlyDisabled = false;
    
    public static void Disable(Player player) {
        if(wasRecentlyDisabled)
            return;
        try {
            LocalPlayerPatch p = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            if(p.isEpicFightMode()) {
                wasEnabled = true;
                p.toVanillaMode(true);
            }
        } catch(Exception e) {
            return;
        }
    }

    public static void Reset(Player player) {
        if(!wasRecentlyDisabled)
            return;
        wasRecentlyDisabled = false;
        try {
            LocalPlayerPatch p = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            if(wasEnabled)
                p.toEpicFightMode(true);
        } catch(Exception e) {
            return;
        }
    }
}
