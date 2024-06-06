package com.lazrproductions.cuffed.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

public class MenuUtils {
    public static void openCustomMenu(ServerPlayer player, MenuProvider menuProvider, BlockPos pos) {
        NetworkHooks.openScreen(player, menuProvider, pos);
    }
}
