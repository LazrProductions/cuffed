package com.lazrproductions.cuffed.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.lazrproductions.cuffed.client.ChainRenderHelper;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModClientEvents {

    public static int maxPhases;
    public static int pickingSlot;
    public static boolean isLockpicking;
    public static float lockpickTick;
    public static int pickingLock;
    int pickPhaseTick;
    int pickProgress;
    float pickSpeed = 1.4f;
    int curPhase = -1;

    float pickedLerpedProgress;

    @SubscribeEvent
    public void clientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getPlayer() != null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(event.getPlayer());
            cap.client_joinWorld(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void clientLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        isLockpicking = false;

        Minecraft inst = Minecraft.getInstance();
        Player player = inst.player;
        if (player != null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
            cap.client_leaveWorld(player);
        }
    }

    @SubscribeEvent
    public void clientInput(InputEvent.Key event) {
        Minecraft inst = Minecraft.getInstance();
        Player player = inst.player;
        if (player != null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
            cap.client_onKeyPressed(inst, event);
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            Minecraft inst = Minecraft.getInstance();
            LocalPlayer player = inst.player;
            if (player != null) {
                CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
                cap.client_tick(player);

                ClientLevel level = inst.level;

                if (level == null)
                    return;

                if (isLockpicking) {
                    player.getInventory().selected = pickingSlot;

                    if (pickProgress >= maxPhases) {
                        isLockpicking = false;
                        CuffedAPI.Lockpicking.sendLockpickFinishPacket(2, pickingLock, player.getId(),
                                player.getUUID()); // Success
                    }

                    lockpickTick += pickSpeed;

                    if (curPhase > pickProgress) {
                        isLockpicking = false;
                        CuffedAPI.Lockpicking.sendLockpickFinishPacket(0, pickingLock, player.getId(),
                                player.getUUID()); // Missed a phase and didnt click.
                        player.playSound(SoundEvents.ITEM_BREAK);
                    }

                    curPhase = Mth.floor(lockpickTick / 20);

                    if (lockpickTick > (20 * maxPhases)) {
                        isLockpicking = false;
                        CuffedAPI.Lockpicking.sendLockpickFinishPacket(0, pickingLock, player.getId(),
                                player.getUUID()); // Time ran out (or didnt get make complete enough phases)
                        player.playSound(SoundEvents.ITEM_BREAK);
                    }
                } else {
                    pickPhaseTick = 0;
                    pickProgress = 0;
                    lockpickTick = 0;
                    pickedLerpedProgress = 0;
                    maxPhases = 0;
                    pickSpeed = 1.4f;
                    curPhase = -1;
                }
            }
        }
    }

    @SubscribeEvent
    public void renderGUI(RenderGuiOverlayEvent.Post event) {
        Minecraft inst = Minecraft.getInstance();
        LocalPlayer p = inst.player;
        if (p != null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(p);
            cap.client_renderOverlay(inst, p, event.getGuiGraphics(), event.getPartialTick(), event.getWindow());

            if (isLockpicking) {
                pickedLerpedProgress = org.joml.Math.lerp(pickedLerpedProgress, (float) pickProgress * 20f,
                        event.getPartialTick() / 100);

                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("Picking lock"));
                list.add(Component.literal("" + curPhase + "/" + maxPhases));
                CuffedCapability.renderHandcuffedGUI(inst, event.getGuiGraphics(), list);

                CuffedCapability.renderLockpickGUI(inst, event.getGuiGraphics(), (20 * maxPhases),
                        Mth.floor((float) pickedLerpedProgress));
                pickPhaseTick = Mth.floor(lockpickTick) % 20;
                CuffedCapability.renderHandcuffedGUI(inst, event.getGuiGraphics(), 20, pickPhaseTick);
            }
        }
    }

    ///////// MISC CLIENT EVENTS FOR HANDCUFFED PLAYERS //////////

    @SubscribeEvent
    public void computeFov(ComputeFovModifierEvent event) {
        CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(event.getPlayer());
        if (cap.isGettingOrCurrentlyHandcuffed())
            event.setNewFovModifier(1);
    }

    @SubscribeEvent
    public void openInv(Opening event) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            Player player = inst.player;
            if (player != null) {
                CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
                if (cap.isGettingOrCurrentlyHandcuffed() || isLockpicking)
                    if (event.getScreen() instanceof InventoryScreen)
                        event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void click(InteractionKeyMappingTriggered event) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            Player player = inst.player;
            if (player != null) {
                CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
                if (cap.isGettingOrCurrentlyHandcuffed()) {
                    event.setCanceled(true); // TODO: figure out how to cancel better combat's events
                } else if (!player.isCreative()
                        && !player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES)
                        && event.isAttack()) {
                    BlockState pickresult = GetSelectedBlock(player, false);
                    if (pickresult != null
                            && (pickresult.is(ModBlocks.CELL_DOOR.get())
                                    || pickresult.is(ModBlocks.REINFORCED_STONE.get())
                                    || pickresult.is(ModBlocks.REINFORCED_STONE_CHISELED.get())
                                    || pickresult.is(ModBlocks.REINFORCED_STONE_SLAB.get())
                                    || pickresult.is(ModBlocks.REINFORCED_STONE_STAIRS.get())))
                        event.setCanceled(true);
                } else if (event.isUseItem() && !(inst.hitResult instanceof EntityHitResult)) {

                    BlockState pickresult = GetSelectedBlock(player, false);
                    BlockPos pickpos = GetSelectedBlockPos(player, false);
                    PadlockEntity padlock = PadlockEntity.getLockAt(player.level(), pickpos);

                    if (pickresult != null) {
                        boolean isLockedBlock = false;
                        if (pickresult.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
                            if (padlock != null && padlock.isLocked())
                                isLockedBlock = true;
                            else if (pickresult.getBlock() instanceof DoorBlock door) {
                                PadlockEntity eB = PadlockEntity.getLockAt(player.level(), pickpos.below());
                                PadlockEntity eA = PadlockEntity.getLockAt(player.level(), pickpos.above());
                                if (player.level().getBlockState(pickpos.below()).is(door) && eB != null
                                        && eB.isLocked())
                                    isLockedBlock = true;
                                else if (player.level().getBlockState(pickpos.above()).is(door) && eA != null
                                        && eA.isLocked())
                                    isLockedBlock = true;
                            }
                        }

                        if (isLockedBlock)
                            event.setCanceled(true);
                    }
                } else if (event.isAttack()) {
                    BlockState pickresult = GetSelectedBlock(player, false);
                    BlockPos pickpos = GetSelectedBlockPos(player, false);
                    PadlockEntity padlock = PadlockEntity.getLockAt(player.level(), pickpos);

                    if (pickresult != null) {
                        boolean isLockedBlock = false;
                        if (pickresult.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
                            if (padlock != null && padlock.isLocked())
                                isLockedBlock = true;
                            else if (pickresult.getBlock() instanceof DoorBlock door) {
                                PadlockEntity eB = PadlockEntity.getLockAt(player.level(), pickpos.below());
                                PadlockEntity eA = PadlockEntity.getLockAt(player.level(), pickpos.above());
                                if (player.level().getBlockState(pickpos.below()).is(door) && eB != null
                                        && eB.isLocked())
                                    isLockedBlock = true;
                                else if (player.level().getBlockState(pickpos.above()).is(door) && eA != null
                                        && eA.isLocked())
                                    isLockedBlock = true;
                            }
                        }

                        if (isLockedBlock)
                            event.setCanceled(true);
                    }
                }

                if (isLockpicking) {
                    if (pickPhaseTick <= 16 && pickPhaseTick >= 10) {
                        pickProgress++;
                        pickSpeed += ((float) CuffedMod.CONFIG.lockpickingSettings.lockpickingSpeedIncreasePerPhase)
                                / 100f;
                        player.playSound(SoundEvents.IRON_TRAPDOOR_OPEN);
                    } else {
                        CuffedAPI.Lockpicking.sendLockpickFinishPacket(1, pickingLock, player.getId(),
                                player.getUUID()); // missed sweet spot
                        player.playSound(SoundEvents.ITEM_BREAK);
                        isLockpicking = false;
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void scroll(InputEvent.MouseScrollingEvent event) {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null) {
            Player player = inst.player;
            if (player != null) {
                CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
                if (cap.isGettingOrCurrentlyHandcuffed() || isLockpicking)
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void interactBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockState pickresult = event.getLevel().getBlockState(event.getPos());
        BlockPos pickpos = event.getPos();
        PadlockEntity padlock = PadlockEntity.getLockAt(player.level(), pickpos);

        boolean isLockedBlock = false;
        if (pickresult.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
            if (padlock != null && padlock.isLocked())
                isLockedBlock = true;
            else if (pickresult.getBlock() instanceof DoorBlock door) {
                // Block is a door and does not have a lock on it
                PadlockEntity eB = PadlockEntity.getLockAt(player.level(), pickpos.below());
                PadlockEntity eA = PadlockEntity.getLockAt(player.level(), pickpos.above());
                if (player.level().getBlockState(pickpos.below()).is(door) && eB != null && eB.isLocked())
                    // Block is top part of door with a lock on bottom part
                    isLockedBlock = true;
                else if (player.level().getBlockState(pickpos.above()).is(door) && eA != null && eA.isLocked())
                    // Block is bottom part of door with a lock on top part
                    isLockedBlock = true;
            }
        }

        if (isLockedBlock)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent event) {
        Player player = event.getEntity();
        CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);

        Entity e = cap.getAnchor(player.level());
        if (e != null) {
            ChainRenderHelper.renderChainTo(player, event.getPartialTick(), event.getPoseStack(),
                    event.getMultiBufferSource(), e); // render chain from mayself to chained player.
        }
    
        Entity renderedPlayer = event.getEntity();
        Entity anchoredPlayer = null;
        for (Player p : renderedPlayer.level().players()) {
            CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(p);
            Entity a = c.getAnchor(renderedPlayer.level());
            if(c.isAnchored() && a != null && a.getId() == renderedPlayer.getId())
                anchoredPlayer = p;
        }
        if(anchoredPlayer != null) {
            ChainRenderHelper.renderChainFrom(anchoredPlayer, event.getPartialTick(), event.getPoseStack(),
                    event.getMultiBufferSource(), renderedPlayer);
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        PlayerRenderer render = event.getRenderer();
        PlayerModel<AbstractClientPlayer> model = render.getModel();

        if (player != null) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(player);
            if (cap.isAnchored()) {
                renderChainedOverlay(model, player, event);
            }
        }
    }

    Vec3 defaultScale = new Vec3(1, 1, 1);
    float oldRot = 0;

    private void renderChainedOverlay(PlayerModel<AbstractClientPlayer> model, Player player,
            RenderPlayerEvent event) {
        model.attackTime = 0;
        PoseStack matrix = event.getPoseStack();
        VertexConsumer buffer = event.getMultiBufferSource()
                .getBuffer(model.renderType(CuffedMod.CHAINED_OVERLAY_TEXTURE));
        int light = event.getPackedLight();
        int texture = OverlayTexture.NO_OVERLAY;

        ModelPart part = model.body;

        oldRot = Mth.clampedLerp(oldRot, player.yBodyRot, event.getPartialTick() / 1.1f);

        part.x = 0;
        part.y = player.isCrouching() ? 19.75F : 22.75F;
        part.z = 0;
        part.xRot = 3.14F; // - (player.xRotO/90)*1.2F; //-3.0F > -1.65F > -0.0F;
        part.yRot = (float) -Math.toRadians(oldRot); // + (float)Math.toRadians(180F);
        part.zRot = 0.0F;
        part.xScale = 1.1F;
        part.yScale = 1.1F;
        part.zScale = 1.1F;

        part.visible = true;

        part.render(matrix, buffer, light, texture);
        part.xScale = (float) defaultScale.x;
        part.yScale = (float) defaultScale.y;
        part.zScale = (float) defaultScale.z;
    }

    @SubscribeEvent
    public void chainKnotRender(RenderChainKnotEntityEvent event) {
        Entity e = event.getEntity();
        Player player = null;
        for (Player p : e.level().players()) {
            CuffedCapability cap = CuffedAPI.Capabilities.getCuffedCapability(p);
            Entity _e = cap.getAnchor(e.level());
            if (cap.isAnchored() && _e != null && _e.getId() == e.getId())
                player = p;
        }

        if (player != null)
            ChainRenderHelper.renderChainFrom(player, event.getPartialTick(), event.getPoseStack(),
                    event.getMultiBufferSource(), e);
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

    public static boolean IsTargettingEntity(Player player, boolean isFluid) {
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

    /**
     * Sends a debug message with handcuffed information about the current client in
     * the console.
     */
    public static void clientSideHandcuffedCommand() {
        Minecraft inst = Minecraft.getInstance();
        if (inst != null && inst.player != null) {
            CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(inst.player);
            CuffedMod.LOGGER.info("[COMMAND DEBUG] -> Handcuffed Information [CLIENT]:"
                    + "\n - isHandcuffed: " + c.isHandcuffed()
                    + "\n - isSoftCuffed: " + c.isSoftCuffed()
                    + "\n - isDetained: " + c.isDetained()
                    + "\n -  detainedRot: " + c.getDetainedRotation()
                    + "\n -  detainedPos: " + c.getDetainedPosition()
                    + "\n - isAnchored: " + c.isAnchored()
                    + "\n -  anchor: " + (c.getAnchor() != null ? c.getAnchor().getDisplayName().getString()
                            : "null"
                                    + "\n - nbt: " + c.serializeNBT().getAsString()));
        }
    }

    /**
     * Sends a debug message with handcuffed information about a client in the
     * console.
     */
    public static void clientSideHandcuffedCommand(UUID otherUUID) {
        Minecraft inst = Minecraft.getInstance();
        if (inst.level != null) {
            Player player = inst.level.getPlayerByUUID(otherUUID);
            if (inst != null && player != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                CuffedMod.LOGGER.info("[COMMAND DEBUG] -> Handcuffed Information [CLIENT]:"
                        + "\n - isHandcuffed: " + c.isHandcuffed()
                        + "\n - isSoftCuffed: " + c.isSoftCuffed()
                        + "\n - isDetained: " + c.isDetained()
                        + "\n -  detainedRot: " + c.getDetainedRotation()
                        + "\n -  detainedPos: " + c.getDetainedPosition()
                        + "\n - isAnchored: " + c.isAnchored()
                        + "\n -  anchor: "
                        + (c.getAnchor() != null ? c.getAnchor().getDisplayName().getString()
                                : "null"
                                        + "\n - nbt: " + c.serializeNBT().getAsString()));
            }
        }
    }
}
