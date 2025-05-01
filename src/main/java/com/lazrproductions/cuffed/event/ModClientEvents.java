package com.lazrproductions.cuffed.event;

import java.util.Random;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.client.gui.screen.GenericScreen;
import com.lazrproductions.cuffed.effect.RestrainedEffectInstance;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.restraints.custom.DuckTapeHeadRestraint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModClientEvents {

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            Minecraft inst = Minecraft.getInstance();
            if (inst.screen instanceof GenericScreen sc)
                sc.tick();

            if (inst.player != null) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(inst.player);
                cap.tickClient(inst.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player p = (Player) event.getEntity();
        if (p != null) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
            cap.onLoginClient(p);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player p = event.getEntity();
        if (p != null) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
            cap.onLogoutClient(p);
        }
    }

    @SubscribeEvent
    public void onEntityDied(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null)
                cap.onDeathClient(player);
        }
    }

    @SubscribeEvent
    public void onLand(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null)
                cap.onLandClient(player, event.getDistance(), event.getDamageMultiplier());
        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            if (cap != null)
                cap.onJumpClient(player);

            IRestrainableEntity res = (IRestrainableEntity) player;
            if (RestrainedEffectInstance.decodeNoJumping(res.getRestraintCode())) {
                if (player.isSprinting() && RestrainedEffectInstance.decodeNoMovement(res.getRestraintCode())) {
                    player.setDeltaMovement(0, -1, 0);
                } else
                    player.setDeltaMovement(player.getDeltaMovement().x, -1, player.getDeltaMovement().z);
            }
        }
    }

    @SubscribeEvent
    public void renderGUI(RenderGuiOverlayEvent.Post event) {
        Minecraft inst = Minecraft.getInstance();
        LocalPlayer p = inst.player;
        if (p != null) {
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
            cap.renderOverlay(p, event.getGuiGraphics(), event.getPartialTick(), event.getWindow());
        }
    }

    @SubscribeEvent
    public void chat(ClientChatEvent event) {
        Minecraft instance = Minecraft.getInstance();
        if(instance.player instanceof IRestrainableEntity e)
            if(e.getHeadRestraintId().equals(DuckTapeHeadRestraint.ID))
                event.setMessage(mufflifyPhrase(event.getMessage()));
    }

    static final String[] variants = new String[] { "mph", "mhm", "hmm", "fmp", "mpr", "mrp" };

    String mufflifyPhrase(String message) {
        String[] words = message.split(" ");

        String output = "";
        for (int i = 0; i < words.length; i++) {
            output += mufflifyWord(words[i]);
            if(i < words.length - 1)
                output += " ";
        }
        return output;
    }
    String mufflifyWord(String word) {
        String myVariant = variants[new Random().nextInt(3)];

        String output = "";
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if(Character.isLetter(c)) {
                if(i == word.length()-1)
                    output += myVariant.charAt(2);
                else if(i == word.length()-2)
                    output += myVariant.charAt(1);
                else 
                    output += myVariant.charAt(0);
            } else 
            output += c;
        }
        return output;
    }

    ///////// MISC CLIENT EVENTS FOR HANDCUFFED PLAYERS //////////

    @SubscribeEvent
    public void computeFov(ComputeFovModifierEvent event) {
        IRestrainableEntity e = (IRestrainableEntity) event.getPlayer();
        if (RestrainedEffectInstance.decodeNoMovement(e.getRestraintCode()))
            event.setNewFovModifier(1);
    }

    @SubscribeEvent
    public void onOpenScreen(Opening event) {
        if (event.getNewScreen() instanceof InventoryScreen) {
            Minecraft inst = Minecraft.getInstance();
            if (inst != null) {
                Player player = inst.player;
                if (player != null) {
                    IRestrainableEntity restrainable = (IRestrainableEntity) player;
                    if (RestrainedEffectInstance.decodeNoItemUse(restrainable.getRestraintCode()))
                        event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteractionKeyMappingTriggered(InteractionKeyMappingTriggered event) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            Player player = inst.player;
            if (player != null) {
                IRestrainableEntity restrainable = (IRestrainableEntity) player;
                if (restrainable.isRestrained()) {
                    if (!event.isAttack()) {
                        if (RestrainedEffectInstance.decodeNoItemUse(restrainable.getRestraintCode()))
                            event.setCanceled(true);
                    } else if (RestrainedEffectInstance.decodeNoMining(restrainable.getRestraintCode()))
                        event.setCanceled(true);
                }
                ItemStack handItem  =player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!player.isCreative()
                        && !handItem.is(ItemTags.PICKAXES)
                        && event.isAttack()) {
                    BlockState pickresult = GetSelectedBlock(player, false);
                    if (pickresult != null && pickresult.is(ModTags.Blocks.REINFORCED_BLOCKS) && !(inst.hitResult instanceof EntityHitResult)) {
                            event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            Player player = inst.player;
            if (player != null) {
                IRestrainableEntity restrainable = (IRestrainableEntity) player;
                if (restrainable.isRestrained()) {
                    if (RestrainedEffectInstance.decodeNoItemUse(restrainable.getRestraintCode()))
                        event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent event) {
        // Player player = event.getEntity();
        // CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);

        // Entity e = cap.getAnchor(player.level());
        // if (e != null) {
        // ChainRenderHelper.renderChainTo(player, event.getPartialTick(),
        // event.getPoseStack(),
        // event.getMultiBufferSource(), e); // render chain from mayself to chained
        // player.
        // }

        // Entity renderedPlayer = event.getEntity();
        // Entity anchoredPlayer = null;
        // for (Player p : renderedPlayer.level().players()) {
        // CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(p);
        // Entity a = c.getAnchor(renderedPlayer.level());
        // if(c.isAnchored() && a != null && a.getId() == renderedPlayer.getId())
        // anchoredPlayer = p;
        // }
        // if(anchoredPlayer != null) {
        // ChainRenderHelper.renderChainFrom(anchoredPlayer, event.getPartialTick(),
        // event.getPoseStack(),
        // event.getMultiBufferSource(), renderedPlayer);
        // }
    }

    ////////// UTILITY FUNCTIONS FOR GENERAL USE /////////////
    
    public static BlockState GetSelectedBlock(Player player, boolean isFluid) {

        HitResult block = player.pick(20.0D, 0.0F, isFluid);

        if (block.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) block).getBlockPos();
            return player.level().getBlockState(blockpos);
        }
        return null;
    }

    public static BlockPos GetSelectedBlockPos(Player player, boolean isFluid) {
        HitResult block = player.pick(20.0D, 0.0F, isFluid);

        if (block.getType() == HitResult.Type.BLOCK)
            return ((BlockHitResult) block).getBlockPos();

        return null;
    }

    public static boolean isTargettingEntity(Player player, boolean isFluid) {
        HitResult block = player.pick(20.0D, 0.0F, isFluid);

        return block.getType() == HitResult.Type.ENTITY;
    }

    public static boolean isLocalPlayer(Player player) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            if (player == null)
                return false;
            LocalPlayer localPlayer = inst.player;
            if (localPlayer == null)
                return false;
            return player.getUUID() == localPlayer.getUUID();
        }

        return false;
    }
}
