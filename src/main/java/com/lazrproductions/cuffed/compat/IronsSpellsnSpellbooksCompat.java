package com.lazrproductions.cuffed.compat;

import javax.annotation.Nonnull;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.server.level.ServerPlayer;

public class IronsSpellsnSpellbooksCompat {
    public static void load() {
    }

    public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
        MagicData.getPlayerMagicData(player).addMana(-amount);
    }

    public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
        MagicData data = MagicData.getPlayerMagicData(player);
        float maxMana = (float)player.getAttributeValue(AttributeRegistry.MAX_MANA.get());
        data.addMana(-maxMana * (float)amountPercentage);
    }
}
