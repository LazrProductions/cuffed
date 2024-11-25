package com.lazrproductions.cuffed.api;

import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.base.ILockableBlock;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.client.gui.screen.LockpickingScreen;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.packet.LockpickBlockPacket;
import com.lazrproductions.cuffed.packet.LockpickLockPacket;
import com.lazrproductions.cuffed.packet.LockpickRestraintPacket;
import com.lazrproductions.cuffed.packet.RestraintEquippedPacket;
import com.lazrproductions.cuffed.packet.RestraintSyncPacket;
import com.lazrproductions.cuffed.packet.RestraintUtilityPacket;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.network.LazrNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CuffedAPI {
    public static class Networking {

        public static final LazrNetwork NETWORK = new LazrNetwork(new ResourceLocation(CuffedMod.MODID, "main"), 1);


        public static void sendRestraintSyncPacket(@Nonnull ServerPlayer client) {
            IRestrainableCapability cap = Capabilities.getRestrainableCapability(client);
            RestraintSyncPacket packet = new RestraintSyncPacket(client.getId(), client.getUUID().toString(),
                    cap.serializeNBT());
            NETWORK.sendTo(packet, client); // sends the sync packet to the client we want.
        }
        

        public static void sendRestraintEquipPacket(@Nonnull ServerPlayer client, @Nullable ServerPlayer captor,
                RestraintType type, @Nullable AbstractRestraint newRestraint,
                @Nullable AbstractRestraint oldRestraint) {
            RestraintEquippedPacket packet = new RestraintEquippedPacket(client.getId(), client.getUUID().toString(),
                    type, oldRestraint != null ? oldRestraint.serializeNBT() : null,
                    newRestraint != null ? newRestraint.serializeNBT() : null,
                    captor != null ? captor.getUUID().toString() : "null");
            NETWORK.sendTo(packet, client); // sends the sync packet to the client we want.
        }


        public static void sendRestraintUtilityPacketToClient(ServerPlayer client, RestraintType restraintType,
                int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
            RestraintUtilityPacket packet = new RestraintUtilityPacket(restraintType.toInteger(),
                    utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
            NETWORK.sendTo(packet, client);
        }
        public static void sendRestraintUtilityPacketToServer(RestraintType restraintType, int utiltiyCode,
                int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
            RestraintUtilityPacket packet = new RestraintUtilityPacket(restraintType.toInteger(),
                    utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
            NETWORK.sendToServer(packet);
        }

        
        public static void sendLockpickFinishPickingLockPacketToServer(boolean wasFailed, int lockId, UUID playerUUID) {
            LockpickLockPacket packet = new LockpickLockPacket(wasFailed, lockId, playerUUID.toString());
            Networking.NETWORK.sendToServer(packet);
        }
        public static void sendLockpickFinishPickingRestraintPacketToServer(boolean wasFailed, String restrainedUUID, int restraintType, UUID playerUUID) {
            LockpickRestraintPacket packet = new LockpickRestraintPacket(wasFailed, restrainedUUID, restraintType, playerUUID.toString());
            Networking.NETWORK.sendToServer(packet);
        }
        public static void sendLockpickFinishPickingCellDoorPacketToServer(boolean wasFailed, BlockPos pos, UUID playerUUID) {
            LockpickBlockPacket packet = new LockpickBlockPacket(wasFailed, pos, playerUUID.toString());
            Networking.NETWORK.sendToServer(packet);
        }


        public static void sendLockpickBeginPickingLockPacketToClient(@Nonnull ServerPlayer player, int lockId, int speedIncreasePerPhase, int progressPerPick) {
            LockpickLockPacket packet = new LockpickLockPacket(lockId, speedIncreasePerPhase, progressPerPick, player.getUUID().toString());
            Networking.NETWORK.sendTo(packet, player);
        }
        public static void sendLockpickBeginPickingRestraintPacketToClient(@Nonnull ServerPlayer player, String restrainedUUID, int restraintType, int speedIncreasePerPhase, int progressPerPick) {
            LockpickRestraintPacket packet = new LockpickRestraintPacket(restrainedUUID, restraintType, speedIncreasePerPhase, progressPerPick, player.getUUID().toString());
            Networking.NETWORK.sendTo(packet, player);
        }
        public static void sendLockpickBeginPickingCellDoorPacketToClient(@Nonnull ServerPlayer player, BlockPos pos, int speedIncreasePerPhase, int progressPerPick) {
            LockpickBlockPacket packet = new LockpickBlockPacket(pos, speedIncreasePerPhase, progressPerPick, player.getUUID().toString());
            Networking.NETWORK.sendTo(packet, player);
        }


        public static void registerPackets() {
            NETWORK.registerPacket(LockpickLockPacket.class, LockpickLockPacket::new);
            NETWORK.registerPacket(LockpickRestraintPacket.class, LockpickRestraintPacket::new);
            NETWORK.registerPacket(LockpickBlockPacket.class, LockpickBlockPacket::new);

            NETWORK.registerPacket(RestraintSyncPacket.class, RestraintSyncPacket::new);
            NETWORK.registerPacket(RestraintEquippedPacket.class, RestraintEquippedPacket::new);
            NETWORK.registerPacket(RestraintUtilityPacket.class, RestraintUtilityPacket::new);
        }
    }

    public static class Lockpicking {
        public static void finishLockpickingLock(boolean wasFailed, int lockId, @Nonnull UUID lockpickerUUID) {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
            if (player != null) {
                Level level = player.level();
                if (level != null) {
                    if (!level.isClientSide()) {
                        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
                        player.getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 20);
                        if (wasFailed) {
                            itemstack.hurtAndBreak(1, player, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            player.awardStat(ModStatistics.LOCKPICKS_BROKEN.get());
                        } else {
                            level.playLocalSound((float) player.position().x, (float) player.position().y,
                                    (float) player.position().z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS,
                                    1, 1,
                                    true);

                            itemstack.hurtAndBreak(1, player, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });
                            if (level.getEntity(lockId) instanceof PadlockEntity e) {
                                player.awardStat(ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                                e.RemoveLock();
                            }
                        }
                    }
                }
            }
        }
        public static void finishLockpickingRestraint(boolean wasFailed, RestraintType restraintType, @Nonnull UUID restrainedPlayerUUID, @Nonnull UUID lockpickerUUID) {
            ServerPlayer lockpicker = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
            ServerPlayer restrained = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(restrainedPlayerUUID);
            if (lockpicker != null && restrained != null) {
                Level level = lockpicker.level();
                if (level != null && !level.isClientSide()) {
                        ItemStack itemstack = lockpicker.getItemInHand(InteractionHand.MAIN_HAND);
                        lockpicker.getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 20);
                        if (wasFailed) {
                            itemstack.hurtAndBreak(1, lockpicker, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            lockpicker.awardStat(ModStatistics.LOCKPICKS_BROKEN.get());
                        } else {
                            level.playLocalSound((float) restrained.position().x, (float) restrained.position().y,
                                    (float) restrained.position().z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS,
                                    1, 1,
                                    true);

                            itemstack.hurtAndBreak(1, lockpicker, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            lockpicker.awardStat(ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                            RestrainableCapability cap = (RestrainableCapability)Capabilities.getRestrainableCapability(restrained);
                            cap.TryUnequipRestraint(restrained, lockpicker, restraintType);
                        }
                    }
                }
            
        }
        public static void finishLockpickingCellDoor(boolean wasFailed, @Nonnull BlockPos pos, UUID lockpickerUUID) {
            ServerPlayer lockpicker = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
            if (lockpicker != null) {
                Level level = lockpicker.level();
                if (level != null) {
                    if (!level.isClientSide()) {
                        ItemStack itemstack = lockpicker.getItemInHand(InteractionHand.MAIN_HAND);
                        lockpicker.getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 20);
                        if (wasFailed) {
                            itemstack.hurtAndBreak(1, lockpicker, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            lockpicker.awardStat(ModStatistics.LOCKPICKS_BROKEN.get());
                        } else {
                            level.playLocalSound((float) lockpicker.position().x, (float) lockpicker.position().y,
                                    (float) lockpicker.position().z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS,
                                    1, 1,
                                    true);

                            lockpicker.awardStat(ModStatistics.SUCCESSFUL_LOCKPICKS.get());

                            itemstack.hurtAndBreak(1, lockpicker, (p) -> {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });
                            
                            level.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }         


        @OnlyIn(Dist.CLIENT)
        public static void beginLockpickingLock(@Nonnull Minecraft instance, int lockId, int speedIncreasePerPhase, int progressPerPick) {
            LockpickingScreen overlay = new LockpickingScreen(instance);
            overlay.speedIncreasePerPhase = speedIncreasePerPhase;
            overlay.progressPerPick = progressPerPick;
            
            overlay.type = 0;
            overlay.lockId = lockId;
            
            instance.setScreen(overlay);
        }
        @OnlyIn(Dist.CLIENT)
        public static void beginLockpickingRestraint(@Nonnull Minecraft instance, String restrainedUUID, int restraintType, int speedIncreasePerPhase, int progressPerPick) {
            LockpickingScreen overlay = new LockpickingScreen(instance);
            overlay.speedIncreasePerPhase = speedIncreasePerPhase;
            overlay.progressPerPick = progressPerPick;

            overlay.type = 1;
            overlay.restrainedUUID = restrainedUUID;
            overlay.restraintType = restraintType;
            
            instance.setScreen(overlay);
        }
        @OnlyIn(Dist.CLIENT)
        public static void beginLockpickingCellDoor(@Nonnull Minecraft instance, BlockPos pos, int speedIncreasePerPhase, int progressPerPick) {
            LockpickingScreen overlay = new LockpickingScreen(instance);
            overlay.speedIncreasePerPhase = speedIncreasePerPhase;
            overlay.progressPerPick = progressPerPick;

            overlay.type = 2;
            overlay.doorPos = pos;
            
            instance.setScreen(overlay);
        }
    
    
        public static boolean isLockedAt(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos) {
            boolean isLockedBlock = false;
            if (state.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
                PadlockEntity padlock = PadlockEntity.getLockAt(level, pos);
                if (padlock != null && padlock.isLocked())
                    isLockedBlock = true;

                if (state.getBlock() instanceof ILockableBlock)
                    if (ILockableBlock.isLocked(state))
                        isLockedBlock = true;
                
                if (state.getBlock() instanceof DoorBlock door) {
                    PadlockEntity eB = PadlockEntity.getLockAt(level, pos.below());
                    PadlockEntity eA = PadlockEntity.getLockAt(level, pos.above());
                    if (level.getBlockState(pos.below()).is(door) && eB != null && eB.isLocked())
                        isLockedBlock = true;
                    else if (level.getBlockState(pos.above()).is(door) && eA != null && eA.isLocked())
                        isLockedBlock = true;
                }

                if (state.getBlock() instanceof PilloryBlock pillory) {
                    PadlockEntity eB = PadlockEntity.getLockAt(level, pos.below());
                    PadlockEntity eA = PadlockEntity.getLockAt(level, pos.above());
                    if (level.getBlockState(pos.below()).is(pillory) && eB != null && eB.isLocked())
                        isLockedBlock = true;
                    else if (level.getBlockState(pos.above()).is(pillory) && eA != null && eA.isLocked())
                        isLockedBlock = true;
                }
                
                if (state.getBlock() instanceof ChestBlock) {
                    Direction dir = ChestBlock.getConnectedDirection(state);
                    BlockPos otherPos = pos.relative(dir);
                    PadlockEntity otherPadlock = PadlockEntity.getLockAt(level, otherPos);
                    if (level.getBlockState(otherPos).is(net.minecraft.world.level.block.Blocks.CHEST)
                            && otherPadlock != null && otherPadlock.isLocked())
                        isLockedBlock = true;
                } 
            }

            return isLockedBlock;
        }
    
        public static final HashMap<BlockPos, UUID> registeredLocks = new HashMap<>();

        public static void loadWorld() {
            registeredLocks.clear();

            // Load keys from world files
        }

        public static void saveWorld() {

        }
    }

    public static class Capabilities {
        public static final ResourceLocation RESTRAINABLE_CAPABILITY_NAME = new ResourceLocation(CuffedMod.MODID,
                "restrainable");
        public static final Capability<RestrainableCapability> RESTRAINABLE_CAPABILITY = CapabilityManager
                .get(new CapabilityToken<RestrainableCapability>() {
                });

        public static IRestrainableCapability getRestrainableCapability(Player player) {
            return player.getCapability(Capabilities.RESTRAINABLE_CAPABILITY).orElseGet(RestrainableCapability::new);
        }
    }

    public static class Privacy {
        // TODO: next update's problem
        // public static boolean attemptToAnchor(@Nonnull ServerPlayer player, @Nonnull ServerPlayer playerAnchoring) {
        //     if(playerAnchoring.hasPermissions(1))
        //         return true; // allow moderators and higher to bypass privacy restrictions
            
        //     IPrivacyOperand priv = (IPrivacyOperand)player;
        //     IRestrainableCapability cap = Capabilities.getRestrainableCapability(player);
        //     switch (priv.getAnchoringRestrictions()) {
        //         case NEVER:
        //             return true;
        //         case ONLY_WHEN_RESTRAINED:
        //             return cap.armsOrLegsRestrained();
        //         case ASK:
        //             // ask for permission
        //             return false;
        //         case ALWAYS:
        //             return false;
        //     }

        //     return false;
        // }


        // public static void askPlayerForPermission(@Nonnull ServerPlayer player) {

        // }
    }
}
