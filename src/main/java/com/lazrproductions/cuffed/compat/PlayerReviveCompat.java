package com.lazrproductions.cuffed.compat;

import net.minecraft.world.entity.player.Player;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class PlayerReviveCompat {
    public static void load() {}

    public static boolean IsDowned(Player player) {
        IBleeding revive = PlayerReviveServer.getBleeding(player);
        return revive.isBleeding();
    }

    public static void Revive(Player player) {
        IBleeding revive = PlayerReviveServer.getBleeding(player);
        revive.revive();
    }

    public static void Kill(Player player) {
        IBleeding revive = PlayerReviveServer.getBleeding(player);
        revive.forceBledOut();
    }
}
