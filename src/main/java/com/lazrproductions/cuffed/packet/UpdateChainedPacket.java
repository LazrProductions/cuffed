package com.lazrproductions.cuffed.packet;

import java.util.ArrayList;

//import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.client.CuffedEventClient;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class UpdateChainedPacket extends CreativePacket {

    public int[] listA = new int[0];
    public int[] listB = new int[0];

    public UpdateChainedPacket(int[] a, int[] b) {
        //CuffedMod.LOGGER.info("Creating packet with size: " + a.length);
        this.listA = a;
        this.listB = b;
    }

    public UpdateChainedPacket() {
    }

    @Override
    public void executeClient(Player arg0) {
        //CuffedMod.LOGGER.info("[Server] - Receiving chained update packey\n-> with size: " + this.listA.length);
        CuffedEventClient.allChainedPlayers = GenericToList(listA, listB);
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
    }

    public static ArrayList<Pair<Integer, Integer>> GenericToList(int[] a, int[] b) {
        if (a.length != b.length)
            return null;

        ArrayList<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
        for (int i = 0; i < a.length; i++) {
            Pair<Integer, Integer> p = new Pair<Integer, Integer>(a[i], b[i]);
            l.add(p);
        }
        //CuffedMod.LOGGER.info("Converting simple arrays to list, length at start " + a.length + ", length at end " + l.size());
        return l;
    }

    public static int[] ListToGeneric(ArrayList<Pair<Integer, Integer>> list, boolean getFirst) {
        int[] a = new int[list.size()];
        for (int i = 0; i < a.length; i++) 
            if (getFirst)
                a[i] = list.get(i).getFirst();
            else
                a[i] = list.get(i).getSecond();
        //CuffedMod.LOGGER.info("Converting List to simple array(s), length at start " + list.size() + ", length at end " + a.length);
        return a;
    }

    public static ArrayList<Pair<Integer, Integer>> ComplexToSimple(ArrayList<Pair<Player, Entity>> list){
        ArrayList<Pair<Integer, Integer>> a = new ArrayList<Pair<Integer, Integer>>();
        for (int i = 0; i < list.size(); i++) {
            Pair<Integer,Integer> p = new Pair<Integer,Integer>(list.get(i).getFirst().getId(), list.get(i).getSecond().getId());
            a.add(p);
        }
        //CuffedMod.LOGGER.info("Converting complex array to simple, length at start " + list.size() + ", length at end " + a.size());
        return a;
    }
}
