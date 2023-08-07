package com.lazrproductions.cuffed.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.api.event.PlayerUncuffedEvent;
import com.lazrproductions.cuffed.cap.Handcuffed;
import com.lazrproductions.cuffed.packet.CuffedUpdatePacket;
import com.lazrproductions.cuffed.packet.LockpickPacket;
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


    public static ArrayList<Pair<Player, Entity>> chainedPlayers = new ArrayList<Pair<Player, Entity>>();

    public static void addChainedPair(Player player, Entity entity) {
        int index = getChainedFor(player);

        Pair<Player, Entity> p = new Pair<Player, Entity>(player, entity);
        if (index < 0) {
            // CuffedMod.LOGGER.info("Adding chained pair " + player + " and " + entity);
            chainedPlayers.add(p);
        } else {
            // CuffedMod.LOGGER.info("Setting chained pair at index " + index + " to " +
            // player + " and " + entity);
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
    

    public static ArrayList<Player> handcuffedPlayers = new ArrayList<Player>();

    public static void addHandcuffed(Player player) {
        if (player == null) {
            CuffedMod.LOGGER.warn("Cannot add a null player to the handcuffed list!");
            return;
        }
        int index = getChainedFor(player);

        if (index < 0) {
            // CuffedMod.LOGGER.info("Adding chained pair " + player + " and " + entity);
            handcuffedPlayers.add(player);
        } else {
            // CuffedMod.LOGGER.info("Setting chained pair at index " + index + " to " +
            // player + " and " + entity);
            handcuffedPlayers.set(index, player);
        }
        sendChainedUpdateToAll(player.level());
    }

    public static void removeHandcuffed(Player player) {
        if(handcuffedPlayers.indexOf(player) <= -1)
            return;
        handcuffedPlayers.remove(handcuffedPlayers.indexOf(player));
        sendChainedUpdateToAll(player.level());
    }

    public static int getHandcuffedFor(Player player) {
        for (int i = 0; i < handcuffedPlayers.size(); i++) {
            if (handcuffedPlayers.get(i).getId() == player.getId())
                return i;
        }
        return -1;
    }

    public static void sendChainedUpdateToAll(Level level) {
        MinecraftServer server = level.getServer();
        if (server != null)
            for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                    .hasNext();) {
                ServerPlayer member = iterator.next();
                ArrayList<Pair<Integer, Integer>> l = UpdateChainedPacket.ComplexToSimple(chainedPlayers);
                int[] a = UpdateChainedPacket.ListToGeneric(l, true);
                int[] b = UpdateChainedPacket.ListToGeneric(l, false);

                CuffedMod.NETWORK.sendToClient(
                        new UpdateChainedPacket(a, b, UpdateChainedPacket.ListToGeneric(handcuffedPlayers)), member);
            }
    }


    /**
     * Send a lockpicking packet to update the client's gui screen.
     * @param player (Player) The player to send to
     * @param tick (int) The ticks the player has been lockpicking
     */
    public static void sendLockpickUpdate(Player player, int lockId, int slot, int phases) {
        LockpickPacket packet = new LockpickPacket(0, lockId, slot, phases, player.getId());
        CuffedMod.NETWORK.sendToClientTracking(packet, player);
        CuffedMod.NETWORK.sendToClient(packet, (ServerPlayer) player);
    }


    /**
     * Send a lockpicking packet to finish lockpicking FROM a client TO the server.
     * @param code (int) The exit code of the lockpicking, 0 = timeExpire  1 = missedSweetSpot  2 = success
     * @param lockId (int) The id of the entity being lockpicked
     */
    public static void sendLockpickFinish(int code, int lockId, int playerId, UUID playerUUID) {
        LockpickPacket packet = new LockpickPacket(code, lockId, playerId, playerUUID.toString());
        CuffedMod.NETWORK.sendToServer(packet);
    }
}
