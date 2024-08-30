package com.lazrproductions.cuffed.event;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.cap.provider.RestrainableCapabilityProvider;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.CrumblingBlockEntity;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.PossessionsBox;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ModServerEvents {
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            if (!event.getObject().getCapability(CuffedAPI.Capabilities.RESTRAINABLE_CAPABILITY).isPresent())
                event.addCapability(CuffedAPI.Capabilities.RESTRAINABLE_CAPABILITY_NAME,
                        new RestrainableCapabilityProvider());
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            if (deadEntityRestraintData.containsKey(event.getEntity().getUUID())) {
                event.getEntity().getCapability(CuffedAPI.Capabilities.RESTRAINABLE_CAPABILITY).ifPresent(n -> {
                    n.copyFrom(deadEntityRestraintData.get(event.getEntity().getUUID()),
                            (ServerLevel) event.getEntity().level());
                    deadEntityRestraintData.remove(event.getEntity().getUUID());
                });
            }

            if (deadEntityNicknameData.containsKey(event.getEntity().getUUID())) {
                INicknamable nick = (INicknamable) event.getEntity();
                nick.deserializeNickname(deadEntityNicknameData.get(event.getEntity().getUUID()));
                deadEntityNicknameData.remove(event.getEntity().getUUID());
            }
        }
    }

    @SubscribeEvent
    public void tickServer(TickEvent.PlayerTickEvent event) {
        if (event.phase == Phase.END && event.side == LogicalSide.SERVER) {
            ServerPlayer p = (ServerPlayer) event.player;
            if (p != null) {
                // fix old player's attributes
                AttributeInstance a = p.getAttribute(Attributes.MOVEMENT_SPEED);
                if (a != null)
                    a.removeModifier(UUID.fromString("3b44d328-0746-45c9-85e3-c2df6c70d4a3"));

                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
                cap.tickServer(p);
            }
        }
    }

    @SubscribeEvent
    public void playerMineBlock(BreakEvent event) {
        BlockState pickresult = event.getState();
        if (pickresult.is(ModTags.Blocks.REINFORCED_BLOCKS))
            if (!event.getPlayer().isCreative()
                    && !event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES)) {
                event.setCanceled(true);
                return;
            }

        Level level = (Level) event.getLevel();
        BlockPos pickpos = event.getPos();

        IDetainableEntity detainableEntity = (IDetainableEntity) event.getPlayer();

        if (detainableEntity.getDetained() > -1) {
            event.setCanceled(true);
            return;
        }

        if (CuffedAPI.Lockpicking.isLockedAt(level, pickresult, pickpos))
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            Level level = event.getEntity().level();
            if (!level.isClientSide()) {

                Player interacting = event.getEntity();
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(interacting);

                IDetainableEntity detainableEntity = (IDetainableEntity) interacting;

                BlockPos pos = event.getPos();
                BlockState state = level.getBlockState(pos);

                if (detainableEntity.getDetained() > -1) {
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }

                ArrayList<IAnchorableEntity> entitiesAnchoredToInteractor = new ArrayList<IAnchorableEntity>(0);
                ServerLevel server = (ServerLevel) event.getLevel();
                if (server != null)
                    for (Iterator<Entity> iterator = server.getAllEntities().iterator(); iterator.hasNext();) {
                        if (iterator.next() instanceof IAnchorableEntity en)
                            if (en.isAnchored() && en.getAnchor().getUUID() == interacting.getUUID())
                                entitiesAnchoredToInteractor.add(en);
                    }

                if (((state.is(Blocks.FENCES)
                        && CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_FENCES.get())
                        || (state.is(net.minecraft.world.level.block.Blocks.TRIPWIRE_HOOK)
                                && CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS.get()))
                        && entitiesAnchoredToInteractor.size() > 0) {
                    for (int i = 0; i < entitiesAnchoredToInteractor.size(); i++)
                        ChainKnotEntity.bindEntityToNewOrExistingKnot(
                                (LivingEntity) entitiesAnchoredToInteractor.get(i), level, event.getPos());
                    event.setCanceled(true);
                    return;
                }

                if (state.is(ModBlocks.PILLORY.get())) {
                    if (level.getBlockState(pos.above()).is(ModBlocks.PILLORY.get()))
                        state = level.getBlockState(pos.above());

                    if (cap.getWhoImEscorting() != null) {
                        cap.getWhoImEscorting().moveTo(PilloryBlock.getPositionBehind(state, pos));
                        cap.stopEscortingPlayer();
                    }
                }

                if (state.is(ModTags.Blocks.REINFORCED_BLOCKS) && Block.isShapeFullBlock(state.getShape(level, pos))) {
                    ItemStack stack = event.getItemStack();
                    if (stack.is(ModItems.FORK.get()) || stack.is(ModItems.SPOON.get())) {
                        Random r = new Random();
                        if (r.nextFloat() < 0.25f)
                            CrumblingBlockEntity.crumbleBlock(level, pos, state, 1);

                        level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1f,
                                (r.nextFloat() * 0.2f) + 0.9f);

                        level.levelEvent(null, 2001, pos, Block.getId(state));

                        stack.hurtAndBreak(1, interacting, (f) -> {
                            f.broadcastBreakEvent(event.getHand());
                        });

                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void playerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() == LogicalSide.CLIENT)
            return;
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            if (event.getSide() == LogicalSide.SERVER) {
                ServerPlayer player = (ServerPlayer) event.getEntity();
                IRestrainableCapability myCap = CuffedAPI.Capabilities.getRestrainableCapability(player);
                IDetainableEntity detainableEntity = (IDetainableEntity) player;

                if (detainableEntity.getDetained() > -1) {
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }

                if (event.getTarget() instanceof ServerPlayer target) {
                    IRestrainableCapability targetCap = CuffedAPI.Capabilities.getRestrainableCapability(target);
                    double maxDist = player.getEyePosition().distanceTo(target.position());
                    Vec3 interactionPos = new Vec3(target.position().x, player.getLookAngle()
                            .multiply(new Vec3(maxDist, maxDist, maxDist)).add(player.getEyePosition()).y,
                            target.position().z);

                    if (event.getItemStack().is(ModItems.POSSESSIONSBOX.get()) && targetCap.armsRestrained()) {
                        PossessionsBox.frisk(player, target, event.getItemStack());
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                        return;
                    }

                    if (targetCap != null)
                        targetCap.onInteractedByOther(target, player, interactionPos.y - target.position().y,
                                event.getItemStack(), event.getHand());
                }

                if (event.getTarget().getType().is(ModTags.Entities.CHAINABLE_ENTITIES)) {
                    IAnchorableEntity anchorableEntity = (IAnchorableEntity) event.getTarget();

                    if(CuffedMod.SERVER_CONFIG.ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED.get()) {
                        if(event.getTarget() instanceof Player p) {
                            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
                            if(!cap.isRestrained())
                                return;
                        }
                    }

                    if (anchorableEntity.isAnchored()) {
                        if (player.getItemInHand(event.getHand()).is(Items.AIR)) {
                            anchorableEntity.setAnchoredTo(null);

                            player.level().playSound(null, event.getPos(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS,
                                    0.7f, 1);

                            event.setCancellationResult(InteractionResult.SUCCESS);
                            event.setCanceled(true);
                            return;
                        }
                    } else if (player.getItemInHand(event.getHand()).is(Items.CHAIN)) {
                        anchorableEntity.setAnchoredTo(player);
                        player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);

                        player.level().playSound(null, event.getPos(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS,
                                0.7f, 1);

                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                        return;
                    }

                }

                if (event.getTarget() instanceof Animal && myCap != null && myCap.getWhoImEscorting() != null) {
                    myCap.getWhoImEscorting().startRiding(event.getTarget());
                    player.sendSystemMessage(
                            Component.translatable("info.cuffed.forced_ride",
                                    myCap.getWhoImEscorting().getDisplayName(), event.getTarget().getDisplayName()),
                            true);
                    myCap.stopEscortingPlayer();
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    public void onPlayerDismount() {

    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer p = (ServerPlayer) event.getEntity();
        if (p != null) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
            cap.onLoginServer(p);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer p = (ServerPlayer) event.getEntity();
        if (p != null) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
            cap.onLogoutServer(p);
        }
    }

    HashMap<UUID, CompoundTag> deadEntityRestraintData = new HashMap<UUID, CompoundTag>();
    HashMap<UUID, String> deadEntityNicknameData = new HashMap<UUID, String>();

    @SubscribeEvent
    public void onEntityDied(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null) {
                cap.onDeathServer(player);

                deadEntityRestraintData.put(event.getEntity().getUUID(), cap.serializeNBT());
            }

            INicknamable nick = (INicknamable) player;
            if (CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_DEATH.get())
                deadEntityNicknameData.put(player.getUUID(), nick.serializeNickname());
        }
    }

    @SubscribeEvent
    public void onLand(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null)
                event.setDamageMultiplier(cap.onLandServer(player, event.getDistance(), event.getDamageMultiplier()));
        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null)
                cap.onJumpServer(player);
        }
    }

    @SubscribeEvent
    public void onLivingDamaged(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player captor && !event.getEntity().level().isClientSide()) {
            float originalAmount = event.getAmount();

            ServerLevel level = (ServerLevel) event.getEntity().level();
            MinecraftServer server = event.getEntity().level().getServer();
            if (server != null) {
                boolean activateImbue = true;

                ArrayList<Player> playersToTakeDamage = new ArrayList<>();
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(players.get(i));
                    AbstractArmRestraint arm = cap.getArmRestraint();
                    if (arm != null && arm.getCaptor(level) == captor)
                        playersToTakeDamage.add(players.get(i));
                    AbstractLegRestraint leg = cap.getLegRestraint();
                    if (leg != null && leg.getCaptor(level) == captor)
                        playersToTakeDamage.add(players.get(i));
                }

                if (activateImbue) {
                    float amountNegated = 0;
                    for (Player pl : playersToTakeDamage) {

                        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(pl);
                        if (cap != null && cap.armsRestrained()
                                && cap.getArmRestraint() instanceof IEnchantableRestraint e
                                && e.hasEnchantment(ModEnchantments.IMBUE.get())) {
                            int enchLevel = e.getEnchantmentLevel(ModEnchantments.IMBUE.get());
                            float percentage = ((float) enchLevel / 3f) * 0.8f;
                            amountNegated += (originalAmount * percentage);
                        }
                        if (cap != null && cap.legsRestrained()
                                && cap.getLegRestraint() instanceof IEnchantableRestraint e
                                && e.hasEnchantment(ModEnchantments.IMBUE.get())) {
                            int enchLevel = e.getEnchantmentLevel(ModEnchantments.IMBUE.get());
                            float percentage = ((float) enchLevel / 3f) * 0.8f;
                            amountNegated += (originalAmount * percentage);
                        }

                        // each restrained player takes a percentage of the total damage negated by
                        // imbue
                        pl.hurt(captor.damageSources().magic(), amountNegated / (float) playersToTakeDamage.size());
                    }

                    // CuffedMod.LOGGER.info("Imbue activated! -> original: " +originalAmount + ",
                    // negatedAmount: " + amountNegated + ", amountNegatedPerCaptive: " +
                    // (amountNegated / 1) + "finalDamageToTake: " + (originalAmount -
                    // amountNegated));
                    event.setAmount(Mth.clamp(originalAmount - (amountNegated), 0, originalAmount));
                }
            }
        }
    }
}