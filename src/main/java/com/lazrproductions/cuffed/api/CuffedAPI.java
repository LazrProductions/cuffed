package com.lazrproductions.cuffed.api;

import java.util.List;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.event.PlayerUncuffedEvent;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.packet.CuffedBreakOutPacket;
import com.lazrproductions.cuffed.packet.CuffedDebugPacket;
import com.lazrproductions.cuffed.packet.CuffedSyncPacket;
import com.lazrproductions.cuffed.packet.LockpickPacket;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.server.ServerLifecycleHooks;
import team.creative.creativecore.common.network.CreativeNetwork;

public class CuffedAPI {
    //#region//////////// NETWORKING ////////////

    public static final CreativeNetwork NETWORK = new CreativeNetwork(1, CuffedMod.LOGGER, 
        new ResourceLocation(CuffedMod.MODID, "main"));

    /**
     * Send a cuffed sync packet to the specified client, synchronizing server-side cuffed data to that client.
     * @param playerId The ID of the player in the world
     * @param playerUUID The player's UUID
     * @param data The data to synchronize
     */
    public static void sendCuffedSyncPacketToClient(int playerId, UUID playerUUID, CompoundTag data) {
        CuffedSyncPacket packet = new CuffedSyncPacket(playerId, playerUUID.toString(), data);
        NETWORK.sendToClientAll(ServerLifecycleHooks.getCurrentServer(), packet);
    }

    /**
     * Send a packet for each player in the server to the given player with the information of all player's cuffed data. Does not sync this client's data to themself.
     * @param player The player to sync to.
     */
    public static void syncAllOthersToClient(ServerPlayer player) {
        for (ServerPlayer p : player.serverLevel().players()) {
            if(p.getId()!=player.getId()) {
                CuffedCapability c = Capabilities.getCuffedCapability(p);
                CuffedSyncPacket packet = new CuffedSyncPacket(p.getId(), player.getUUID().toString(), c.serializeNBT());
                NETWORK.sendToClient(packet, player); //send a packet about the player "p" to the player "player"   
            }
        }
    }

    /**
     * Send a debug packet to the given client
     * @param client (ServerPlayer) The client to send the packet to
     */
    public static void sendCuffedDebugPacketToClient(ServerPlayer client) {
        CuffedDebugPacket packet = new CuffedDebugPacket();
        NETWORK.sendToClient(packet, client);
    }
    /**
     * Send a debug packet about a player with the given UUID to the given client
     * @param client (ServerPlayer) The client to send the packet to
     * @param other (UUID) The UUID of the other player to get information from
     */
    public static void sendCuffedDebugPacketToClient(ServerPlayer client, UUID other) {
        CuffedDebugPacket packet = new CuffedDebugPacket(other.toString());
        NETWORK.sendToClient(packet, client);
    }

    /**
     * Send a break out packet to the server to adjust the break progress from the client
     * @param breakProgress (int) the new value of breakProgress to set
     */
    public static void sendCuffedBreakOutPacketToServer(int breakProgress) {
        CuffedBreakOutPacket packet = new CuffedBreakOutPacket(breakProgress);
        NETWORK.sendToServer(packet);
    }


    /**
     * Registyer all packets on the network
     */
    public static void registerPackets() {
        NETWORK.registerType(CuffedSyncPacket.class, CuffedSyncPacket::new);
        NETWORK.registerType(LockpickPacket.class, LockpickPacket::new);
        NETWORK.registerType(CuffedDebugPacket.class, CuffedDebugPacket::new);
        NETWORK.registerType(CuffedBreakOutPacket.class, CuffedBreakOutPacket::new);
    }

    //#endregion//////////////////////////////////

    public static class Handcuffing {
        public static List<Pair<Integer, String>> allHandcuffedPlayers;
        public static List<Pair<Integer, Integer>> allAnchoredPlayers;

        public static void removeHandcuffs(Player player) {
            CuffedCapability uncuff = Capabilities.getCuffedCapability(player);

            // spawn a handcuffs item
            if(uncuff.isHandcuffed()) {
                ItemEntity itementity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), new ItemStack(ModItems.HANDCUFFS.get()));
                itementity.setDefaultPickUpDelay();
                player.level().addFreshEntity(itementity);
            }

            uncuff.server_removeHandcuffs();
            MinecraftForge.EVENT_BUS.post(new PlayerUncuffedEvent(player, uncuff));
        }
    }

    public static class Lockpicking {
        /**
         * Send a lockpicking packet to update the client's gui screen.
         * @param player (Player) The player to send to
         * @param tick (int) The ticks the player has been lockpicking
         */
        public static void sendLockpickUpdatePacket(Player player, int lockId, int slot, int phases) {
            LockpickPacket packet = new LockpickPacket(0, lockId, slot, phases, player.getId());
            NETWORK.sendToClientTracking(packet, player);
            NETWORK.sendToClient(packet, (ServerPlayer) player);
        }

        /**
         * Send a lockpicking packet to finish lockpicking FROM a client TO the server.
         * @param code (int) The exit code of the lockpicking, 0 = timeExpire  1 = missedSweetSpot  2 = success
         * @param lockId (int) The id of the entity being lockpicked
         */
        public static void sendLockpickFinishPacket(int code, int lockId, int playerId, UUID playerUUID) {
            LockpickPacket packet = new LockpickPacket(code, lockId, playerId, playerUUID.toString());
            NETWORK.sendToServer(packet);
        }

        public static void FinishLockpicking(int code, int lockId, int playerId, UUID playerUUID) {
            ServerPlayer pl = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerUUID);
            if (pl != null) {
                Level l = pl.level();
                if (l != null) {
                    Player player = (Player) l.getEntity(playerId);
                    if (player != null) {
                        Level level = player.level();
                        if (level != null) {
                            if (!level.isClientSide) {
                                ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
                                player.getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 20);
                                if (code <= 1) {
                                    // has failed lockpicking
                                    itemstack.hurtAndBreak(1, player, (p) -> {
                                        p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                                    });

                                    player.awardStat(ModStatistics.LOCKPICKS_BROKEN.get());
                                } else {
                                    // has completed lockpicking
                                    level.playLocalSound((float) player.position().x, (float) player.position().y,
                                            (float) player.position().z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1, 1,
                                            true);

                                    player.awardStat(ModStatistics.LOCKPICKS_BROKEN.get());

                                    itemstack.hurtAndBreak(1, player, (p) -> {
                                        p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                                    });
                                    if (level.getEntity(lockId) instanceof PadlockEntity e) {
                                        player.awardStat(ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                                        e.RemoveLock();
                                    } else if (level.getEntity(lockId) instanceof Player e) {
                                        CuffedCapability cuffed = CuffedAPI.Capabilities.getCuffedCapability(e);
                                        if (cuffed.isHandcuffed()) {
                                            CuffedAPI.Handcuffing.removeHandcuffs(e);
                                            player.awardStat(ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Capabilities {
        public static final ResourceLocation CUFFED_NAME = new ResourceLocation(CuffedMod.MODID, "handcuffed");
        public static final Capability<CuffedCapability> CUFFED = CapabilityManager.get(new CapabilityToken<CuffedCapability>() {});

        public static CuffedCapability getCuffedCapability(Player player) {
            return player.getCapability(Capabilities.CUFFED).orElseGet(CuffedCapability::new);
        }
    }
}
