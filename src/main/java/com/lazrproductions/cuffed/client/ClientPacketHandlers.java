package com.lazrproductions.cuffed.client;

import java.util.UUID;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.*;
import com.lazrproductions.cuffed.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPacketHandlers {

    public static void handleRestraintSync(final RestraintSyncPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft inst = Minecraft.getInstance();
            Player localPlayer = inst.player;
            if (localPlayer != null) {
                Player p = localPlayer.level().getPlayerByUUID(UUID.fromString(payload.playerUUID));
                if (p != null && payload.data != null) {
                    CuffedAPI.Capabilities.getRestrainableCapability(p).deserializeNBT(p.level().registryAccess(), payload.data);
                }
            }
        });
    }

    public static void handleRestraintEquipped(final RestraintEquippedPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft inst = Minecraft.getInstance();
            Player arg0 = inst.player;
            if (arg0 != null) {
                RestrainableCapability cap = (RestrainableCapability) CuffedAPI.Capabilities.getRestrainableCapability(arg0);
                Player captor = !payload.captorUUID.equals("null") ? arg0.level().getPlayerByUUID(UUID.fromString(payload.captorUUID)) : null;

                net.minecraft.core.HolderLookup.Provider provider = arg0.level().registryAccess();

                if (payload.type == RestraintType.Arm.toInteger()) {
                    if (payload.newData != null) {
                        cap.armRestraint = (AbstractArmRestraint) RestraintAPI.getRestraintFromTag(provider, payload.newData);
                        if (cap.armRestraint != null) {
                            cap.armRestraint.onEquippedClient(arg0, captor);
                        }
                    } else {
                        cap.armRestraint = null;
                    }

                    if (payload.oldData != null) {
                        AbstractArmRestraint oldRestraint = (AbstractArmRestraint) RestraintAPI.getRestraintFromTag(provider, payload.oldData);
                        if (oldRestraint != null) {
                            oldRestraint.onUnequippedClient(arg0);
                        }
                    }
                } else if (payload.type == RestraintType.Leg.toInteger()) {
                    if (payload.newData != null) {
                        cap.legRestraint = (AbstractLegRestraint) RestraintAPI.getRestraintFromTag(provider, payload.newData);
                        if (cap.legRestraint != null) {
                            cap.legRestraint.onEquippedClient(arg0, captor);
                        }
                    } else {
                        cap.legRestraint = null;
                    }

                    if (payload.oldData != null) {
                        AbstractLegRestraint oldRestraint = (AbstractLegRestraint) RestraintAPI.getRestraintFromTag(provider, payload.oldData);
                        if (oldRestraint != null) {
                            oldRestraint.onUnequippedClient(arg0);
                        }
                    }
                } else if (payload.type == RestraintType.Head.toInteger()) {
                    if (payload.newData != null) {
                        cap.headRestraint = (AbstractHeadRestraint) RestraintAPI.getRestraintFromTag(provider, payload.newData);
                        if (cap.headRestraint != null) {
                            cap.headRestraint.onEquippedClient(arg0, captor);
                        }
                    } else {
                        cap.headRestraint = null;
                    }

                    if (payload.oldData != null) {
                        AbstractHeadRestraint oldRestraint = (AbstractHeadRestraint) RestraintAPI.getRestraintFromTag(provider, payload.oldData);
                        if (oldRestraint != null) {
                            oldRestraint.onUnequippedClient(arg0);
                        }
                    }
                }
            }
        });
    }

    public static void handleRestraintUtility(final RestraintUtilityPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft inst = Minecraft.getInstance();
            Player arg0 = inst.player;
            if (arg0 != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(arg0);
                AbstractRestraint res = cap.getRestraint(RestraintType.fromInteger(payload.restraintType));
                if (res != null) {
                    res.receiveUtilityPacketClient(arg0, payload.utiltiyCode, payload.integerArg, payload.booleanArg, payload.doubleArg, payload.stringArg);
                }
            }
        });
    }

    public static void handleLockpickBlock(final LockpickBlockPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.stopCode <= -1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingCellDoor(instance, new BlockPos(payload.x, payload.y, payload.z), payload.speedIncreasePerPick, payload.progressPerPick);
            }
        });
    }

    public static void handleLockpickLock(final LockpickLockPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.stopCode <= -1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingLock(instance, payload.lockId, payload.speedIncreasePerPick, payload.progressPerPick);
            }
        });
    }

    public static void handleLockpickRestraint(final LockpickRestraintPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.stopCode <= -1) {
                Minecraft instance = Minecraft.getInstance();
                CuffedAPI.Lockpicking.beginLockpickingRestraint(instance, payload.restrainedUUID, payload.restraintType, payload.speedIncreasePerPick, payload.progressPerPick);
            }
        });
    }
}
