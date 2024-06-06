package com.lazrproductions.cuffed.packet;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;

public class RestraintUtilityPacket extends CreativePacket {

    public int restraintType = 0;
    public int utiltiyCode = 0;

    public int integerArg;
    public boolean booleanArg;
    public double doubleArg;
    public String stringArg;

    public RestraintUtilityPacket() {
    }

    public RestraintUtilityPacket(int restraintType, int utiltiyCode) {
        this.restraintType = restraintType;
        this.utiltiyCode = utiltiyCode;
    }

    public RestraintUtilityPacket(int restraintType, int utiltiyCode, int iArg, boolean bArg, double dArg, String sArg) {
        this.restraintType = restraintType;
        this.utiltiyCode = utiltiyCode;

        this.integerArg = iArg;
        this.booleanArg = bArg;
        this.doubleArg = dArg;
        this.stringArg = sArg;
    }

    @Override
    public void executeClient(Player arg0) {
        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
        AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(restraintType));
        if(res != null) res.receiveUtilityPacketClient(arg0, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
    }

    @Override
    public void executeServer(ServerPlayer arg0) {
        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
        AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(restraintType));
        if(res != null) res.receiveUtilityPacketServer(arg0, utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
    }

}
