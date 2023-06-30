package com.lazrproductions.cuffed.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.events.RenderChainKnotEntityEvent;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ScreenEvent.Opening;

@OnlyIn(Dist.CLIENT)
public class CuffedEventClient {

    public static ArrayList<Pair<Integer, Integer>> allChainedPlayers = new ArrayList<Pair<Integer, Integer>>(0);

    public static Minecraft mc = Minecraft.getInstance();

    public static UUID handcuffer;
    public static boolean showGraphic;
    public static boolean isCuffed;
    public static boolean isBeingCuffed;
    public static boolean isSoftCuffed;
    public static boolean isChained;
    public static int anchor;
    Entity _anchor;
    public static float progress;
    Player _player;

    public static void renderCuffedGUI(GuiGraphics graphics, List<Component> list) {
        int space = 15;
        int width = 0;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            width = Math.max(width, mc.font.width(text) + 10);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            graphics.drawString(mc.font, text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2,
                    mc.getWindow()
                            .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)) - 32,
                    16579836);
        }
        RenderSystem.enableDepthTest();
    }

    public static void renderCuffedGUI(GuiGraphics graphics, int maxTick, int curTick) {
        int screenCenterX = mc.getWindow().getGuiScaledWidth() / 2;
        int screenCenterY = mc.getWindow().getGuiScaledHeight() / 2;

        int totalFrames = 21;
        int barScale = 64;

        int curFrame = 0;

        if (curTick > maxTick)
            curTick = maxTick;

        if (curTick == 0)
            curTick = 1;
        if (maxTick == 0)
            maxTick = 42;

        if (curTick != 0 && maxTick != 0) {
            /*
             * Total frames is 21
             * the total ticks is 42 ticks (by default)
             * 
             * which means that animation should be 2 frames per tick.
             * calculated with: total ticks / total frames.
             * 
             * frame 11 is the start of the "safe zone"
             * frame 16 is the final frame of the "safe zone"
             * 
             * meaning that,
             * the "safe zone" starts at 21 ticks
             * ( total ticks / ( total frames / start "safe zone" frame ) )
             * int safeStart = (int)Math.floor(maxTick / (totalFrames / 11));*
             * the "safe zone" ends after 32 ticks
             * ( total ticks / ( total frames / start "safe zone" frame ) )
             * int safeEnd = (int)Math.floor(maxTick / (totalFrames / 16));*
             * 
             * The current frame should be:
             * total frames / ( total ticks / currentTick )
             */float temp = ((float) maxTick / (float) curTick);
            /*
            */ if (temp < 1f)
                temp = 1f;
            /*
            */curFrame = (int) Math.floor((float) totalFrames / temp); /*
                                                                                        * */
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        if (curFrame > CuffedMod.BREAKCUFFS_GUI.length - 1)
            curFrame = CuffedMod.BREAKCUFFS_GUI.length - 1;

        ResourceLocation rl1 = CuffedMod.BREAKCUFFS_GUI[curFrame];

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

        graphics.blitInscribed(rl1, screenCenterX - (barScale / 2), screenCenterY - (barScale / 2), barScale, barScale,
                64, 64, true, true); // bar

        RenderSystem.enableDepthTest();
    }

    private boolean addedEffect = false;

    @SubscribeEvent
     public void clientTick(ClientTickEvent event) {
        Level level = mc.level;
        if (level == null)
            return;

        _anchor = level.getEntity(anchor);

        // CuffedMod.LOGGER.info("receiving update packet values on cleint
        // :)\nhandcuffer -> " + handcuffer+"\nshowGraphic -> " +
        // showGraphic+"\nisCuffed -> " + isCuffed + "\nisBeingCuffed -> " +
        // isBeingCuffed + "\nisChained -> "+isChained+"\nanchor -> " + _anchor +
        // "\nprogress -> " + progress);

        if (event.phase == Phase.END) {
            Player player = mc.player;
            if (player != null) {
                if (showGraphic) {
                    player.getInventory().selected = -100;
                } else if (player.getInventory().selected < 0)
                    player.getInventory().selected = 0;

                /// Handle chained physics
                double maxDist = 5.0; // TODO: make config value for maximum chain length.

                if (isCuffed && isChained) {
                    if (_anchor != null) {
                        if (player.distanceTo(_anchor) > maxDist) {
                            float distance = player.distanceTo(_anchor);

                            double dx = (_anchor.getX() - player.getX()) / (double) distance;
                            double dy = (_anchor.getY() - player.getY()) / (double) distance;
                            double dz = (_anchor.getZ() - player.getZ()) / (double) distance;

                            player.setDeltaMovement(
                                    Math.copySign(dx * dx * (distance / 5D) * .45, dx),
                                    Math.copySign(dy * dy * (distance / 5D) * .45, dy),
                                    Math.copySign(dz * dz * (distance / 5D) * .45, dz));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void openInv(Opening event) {
        if (showGraphic)
            if (event.getScreen() instanceof InventoryScreen)
                event.setCanceled(true);

    }

    @SubscribeEvent
    public void computeFov(ComputeFovModifierEvent event) {
        if (isCuffed || isBeingCuffed)
            event.setNewFovModifier(1);
    }

    @SubscribeEvent
    public void click(InteractionKeyMappingTriggered event) {
        Player player = mc.player;
        if (player != null) {
            if (isCuffed || isBeingCuffed)
                event.setCanceled(false);
            else if (!player.isCreative() && !player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES)
                    && event.isAttack()) {
                BlockState pickresult = GetSelectedBlock(player, false);
                if (pickresult != null && pickresult.is(ModBlocks.CELL_DOOR.get()))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void scroll(InputEvent.MouseScrollingEvent event) {
        if (showGraphic)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent event) {
        Player player = mc.player;
        if (player != null) {
            Level level = event.getEntity().level();
            for (int i = 0; i < allChainedPlayers.size(); i++) {
                Player p = (Player) level.getEntity((int) allChainedPlayers.get(i).getFirst());
                Entity e = level.getEntity((int) allChainedPlayers.get(i).getSecond());
                int pI = (int) allChainedPlayers.get(i).getFirst();
                int eI = (int) allChainedPlayers.get(i).getSecond();
                if (p != null && e != null) {
                    if (event.getEntity().getId() == pI && player.getId() == eI) // is looking at a player who is chained to this player
                        ChainRenderHelper.renderChainTo(p, event.getPartialTick(), event.getPoseStack(),
                                event.getMultiBufferSource(), e); // render chain from mayself to chained player.
                    if (event.getEntity().getId() == eI && player.getId() != eI) // is the player that a player is chained to
                        ChainRenderHelper.renderChainFrom(p, event.getPartialTick(), event.getPoseStack(),
                                event.getMultiBufferSource(), e); // render chain from chain holder to chained player.
                }
            }
        }
    
        player = event.getEntity();
        
        if(player != null){
            event.getRenderer().getModel().rightArm.xRot=90;
            event.getRenderer().getModel().swimAmount=0;
            event.getRenderer().getModel().rightArm.visible=true;
            //event.getRenderer().getEntityModel().bipedRightArm.showModel=false;
        }
    }

    @SubscribeEvent
    public void renderHandcuffedAnimationPost(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        PlayerRenderer render = event.getRenderer();
        PlayerModel<AbstractClientPlayer> model = render.getModel();
        
        if(player != null){
            event.getRenderer().getModel().rightArm.visible = false;
            //model.rightArm.visible = false;
        }
    }


        @SubscribeEvent
    public void renderHandcuffedAnimationPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        PlayerRenderer render = event.getRenderer();
        PlayerModel<AbstractClientPlayer> model = render.getModel();
        
        if(player != null){
            ModelPart arm = model.rightArm;
            arm.xRot=90;
            //model.swimAnimation=0;
            
            PoseStack matrix = event.getPoseStack();
            VertexConsumer buffer = event.getMultiBufferSource().getBuffer(model.renderType(((AbstractClientPlayer)player).getSkinTextureLocation()));
            int light = event.getPackedLight();
            int texture = OverlayTexture.NO_OVERLAY;
            
            model.rightArm.copyFrom(arm);
            model.rightArm.visible=true;    
            arm.render(matrix, buffer, light, texture);;
        }
    }

    @SubscribeEvent
    public void chainKnotRender(RenderChainKnotEntityEvent event) {
        // CuffedMod.LOGGER.info("Poopers -> " + allChainedPlayers.size());

        Level level = event.getEntity().level();
        for (int i = 0; i < allChainedPlayers.size(); i++) {
            Player p = (Player) level.getEntity((int) allChainedPlayers.get(i).getFirst());
            Entity e = level.getEntity((int) allChainedPlayers.get(i).getSecond());
            int eI = (int) allChainedPlayers.get(i).getSecond();
            if (p != null && e != null)
                if (event.getEntity().getId() == eI)
                    ChainRenderHelper.renderChainFrom(p, event.getPartialTick(), event.getPoseStack(),
                            event.getMultiBufferSource(), e);
        }
    }

    @SubscribeEvent
    public void renderGui(RenderGuiOverlayEvent.Post event) {
        Player player = mc.player;

        // CuffedMod.LOGGER
        // .info("receiving update packet values on cleint :)\nhandcuffer -> " +
        // handcuffer + "\nshowGraphic -> "
        // + showGraphic + "\nisCuffed -> " + isCuffed + "\nisBeingCuffed -> " +
        // isBeingCuffed
        // + "\nisChained -> " + isChained + "\nanchor -> " + _anchor + "\nprogress -> "
        // + progress);

        if (player != null) {
            if (addedEffect) {
                player.removeEffect(MobEffects.JUMP);
                addedEffect = false;
            }

            if (isBeingCuffed || isCuffed) {
                if (progress < 42) {
                    renderCuffedGUI(event.getGuiGraphics(), 42, (int) Math.floor(progress));

                    if (isBeingCuffed && !mc.options.hideGui && mc.screen == null) {
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("You are getting handcuffed!"));
                        list.add(Component.literal("" + Math.round((progress / 42f) * 100f) + "/100"));
                        renderCuffedGUI(event.getGuiGraphics(), list);
                    }
                } else {
                    if (!isSoftCuffed)
                        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 0, -10));

                    player.hurtTime = 0;
                    addedEffect = true;

                    if (!mc.options.hideGui && mc.screen == null) {
                        List<Component> list = new ArrayList<>();
                        list.add(Component.translatable("You are Handcuffed"));
                        renderCuffedGUI(event.getGuiGraphics(), list);
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public void clientLeave(PlayerLoggedOutEvent event) {
        handcuffer = null;
        showGraphic = false;
        isCuffed = false;
        isBeingCuffed = false;
        isSoftCuffed = false;
        isChained = false;
        anchor = -1;
        _anchor = null;
        progress = 0;
        _player = null;
    }

    public static BlockState GetSelectedBlock(Player player, boolean isFluid) {

        HitResult block = player.pick(20.0D, 0.0F, isFluid);

        if (block.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) block).getBlockPos();
            return player.level().getBlockState(blockpos);
        }
        return null;
    }

    public static boolean isLocalPlayer(Player player) {
        // CuffedMod.LOGGER.info("given -> " + player +"\nlocal -> " + mc.player);
        if (player == null)
            return false;
        LocalPlayer localPlayer = mc.player;
        if (localPlayer == null)
            return false;
        return player.getUUID() == localPlayer.getUUID();
    }
}