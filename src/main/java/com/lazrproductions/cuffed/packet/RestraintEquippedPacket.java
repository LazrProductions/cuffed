package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.restraints.Restraints;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * A packet to sync the equipping and unequipping of restraints to the
 * restrained client.
 */
public class RestraintEquippedPacket extends ParameterizedLazrPacket {

    int playerId;
    String playerUUID;

    int type;
    String oldData;
    String newData;

    String captorUUID;

    public RestraintEquippedPacket(int playerId, String playerUUID,
            @Nonnull RestraintType type, @Nullable CompoundTag oldData, @Nullable CompoundTag newData,
            String captorUUID) {
        super(playerId, playerUUID, type.toInteger(), oldData != null ? oldData.getAsString() : "null", newData != null ? newData.getAsString() : "null", captorUUID);
        
        this.playerId = playerId;
        this.playerUUID = playerUUID;

        this.type = type.toInteger();
        this.oldData = oldData != null ? oldData.getAsString() : "null";
        this.newData = newData != null ? newData.getAsString() : "null";
        
        this.captorUUID = captorUUID;
    }

    public RestraintEquippedPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void loadValues(Object[] arg0) {
        this.playerId = (int)arg0[0];
        this.playerUUID = (String)arg0[1];

        this.type = (int)arg0[2];

        this.oldData = (String)arg0[3];
        this.newData = (String)arg0[4];

        this.captorUUID = (String)arg0[5];
    }

    @Override
    public void handleClientside(@Nonnull Player arg0) {
        RestrainableCapability cap = (RestrainableCapability) CuffedAPI.Capabilities.getRestrainableCapability(arg0);

        try {
            CompoundTag newTag = newData.toLowerCase().equals("null") ? null : TagParser.parseTag(newData);
            CompoundTag oldTag = oldData.toLowerCase().equals("null") ? null : TagParser.parseTag(oldData);
            Player captor = !captorUUID.equals("null") ? arg0.level().getPlayerByUUID(UUID.fromString(captorUUID)) : null;

            if (type == 0) {
                // Sets the client-side version of the restraint AND sends onEquippedClient &
                // onUnequipped events
                if (!newData.toLowerCase().equals("null")) {
                    cap.armRestraint = (AbstractArmRestraint) Restraints.GetRestraintFromNBT(newTag);
                    cap.armRestraint.onEquippedClient(arg0, captor);
                } else
                    cap.armRestraint = null;

                if (!oldData.toLowerCase().equals("null")) {
                    AbstractArmRestraint oldRestraint = (AbstractArmRestraint) Restraints.GetRestraintFromNBT(oldTag);
                    oldRestraint.onUnequippedClient(arg0);
                }
            } else {
                // Sets the client-side version of the restraint AND sends onEquippedClient &
                // onUnequipped events
                if (!newData.toLowerCase().equals("null")) {
                    cap.legRestraint = (AbstractLegRestraint) Restraints.GetRestraintFromNBT(newTag);
                    cap.legRestraint.onEquippedClient(arg0, captor);
                } else
                    cap.legRestraint = null;
                if (!oldData.toLowerCase().equals("null")) {
                    AbstractLegRestraint oldRestraint = (AbstractLegRestraint) Restraints.GetRestraintFromNBT(oldTag);
                    oldRestraint.onUnequippedClient(arg0);
                }
            }
        } catch (CommandSyntaxException exception) {
            CuffedMod.LOGGER.warn("NBT was received in incorrect format!");
        }
    }

    @Override
    public void handleServerside(@Nonnull ServerPlayer arg0) {
    }

}
