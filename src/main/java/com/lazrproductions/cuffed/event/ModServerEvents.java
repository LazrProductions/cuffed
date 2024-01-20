package com.lazrproductions.cuffed.event;

import java.util.ArrayList;
import java.util.Iterator;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.lazrproductions.cuffed.cap.provider.CuffedCapabilityProvider;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModAttributes;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.PossessionsBox;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ModServerEvents {
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(CuffedAPI.Capabilities.CUFFED).isPresent()) {
                event.addCapability(CuffedAPI.Capabilities.CUFFED_NAME, new CuffedCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(CuffedAPI.Capabilities.CUFFED).ifPresent(oldStore -> {
                event.getOriginal().getCapability(CuffedAPI.Capabilities.CUFFED).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public void joinServer(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer p = (ServerPlayer)event.getEntity();
        if(p!=null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(p);
            cap.server_joinWorld(p);
        }
    }

    public void leaveServer(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer)event.getEntity();

        CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
        cap.server_leaveWorld(player);

        AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
            playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
    }

    @SubscribeEvent
    public void tickServer(TickEvent.PlayerTickEvent event) {
        if(event.phase == Phase.END && event.side == LogicalSide.SERVER) {
            ServerPlayer p = (ServerPlayer)event.player;
            if(p!=null) {
                CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(p);
                cap.server_tick(p);
            }
        }
    }

    @SubscribeEvent
    public void playerMineBlock(BreakEvent event) {
        BlockState pickresult = event.getState();
        if ((pickresult.is(ModBlocks.CELL_DOOR.get())
            || pickresult.is(ModBlocks.REINFORCED_STONE.get())
            || pickresult.is(ModBlocks.REINFORCED_STONE_CHISELED.get())
            || pickresult.is(ModBlocks.REINFORCED_STONE_SLAB.get())
            || pickresult.is(ModBlocks.REINFORCED_STONE_STAIRS.get())
            || pickresult.is(ModBlocks.REINFORCED_BARS.get())))
            if (!event.getPlayer().isCreative()
                    && !event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES))
                event.setCanceled(true);

        Level level = (Level) event.getLevel();
        BlockPos pickpos = event.getPos();
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

    int hadJustSofted = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide) {

            if (event.getHand() == InteractionHand.OFF_HAND)
                return;

            hadJustSofted--;
            Player interacting = event.getEntity();

            ArrayList<CuffedCapability> isAnchorForPlayer = new ArrayList<CuffedCapability>(0);
            MinecraftServer server = interacting.getServer();
            if (server != null)
                for (Iterator<ServerPlayer> iterator = server.getPlayerList().getPlayers().iterator(); iterator
                        .hasNext();) {
                    ServerPlayer member = iterator.next();
                    CuffedCapability cuffed = CuffedAPI.Capabilities.getCuffedCapability(member);
                    if (cuffed.isAnchored() && cuffed.getAnchor().getUUID() == interacting.getUUID())
                        isAnchorForPlayer.add(cuffed);
                }

            if ((level.getBlockState(event.getPos()).is(Blocks.FENCES)
                    || level.getBlockState(event.getPos()).is(net.minecraft.world.level.block.Blocks.TRIPWIRE_HOOK))
                    && isAnchorForPlayer.size() > 0) {
                for (int i = 0; i < isAnchorForPlayer.size(); i++) {
                    isAnchorForPlayer.get(i).server_setAnchor(ChainKnotEntity.getOrCreateKnot(level, event.getPos()), true);
                }
                event.setCanceled(true);
            }
        }
    }

    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide) {

            if (event.getTarget() instanceof Player target) {
                if (event.getHand() == InteractionHand.OFF_HAND)
                    return;

                hadJustSofted--;
                Player interacting = event.getEntity();
                CuffedCapability handcuffed = CuffedAPI.Capabilities.getCuffedCapability(target);

                if (handcuffed.isHandcuffed()) {
                    event.setCanceled(true);

                    if (!handcuffed.isAnchored()) {
                        if (interacting.getMainHandItem().is(ModItems.HANDCUFFS_KEY.get())) {
                            if(handcuffed.isDetained() < 0) {
                                // remove Handcuffed player's handcuffs
                                CuffedAPI.Handcuffing.removeHandcuffs((ServerPlayer)target);
                            }
                            interacting.awardStat(Stats.ITEM_USED.get(ModItems.HANDCUFFS_KEY.get()));
                        } else if (interacting.getMainHandItem().is(Items.CHAIN)) {
                            interacting.awardStat(Stats.ITEM_USED.get(Items.CHAIN));
                            handcuffed.server_setAnchor(interacting);
                            interacting.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                        } else if (interacting.getMainHandItem().is(ModItems.POSSESSIONSBOX.get())) {
                            ((PossessionsBox) interacting.getMainHandItem().getItem())
                                    .FillFromInventory(interacting.getMainHandItem(), target.getInventory(), true);
                            interacting.awardStat(Stats.ITEM_USED.get(ModItems.POSSESSIONSBOX.get()));
                        } else if (event.getEntity().getItemInHand(event.getHand()).is(ModItems.LOCKPICK.get())) {
                            if (!event.getEntity().getCooldowns().isOnCooldown(ModItems.LOCKPICK.get()) && handcuffed.isDetained() < 0) {
                                // can lockpick so begin lockpick
                                event.getEntity().getCooldowns().addCooldown(ModItems.LOCKPICK.get(), 4 * 20);

                                event.getEntity().awardStat(Stats.ITEM_USED.get(ModItems.LOCKPICK.get()));

                                CuffedAPI.Lockpicking.sendLockpickUpdatePacket(event.getEntity(), target.getId(),
                                    event.getEntity().getInventory().selected,
                                    CuffedMod.CONFIG.lockpickingSettings.lockpickingPhasesForBreakingHandcuffs);
                            }
                        } else if (interacting.getMainHandItem().getItem().getFoodProperties(interacting.getMainHandItem(), interacting).getNutrition() > 0) {
                            if(target.getFoodData().needsFood()) {
                                target.eat(level, interacting.getMainHandItem());
                            }
                        
                        }else if (interacting.getMainHandItem().isEmpty()) {
                            if (interacting.isCrouching()) {
                                if (hadJustSofted <= 0) {
                                    handcuffed.server_setSoftCuffed(!handcuffed.isSoftCuffed());
                                    hadJustSofted = 2;
                                }
                            }
                        }
                    } else {
                        handcuffed.server_setAnchor(null);
                    }
                } else if(!handcuffed.isGettingHandcuffed()) {
                    if (interacting.getMainHandItem().is(ModItems.HANDCUFFS.get())
                            && interacting.getCooldowns().getCooldownPercent(ModItems.HANDCUFFS.get(), 20) <= 0) {
                        interacting.getCooldowns().addCooldown(ModItems.HANDCUFFS.get(), Mth.floor(42f / CuffedMod.CONFIG.handcuffSettings.interuptPhaseSpeed));
                        handcuffed.server_setCuffingPlayer(interacting);
                    }
                }
            } else if (event.getTarget() instanceof PadlockEntity entity) {
                if (event.getEntity().getItemInHand(event.getHand()).is(ModItems.LOCKPICK.get())) {
                    if (!event.getEntity().getCooldowns().isOnCooldown(ModItems.LOCKPICK.get())) {
                        event.getEntity().getCooldowns().addCooldown(ModItems.LOCKPICK.get(),
                                (20 * (entity.isReinforced() ? CuffedMod.CONFIG.lockpickingSettings.lockpickingPhasesForBreakingReinforcedPadlocks
                                        : CuffedMod.CONFIG.lockpickingSettings.lockpickingPhasesForBreakingPadlocks)));
                        event.getEntity().awardStat(Stats.ITEM_USED.get(ModItems.LOCKPICK.get()));
                        CuffedAPI.Lockpicking.sendLockpickUpdatePacket(event.getEntity(), entity.getId(),
                                event.getEntity().getInventory().selected,
                                entity.isReinforced() ? CuffedMod.CONFIG.lockpickingSettings.lockpickingPhasesForBreakingReinforcedPadlocks
                                        : CuffedMod.CONFIG.lockpickingSettings.lockpickingPhasesForBreakingPadlocks);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDied(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CuffedCapability handcuffed = CuffedAPI.Capabilities.getCuffedCapability(player);
            if (handcuffed.isGettingOrCurrentlyHandcuffed()) {
                CuffedAPI.Handcuffing.removeHandcuffs(player);

                // ItemEntity itementity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(),
                //         new ItemStack(ModItems.HANDCUFFS.get()));
                // itementity.setDefaultPickUpDelay();
                // level.addFreshEntity(itementity);

                AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
                    playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
            }
        }
    }
}
