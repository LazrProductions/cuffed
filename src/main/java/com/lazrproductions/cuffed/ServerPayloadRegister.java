package com.lazrproductions.cuffed;

import com.lazrproductions.cuffed.packet.*;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ServerPayloadRegister {
    public static void register(final PayloadRegistrar registrar) {
        registrar.playToClient(
            RestraintSyncPacket.TYPE,
            RestraintSyncPacket.STREAM_CODEC,
            (payload, context) -> {}
        );

        registrar.playToClient(
            RestraintEquippedPacket.TYPE,
            RestraintEquippedPacket.STREAM_CODEC,
            (payload, context) -> {}
        );

        registrar.playBidirectional(
            RestraintUtilityPacket.TYPE,
            RestraintUtilityPacket.STREAM_CODEC,
            (payload, context) -> {
                if (context.flow().isServerbound()) {
                    payload.handleServer(context);
                }
            }
        );

        registrar.playBidirectional(
            LockpickBlockPacket.TYPE,
            LockpickBlockPacket.STREAM_CODEC,
            (payload, context) -> {
                if (context.flow().isServerbound()) {
                    payload.handleServer(context);
                }
            }
        );

        registrar.playBidirectional(
            LockpickLockPacket.TYPE,
            LockpickLockPacket.STREAM_CODEC,
            (payload, context) -> {
                if (context.flow().isServerbound()) {
                    payload.handleServer(context);
                }
            }
        );

        registrar.playBidirectional(
            LockpickRestraintPacket.TYPE,
            LockpickRestraintPacket.STREAM_CODEC,
            (payload, context) -> {
                if (context.flow().isServerbound()) {
                    payload.handleServer(context);
                }
            }
        );
    }
}
