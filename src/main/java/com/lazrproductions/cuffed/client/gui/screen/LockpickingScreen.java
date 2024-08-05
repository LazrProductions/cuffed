package com.lazrproductions.cuffed.client.gui.screen;

import javax.annotation.Nonnull;

import org.joml.Random;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockpickingScreen extends GenericScreen{

    static final float TARGET_BUFFER = 5;

    public float secondsPerPhase = 1;
    public int speedIncreasePerPhase = 10;
    public int progressPerPick = 8;


    public int type = 0;


    public int lockId = -1; // used only when picking a padlock

    public String restrainedUUID; // used only when picking a restrained player
    public int restraintType;
    
    public BlockPos doorPos = null; // used only when picking a cell door


    int currentPhase = 0;

    float ticksLeftToPick = 20;
    float currentTargetPick = 0;

    public LockpickingScreen(Minecraft instance) {
        super(instance);
        startLockpicking();
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(minecraft!=null)
            renderContent(minecraft, graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    float animationTick;
    boolean isAnimating = false;

    float lerpedGhostAngle = 0;

    public void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        if(!isAnimating) {
            if(ticksLeftToPick > 0) {
                if(ticksLeftToPick>=40) {
                    completeLockpicking(instance);   
                }
                ticksLeftToPick -= ((currentPhase+1) * (speedIncreasePerPhase / 200f));
            } else 
                failLockpicking(instance);
        }  
        else {
            animationTick -= partialTick;
            if(ticksLeftToPick < 0 && animationTick % 2 == 0)
            {
                LocalPlayer player = instance. player;
                if(player!=null)
                    player.playSound(SoundEvents.CHAIN_PLACE, 1f, 0.7f);
            }
            if(animationTick <= 0) {
                animationTick = 0;
                isAnimating = false;
            }
        }



        int centerScreenX = graphics.guiWidth() / 2;
        int centerScreenY = graphics.guiHeight() / 2;

        // SHAKE ANIMATION
        int shakeOffset = Mth.floor((Mth.sin((float)animationTick * 2) - 0.5f) * 3);
        //




        int scale = 3;
        int originalSize = 32;
        int finalSize = originalSize * scale;
        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(centerScreenX - (finalSize/2) + shakeOffset, centerScreenY - (16 * scale), finalSize, finalSize), 
            new ScreenTexture(new ResourceLocation(CuffedMod.MODID, "textures/item/padlock.png"), 0, 0, 16, 16, 16, 16));

        
        // draw time left bar
        ScreenUtilities.drawGenericProgressBarUpright(graphics, 
            new BlitCoordinates(centerScreenX - finalSize, Mth.floor(centerScreenY - (finalSize / 2f)), 
            4, finalSize), (ticksLeftToPick / 40f), partialTick);

        
        int pickPosX = Mth.floor(centerScreenX - (finalSize*0.75f)) - 8;
        int pickPosY = Mth.floor(centerScreenY) -6;


        float angleToMouse = 0;
        Vec2 mouseDelta = new Vec2(
            Mth.clamp(mouseX - centerScreenX , -100f, 100f) / 100f, 
            Mth.clamp(mouseY - centerScreenY - 16, -100f, 100f) / 100f);
        angleToMouse = ((float)Mth.atan2((double)mouseDelta.y,(double)mouseDelta.x) * Mth.RAD_TO_DEG) + 225;


        float angleToCurrentGhost = -(90 * (currentTargetPick / (secondsPerPhase * 20))) + 360;
        lerpedGhostAngle = Mth.lerp(partialTick*4f, lerpedGhostAngle, angleToCurrentGhost);

        if(ticksLeftToPick < 40) {
            // Render ghost lockpick
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.5f);
            RenderSystem.enableBlend();
            ScreenUtilities.drawTexture(graphics, new BlitCoordinates(pickPosX, pickPosY, finalSize, finalSize), 
                lerpedGhostAngle, 26 * scale, 8 * scale,
                new ScreenTexture(new ResourceLocation(CuffedMod.MODID, "textures/item/lockpick.png"), 0, 0, 16, 16, 16, 16));
       
            
            // render lockpick
            RenderSystem.setShaderColor(1f, 1f, 1f, (float)MathUtilities.invert01(animationTick / 5d));
            ScreenUtilities.drawTexture(graphics, new BlitCoordinates(pickPosX, pickPosY, finalSize, finalSize), 
                angleToMouse, 26 * scale, 8 * scale,
                new ScreenTexture(new ResourceLocation(CuffedMod.MODID, "textures/item/lockpick.png"), 0, 0, 16, 16, 16, 16));
        }



        if(lastMouseInput.getAction() == 0 && lastMouseInput.getInput() == 0) {
            if(angleToMouse > angleToCurrentGhost - TARGET_BUFFER * 2
                    && angleToMouse < angleToCurrentGhost + TARGET_BUFFER ) {

                beginNewPhase(instance);
            }
        }
    }

    void beginNewPhase(@Nonnull Minecraft instance) {
        currentPhase++;
        ticksLeftToPick += progressPerPick;

        LocalPlayer player = instance. player;
        if(player!=null)
            player.playSound(SoundEvents.CHAIN_STEP, 1f, 0.7f + (ticksLeftToPick / 40));


        animationTick = 5;
        isAnimating = true;

        assignNewGhost();
    }
    
    void startLockpicking() {
        currentPhase = 0;
        ticksLeftToPick = 30;

        assignNewGhost();
    }

    void failLockpicking(@Nonnull Minecraft instance) {
        LocalPlayer player = instance.player;
        if(player != null) {
            switch (type) {
                case 0:
                    CuffedAPI.Networking.sendLockpickFinishPickingLockPacketToServer(true, lockId, player.getUUID());
                    break;
                case 1:
                    CuffedAPI.Networking.sendLockpickFinishPickingRestraintPacketToServer(true, restrainedUUID, restraintType, player.getUUID());
                    break;
                case 2:
                    CuffedAPI.Networking.sendLockpickFinishPickingCellDoorPacketToServer(true, doorPos, player.getUUID());
                    break;
            }
            instance.setScreen(null);
        }
    }

    void completeLockpicking(@Nonnull Minecraft instance) {
        LocalPlayer player = instance.player;
        if(player != null) {
            switch (type) {
                case 0:
                    CuffedAPI.Networking.sendLockpickFinishPickingLockPacketToServer(false, lockId, player.getUUID());
                    break;
                case 1:
                    CuffedAPI.Networking.sendLockpickFinishPickingRestraintPacketToServer(false, restrainedUUID, restraintType, player.getUUID());
                    break;
                case 2:
                    CuffedAPI.Networking.sendLockpickFinishPickingCellDoorPacketToServer(false, doorPos, player.getUUID());
                    break;
            }
            instance.setScreen(null);
        }
        instance.setScreen(null);
    }

    void assignNewGhost() {
        Random r = new Random();
        currentTargetPick = r.nextFloat() * (secondsPerPhase * 20);
    }
}
