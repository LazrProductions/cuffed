package com.lazrproductions.cuffed.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.cap.Handcuffed;
import com.lazrproductions.cuffed.config.ModCommonConfigs;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.PossessionsBox;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CuffedEventServer {

    public static boolean isCuffedActive(Entity player) {
        return true;
    }

    int suffocateAir;
    int suffocateTick;
    boolean isSuffocating;
    boolean wasHanging;

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.phase == Phase.START && event.side == LogicalSide.SERVER && isCuffedActive(event.player)) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (!player.isAlive())
                return;
            IHandcuffed handcuffed = CuffedServer.getHandcuffed(player);
            handcuffed.SetServerPlayer((ServerPlayer) event.player);

            AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (handcuffed.isGettingOrCurrentlyCuffed()) {

                if (!handcuffed.isSoftCuffed()) {
                    if (playerAtt != null && !playerAtt.hasModifier(CuffedMod.HANDCUFFED_ATTIRBUTE))
                        playerAtt.addPermanentModifier(CuffedMod.HANDCUFFED_ATTIRBUTE);
                } else {
                    if (playerAtt != null && playerAtt.hasModifier(CuffedMod.HANDCUFFED_ATTIRBUTE))
                        playerAtt.removeModifier(CuffedMod.HANDCUFFED_ATTIRBUTE);
                }

                handcuffed.tick(player);

                if (handcuffed.uncuffed())
                    CuffedServer.removeHandcuffs(player);
            } else {
                if (playerAtt != null && playerAtt.hasModifier(CuffedMod.HANDCUFFED_ATTIRBUTE))
                    playerAtt.removeModifier(CuffedMod.HANDCUFFED_ATTIRBUTE);
                wasHanging = false;
            }
        }

    }

    @SubscribeEvent
    public void playerLeave(PlayerLoggedOutEvent event) {
        IHandcuffed handcuffed = CuffedServer.getHandcuffed(event.getEntity());
        if (isCuffedActive(event.getEntity()) && handcuffed.isGettingOrCurrentlyCuffed()) {
            CuffedServer.removeHandcuffs(event.getEntity());

            Level level = event.getEntity().level();
            Player player = event.getEntity();

            ItemEntity itementity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(),
                    new ItemStack(ModItems.HANDCUFFS.get()));
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);

            if (!level.isClientSide)
                CuffedServer.removePlayerAsHelper(player);

            AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (playerAtt != null && playerAtt.hasModifier(CuffedMod.HANDCUFFED_ATTIRBUTE))
                playerAtt.removeModifier(CuffedMod.HANDCUFFED_ATTIRBUTE);
        }
    }

    int hadJustSofted = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide) {

            if (event.getHand() == InteractionHand.OFF_HAND)
                return;

            hadJustSofted--;
            Player interacting = event.getEntity();

            ArrayList<IHandcuffed> isAnchorTo = new ArrayList<IHandcuffed>(0);
            MinecraftServer server = interacting.getServer();
            if (server != null)
                for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                        .hasNext();) {
                    ServerPlayer member = iterator.next();
                    IHandcuffed cuffed = CuffedServer.getHandcuffed(member);
                    if (cuffed.isChained() && cuffed.getAnchor().getUUID() == interacting.getUUID())
                        isAnchorTo.add(cuffed);
                }

            if ((level.getBlockState(event.getPos()).is(Blocks.FENCES)
                    || level.getBlockState(event.getPos()).is(net.minecraft.world.level.block.Blocks.TRIPWIRE_HOOK))
                    && isAnchorTo.size() > 0) {
                for (int i = 0; i < isAnchorTo.size(); i++) {
                    isAnchorTo.get(i).setAnchor(ChainKnotEntity.getOrCreateKnot(level, event.getPos()), true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide) {

            if (event.getTarget() instanceof Player) {
                if (event.getHand() == InteractionHand.OFF_HAND)
                    return;

                hadJustSofted--;
                Player target = (Player) event.getTarget();
                Player interacting = event.getEntity();
                IHandcuffed handcuffed = CuffedServer.getHandcuffed(target);

                if (handcuffed.isHandcuffed()) {
                    event.setCanceled(true);

                    if (!handcuffed.isChained()) {
                        if (interacting.getMainHandItem().is(ModItems.HANDCUFFS_KEY.get())) {
                            // remove Handcuffed player's handcuffs
                            CuffedServer.removeHandcuffs(target);
                            interacting.addItem(new ItemStack(ModItems.HANDCUFFS.get(), 1));
                        } else if (interacting.getMainHandItem().is(Items.CHAIN)) {
                            handcuffed.setAnchor(interacting);
                            interacting.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                        } else if (interacting.getMainHandItem().is(ModItems.POSSESSIONSBOX.get())) {
                            ((PossessionsBox) interacting.getMainHandItem().getItem())
                                    .FillFromInventory(interacting.getMainHandItem(), target.getInventory(), true);
                        } else if (event.getEntity().getItemInHand(event.getHand()).is(ModItems.LOCKPICK.get())) {
                            if (!event.getEntity().getCooldowns().isOnCooldown(ModItems.LOCKPICK.get())) {
                                // can lockpick so begin lockpick
                                event.getEntity().getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 4 * 20);
                                CuffedServer.sendLockpickUpdate(event.getEntity(), target.getId(),
                                        event.getEntity().getInventory().selected,
                                        ModCommonConfigs.BREAK_HANDCUFFS_PHASES.get());
                            }
                        } else if (interacting.getMainHandItem().isEmpty()) {
                            if (interacting.isCrouching()) {
                                if (hadJustSofted <= 0) {
                                    handcuffed.setSoftCuffed(!handcuffed.isSoftCuffed());
                                    hadJustSofted = 2;
                                }
                            }
                        }
                    } else {
                        handcuffed.setAnchor(null);
                    }
                } else {
                    if (interacting.getMainHandItem().is(ModItems.HANDCUFFS.get())
                            && interacting.getCooldowns().getCooldownPercent(ModItems.HANDCUFFS.get(), 20) <= 0) {
                        handcuffed.setCuffingPlayer(interacting);
                    }
                }
            } else if (event.getTarget() instanceof PadlockEntity entity) {
                if (event.getEntity().getItemInHand(event.getHand()).is(ModItems.LOCKPICK.get())) {
                    if (!event.getEntity().getCooldowns().isOnCooldown(ModItems.LOCKPICK.get())) {
                        event.getEntity().getCooldowns().addCooldown(ModItems.LOCKPICK.get(),
                                (20 * (entity.isReinforced() ? ModCommonConfigs.BREAK_REINFORCED_PADLOCK_PHASES.get()
                                        : ModCommonConfigs.BREAK_PADLOCK_PHASES.get())));
                        CuffedServer.sendLockpickUpdate(event.getEntity(), entity.getId(),
                                event.getEntity().getInventory().selected,
                                entity.isReinforced() ? ModCommonConfigs.BREAK_REINFORCED_PADLOCK_PHASES.get()
                                        : ModCommonConfigs.BREAK_PADLOCK_PHASES.get());
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDied(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && isCuffedActive(event.getEntity())) {
            Level level = event.getEntity().level();

            CuffedServer.removeHandcuffs(player);

            if (!level.isClientSide) {
                CuffedServer.removePlayerAsHelper(player);

                AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (playerAtt != null && playerAtt.hasModifier(CuffedMod.HANDCUFFED_ATTIRBUTE))
                    playerAtt.removeModifier(CuffedMod.HANDCUFFED_ATTIRBUTE);

                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(),
                        new ItemStack(ModItems.HANDCUFFS.get()));
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(CuffedMod.HANDCUFFED_NAME, new ICapabilityProvider() {

                private LazyOptional<IHandcuffed> cuffed = LazyOptional.of(Handcuffed::new);

                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                    return CuffedMod.HANDCUFFED.orEmpty(cap, cuffed);
                }
            });

    }

    @SubscribeEvent
    public void playerMineBlock(BreakEvent event) {
        if (event.getState().is(ModBlocks.CELL_DOOR.get()))
            if (!event.getPlayer().isCreative()
                    && !event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES))
                event.setCanceled(true);

        Level level = (Level) event.getLevel();
        BlockPos pickpos = event.getPos();
        BlockState pickresult = event.getState();
        PadlockEntity padlock = PadlockEntity.getLockAt(level, pickpos);

        boolean isLockedBlock = false;
        if (pickresult.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
            if (padlock != null && padlock.isLocked())
                isLockedBlock = true;
            else if (pickresult.getBlock() instanceof DoorBlock door) {
                PadlockEntity eB = PadlockEntity.getLockAt(level, pickpos.below());
                PadlockEntity eA = PadlockEntity.getLockAt(level, pickpos.above());
                if (level.getBlockState(pickpos.below()).is(door) && eB != null && eB.isLocked())
                    isLockedBlock = true;
                else if (level.getBlockState(pickpos.above()).is(door) && eA != null && eA.isLocked())
                    isLockedBlock = true;
            }
        }

        if (isLockedBlock)
            event.setCanceled(true);
    }

    public static void FinishLockpicking(int code, int lockId, int playerId, UUID playerUUID) {
        ServerPlayer pl = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerUUID);
        if(pl != null) {
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
                            } else {
                                // has completed lockpicking
                                level.playLocalSound((float) player.position().x, (float) player.position().y,
                                        (float) player.position().z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1, 1,
                                        true);

                                itemstack.hurtAndBreak(1, player, (p) -> {
                                    p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                                });
                                if (level.getEntity(lockId) instanceof PadlockEntity e)
                                    e.RemoveLock();
                                else if (level.getEntity(lockId) instanceof Player e) {
                                    IHandcuffed cuffed = CuffedServer.getHandcuffed(e);
                                    if (cuffed.isHandcuffed())
                                        cuffed.removeHandcuffs();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}