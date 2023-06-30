package com.lazrproductions.cuffed.server;

import java.util.ArrayList;
import java.util.Iterator;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.api.event.PlayerUncuffedEvent;
import com.lazrproductions.cuffed.cap.Handcuffed;
import com.lazrproductions.cuffed.packet.CuffedUpdatePacket;
import com.lazrproductions.cuffed.packet.UpdateChainedPacket;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class CuffedServer {
    public static boolean isHandcuffed(Player player) {
        return getHandcuffed(player).isHandcuffed();
    }

    public static float getProgress(Player player) {
        return getHandcuffed(player).getProgress();
    }

    public static IHandcuffed getHandcuffed(Player player) {
        return player.getCapability(CuffedMod.HANDCUFFED).orElseGet(Handcuffed::new);
    }

    public static void sendUpdatePacket(Player player) {
        CuffedUpdatePacket packet = new CuffedUpdatePacket(player);
        CuffedMod.NETWORK.sendToClientTracking(packet, player);
        CuffedMod.NETWORK.sendToClient(packet, (ServerPlayer) player);
    }

    public static void applyHandcuffs(Player player) {
        getHandcuffed(player).applyHandcuffs(player);
        player.getPersistentData().putBoolean("cuffed:handcuffed", true);
        sendUpdatePacket(player);
    }

    public static void removeHandcuffs(Player player) {

        IHandcuffed uncuff = getHandcuffed(player);
        uncuff.removeHandcuffs();

        MinecraftForge.EVENT_BUS.post(new PlayerUncuffedEvent(player, uncuff));

        sendUpdatePacket(player);
    }

    public static void removePlayerAsHelper(Player player) {
        MinecraftServer server = player.getServer();
        if (server != null)
            for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                    .hasNext();) {
                ServerPlayer member = iterator.next();
                IHandcuffed cuffed = getHandcuffed(member);
                cuffed.setCuffingPlayer(null);
            }
    }

    public static ArrayList<IHandcuffed> getAllHandcuffed(Level level) {
        ArrayList<IHandcuffed> c = new ArrayList<IHandcuffed>(0);
        MinecraftServer server = level.getServer();
        if (server != null)
            for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                    .hasNext();) {
                ServerPlayer member = iterator.next();
                c.add(getHandcuffed(member));
            }
        return c;
    }

    // #region Chained Players Handling

    public static ArrayList<Pair<Player, Entity>> chainedPlayers = new ArrayList<Pair<Player, Entity>>();

    public static void addChainedPair(Player player, Entity entity) {
        int index = getChainedFor(player);

        Pair<Player, Entity> p = new Pair<Player, Entity>(player, entity);
        if (index < 0) {
            //CuffedMod.LOGGER.info("Adding chained pair " + player + " and " + entity);
            chainedPlayers.add(p);
        } else {
            //CuffedMod.LOGGER.info("Setting chained pair at index " + index + " to " + player + " and " + entity);
            chainedPlayers.set(index, p);
        }
        sendChainedUpdateToAll(player.level());
    }

    public static int getChainedFor(Player player) {
        for (int i = 0; i < chainedPlayers.size(); i++) {
            if (chainedPlayers.get(i).getFirst().getId() == player.getId())
                return i;
        }
        return -1;
    }

    public static void removeChainedPair(Player player, Entity entity) {
        int removeAt = -1;
        for (int i = 0; i < chainedPlayers.size(); i++) {
            Pair<Player, Entity> p = chainedPlayers.get(i);
            if (p.getFirst() == player && p.getSecond() == entity) {
                removeAt = i;
                break;
            }
        }
        if (removeAt >= 0)
            chainedPlayers.remove(removeAt);

        sendChainedUpdateToAll(player.level());
    }

    public static void removeChainedFrom(Player player) {
        ArrayList<Integer> removeAt = new ArrayList<Integer>();
        for (int i = 0; i < chainedPlayers.size(); i++) {
            Pair<Player, Entity> p = chainedPlayers.get(i);
            if (p.getFirst() == player) {
                removeAt.add(i);
                break;
            }
        }
        for (int i = 0; i < removeAt.size(); i++)
            chainedPlayers.remove((int) removeAt.get(i));

        sendChainedUpdateToAll(player.level());
    }

    public static void removeAllChainedFor(Player player) {
        ArrayList<Integer> removeAt = new ArrayList<Integer>();
        for (int i = 0; i < chainedPlayers.size(); i++) {
            Pair<Player, Entity> p = chainedPlayers.get(i);
            if (p.getFirst() == player || p.getSecond() == player) {
                removeAt.add(i);
                break;
            }
        }
        for (int i = 0; i < removeAt.size(); i++)
            chainedPlayers.remove((int) removeAt.get(i));

        sendChainedUpdateToAll(player.level());
    }

    public static void sendChainedUpdateToAll(Level level) {
        //CuffedMod.LOGGER.info("[Server] - Sending chained update to 'erybody with size " + chainedPlayers.size());

        MinecraftServer server = level.getServer();
        if (server != null)
            for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                    .hasNext();) {
                ServerPlayer member = iterator.next();
                ArrayList<Pair<Integer, Integer>> l = UpdateChainedPacket.ComplexToSimple(chainedPlayers);
                int[] a = UpdateChainedPacket.ListToGeneric(l, true);
                int[] b = UpdateChainedPacket.ListToGeneric(l, false);

                CuffedMod.LOGGER.info("Getting list, a: " + a.length + " - b: " + b.length);
                CuffedMod.NETWORK.sendToClient(new UpdateChainedPacket(a, b), member);
            }
    }

    // #endregion
}
