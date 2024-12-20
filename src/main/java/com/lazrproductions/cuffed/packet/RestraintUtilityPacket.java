package com.lazrproductions.cuffed.packet;

import java.util.function.Supplier;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.network.packet.ParameterizedLazrPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class RestraintUtilityPacket extends ParameterizedLazrPacket {

    int restraintType;
    int utiltiyCode;

    int integerArg;
    boolean booleanArg;
    double doubleArg;
    String stringArg;

    public RestraintUtilityPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public RestraintUtilityPacket(int restraintType, int utiltiyCode) {
        super(restraintType, utiltiyCode, 0, false, 0.0D, "");
        this.restraintType = restraintType;
        this.utiltiyCode = utiltiyCode;
    }

    public RestraintUtilityPacket(int restraintType, int utiltiyCode, int iArg, boolean bArg, double dArg, String sArg) {
        super(restraintType, utiltiyCode, iArg, bArg, dArg, sArg);
        this.restraintType = restraintType;
        this.utiltiyCode = utiltiyCode;

        this.integerArg = iArg;
        this.booleanArg = bArg;
        this.doubleArg = dArg;
        this.stringArg = sArg;
    }

    @Override
    public void loadValues(Object[] arg0) {
        restraintType = (int)arg0[0];
        utiltiyCode = (int)arg0[1];

        integerArg = (int)arg0[2];
        booleanArg = (Boolean)arg0[3];
        doubleArg = (Double)arg0[4];
        stringArg = (String)arg0[5];
    }

    @Override
    public void handleClientside(Supplier<NetworkEvent.Context> ctx) {
        Clientside.handleClientside(ctx, restraintType, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
    }

    @Override
    public void handleServerside(Supplier<NetworkEvent.Context> ctx) {
        Serverside.handleServerside(ctx, restraintType, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
    }

    static class Clientside {
        public static void handleClientside(Supplier<NetworkEvent.Context> ctx, int restraintType, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
            Minecraft inst = Minecraft.getInstance();
            Player arg0 = inst.player;
            
            if(arg0 != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
                AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(restraintType));
                if(res != null) res.receiveUtilityPacketClient(arg0, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
            }
        }
    }

    static class Serverside {
        public static void handleServerside(Supplier<NetworkEvent.Context> ctx, int restraintType, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
            ServerPlayer arg0 = ctx.get().getSender();
            
            if(arg0 != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
                AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(restraintType));
                if(res != null) res.receiveUtilityPacketServer(arg0, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
            }
        }
    }
}
