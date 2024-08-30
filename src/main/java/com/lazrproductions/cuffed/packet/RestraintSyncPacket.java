package com.lazrproductions.cuffed.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class RestraintSyncPacket extends ParameterizedLazrPacket {
    int playerId;
    String playerUUID;

    String data;

    public RestraintSyncPacket(int playerId, String uuid, CompoundTag data) {
        this(playerId, uuid, data.getAsString());
    }

    public RestraintSyncPacket(int playerId, String uuid, String data) {
        super(playerId, uuid, data);
        this.playerId = playerId;
        this.playerUUID = uuid;

        this.data = data;
    }
    public RestraintSyncPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void loadValues(Object[] arg0) {
        playerId = (int)arg0[0];
        playerUUID = (String)arg0[1];
        data = (String)arg0[2];
    }

    @Override
    public void handleClientside(Supplier<NetworkEvent.Context> ctx) {
        Clientside.handleClientside(ctx, playerId, playerUUID, data);
    }

    @Override
    public void handleServerside(Supplier<NetworkEvent.Context> ctx) {
    }

    static class Clientside {
        public static void handleClientside(Supplier<NetworkEvent.Context> ctx, int playerId, String playerUUID, String data) {
            Minecraft inst = Minecraft.getInstance();
            Player arg0 = inst.player;
            
            if(arg0 != null) {
                if (!data.equals("null")) {
                    try {
                        CompoundTag parsedTag = TagParser.parseTag(data);
                        Player p = arg0.getLevel().getPlayerByUUID(UUID.fromString(playerUUID));
                        if(p != null)
                            CuffedAPI.Capabilities.getRestrainableCapability(p).deserializeNBT(parsedTag);
                    } catch (CommandSyntaxException exception) {
                        CuffedMod.LOGGER.warn("NBT was received in incorrect format!");
                    }
                }
            }
        }
    }
}
