package com.lazrproductions.cuffed.compat;

import net.minecraft.world.entity.player.Player;
import team.creative.playerrevive.server.PlayerReviveServer;

public class PlayerReviveCompat {
    public static void load() {
    }

    public static void Kill(Player player) {
        PlayerReviveServer.getBleeding(player).forceBledOut();
    }
}
