package com.lazrproductions.cuffed.compat;
import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;

public class KnightsOfBritanniaCompat {
    public static void load() {
    }

    public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
        MinecraftServer server = player.getServer();
        if(server == null)
            return;

        var scoreboard = server.getScoreboard();
        if(scoreboard == null)
            return;

        Objective currentManaObjective = scoreboard.getObjective("kob.mana");
        if(currentManaObjective == null)
            return;

        ScoreAccess myManaScore = scoreboard.getOrCreatePlayerScore(player, currentManaObjective);
        
        int currentMana = myManaScore.get();

        if(currentMana <= 0)
            return;

        myManaScore.set(currentMana - (int)amount);
    }

    public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
        MinecraftServer server = player.getServer();
        if(server == null)
            return;

        var scoreboard = server.getScoreboard();
        if(scoreboard == null)
            return;

        Objective currentManaObjective = scoreboard.getObjective("kob.mana");
        if(currentManaObjective == null)
            return;
        Objective maxManaObjective = scoreboard.getObjective("kob.mana.max");
        if(maxManaObjective == null)
            return;

        ScoreAccess myManaScore = scoreboard.getOrCreatePlayerScore(player, currentManaObjective);
        ScoreAccess maxManaScore = scoreboard.getOrCreatePlayerScore(player, maxManaObjective);
        
        int currentMana = myManaScore.get();

        if(currentMana <= 0)
            return;

        float amount = (float)maxManaScore.get() * (float)amountPercentage;
        myManaScore.set(currentMana - (int)amount);
    }
}
