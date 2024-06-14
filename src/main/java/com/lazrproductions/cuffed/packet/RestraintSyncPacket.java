package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class RestraintSyncPacket extends CreativePacket {
    public int playerId;
    public String playerUUID;

    public String data = "null";

    public RestraintSyncPacket(int playerId, String uuid, CompoundTag data) {
        this.playerId = playerId;
        this.playerUUID = uuid;

        this.data = data.getAsString();
    }

    public RestraintSyncPacket(int playerId, String uuid, String data) {
        this.playerId = playerId;
        this.playerUUID = uuid;

        this.data = data;
    }

    public RestraintSyncPacket() {
    }

    @Override
    public void executeClient(Player arg0) {
        if (!data.equals("null")) {
            try {
                CompoundTag parsedTag = TagParser.parseTag(data);
                Player p = arg0.level().getPlayerByUUID(UUID.fromString(playerUUID));
                if(p != null)
                    CuffedAPI.Capabilities.getRestrainableCapability(p).deserializeNBT(parsedTag);
            } catch (CommandSyntaxException exception) {
                CuffedMod.LOGGER.warn("NBT was received in incorrect format!");
            }
        }
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        if (!data.equals("null")) {
        }
    }
}
