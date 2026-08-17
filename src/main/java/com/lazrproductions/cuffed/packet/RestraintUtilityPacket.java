package com.lazrproductions.cuffed.packet;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RestraintUtilityPacket implements CustomPacketPayload {
    public static final Type<RestraintUtilityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "restraint_utility"));

    public static final StreamCodec<FriendlyByteBuf, RestraintUtilityPacket> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeInt(val.restraintType);
            buf.writeInt(val.utiltiyCode);
            buf.writeInt(val.integerArg);
            buf.writeBoolean(val.booleanArg);
            buf.writeDouble(val.doubleArg);
            buf.writeUtf(val.stringArg);
        },
        buf -> new RestraintUtilityPacket(
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readDouble(),
            buf.readUtf()
        )
    );

    public final int restraintType;
    public final int utiltiyCode;
    public final int integerArg;
    public final boolean booleanArg;
    public final double doubleArg;
    public final String stringArg;

    public RestraintUtilityPacket(int restraintType, int utiltiyCode) {
        this(restraintType, utiltiyCode, 0, false, 0.0D, "");
    }

    public RestraintUtilityPacket(int restraintType, int utiltiyCode, int iArg, boolean bArg, double dArg, String sArg) {
        this.restraintType = restraintType;
        this.utiltiyCode = utiltiyCode;
        this.integerArg = iArg;
        this.booleanArg = bArg;
        this.doubleArg = dArg;
        this.stringArg = sArg != null ? sArg : "";
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleServer(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer arg0) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
                AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(restraintType));
                if (res != null) {
                    res.receiveUtilityPacketServer(arg0, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
                }
            }
        });
    }
}
