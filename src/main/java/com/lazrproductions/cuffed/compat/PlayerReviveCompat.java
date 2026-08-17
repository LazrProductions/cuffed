package com.lazrproductions.cuffed.compat;

import net.minecraft.world.entity.player.Player;

public class PlayerReviveCompat {
    public static void load() {
    }

    public static void Kill(Player player) {
        try {
            Class<?> serverClass = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
            Object bleedingObj = serverClass.getMethod("getBleeding", Player.class).invoke(null, player);
            if (bleedingObj != null) {
                bleedingObj.getClass().getMethod("forceBledOut").invoke(bleedingObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void Revive(Player player) {
        try {
            Class<?> serverClass = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
            serverClass.getMethod("revive", Player.class).invoke(null, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean IsBleedingOut(Player player) {
        try {
            Class<?> serverClass = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
            return (boolean) serverClass.getMethod("isBleeding", Player.class).invoke(null, player);
        } catch (Exception e) {
            return false;
        }
    }
}
