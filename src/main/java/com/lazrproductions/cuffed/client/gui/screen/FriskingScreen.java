package com.lazrproductions.cuffed.client.gui.screen;

import java.util.List;

import javax.annotation.Nonnull;


import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.inventory.FriskingMenu;
import com.lazrproductions.lazrslib.client.gui.GuiGraphics;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("deprecation")
public class FriskingScreen extends AbstractContainerScreen<FriskingMenu> {
   private static final ResourceLocation FRISKING_BACKGROUND = new ResourceLocation(CuffedMod.MODID,
         "textures/gui/container/frisking.png");

   public FriskingScreen(FriskingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      // int i = 222;
      // int j = 114;
      this.imageHeight = 256;
      this.inventoryLabelY = this.imageHeight - 94;

      graphics = GuiGraphics.from(Minecraft.getInstance());
   }

   final GuiGraphics graphics; 

   public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(stack);
      super.render(stack, mouseX, mouseY, partialTick);
      this.renderTooltip(stack, mouseX, mouseY);
   }

   protected void renderBg(@Nonnull PoseStack stack, float partialTick, int mouseX, int mouseY) {
      imageWidth = 176;
      imageHeight = 256;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(FRISKING_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
      Minecraft instance = this.minecraft;
      if(instance!=null) {
         ClientLevel level = instance.level;
         if(level != null) {
            LocalPlayer player = instance.player;
            if(player != null) {
               Entity entity = null;
               List<? extends Player> players = level.players();
               for (LivingEntity p : players) {
                  if(p.getDisplayName().getString().equals(this.title.getString()))
                     entity = p;
               }
               if(entity instanceof LivingEntity actualEntity) {
                  renderEntityInInventory(i + 51, j + 75, 30, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, actualEntity);
               }
            }
         }
      }
   }

   public static void renderEntityInInventory(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
      float f = (float)Math.atan((double)(p_98854_ / 40.0F));
      float f1 = (float)Math.atan((double)(p_98855_ / 40.0F));
      renderEntityInInventoryRaw(p_98851_, p_98852_, p_98853_, f, f1, p_98856_);
   }
   public static void renderEntityInInventoryRaw(int p_98851_, int p_98852_, int p_98853_, float angleXComponent, float angleYComponent, LivingEntity p_98856_) {
      float f = angleXComponent;
      float f1 = angleYComponent;
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate((double)p_98851_, (double)p_98852_, 1050.0D);
      posestack.scale(1.0F, 1.0F, -1.0F);
      RenderSystem.applyModelViewMatrix();
      PoseStack posestack1 = new PoseStack();
      posestack1.translate(0.0D, 0.0D, 1000.0D);
      posestack1.scale((float)p_98853_, (float)p_98853_, (float)p_98853_);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
      quaternion.mul(quaternion1);
      posestack1.mulPose(quaternion);
      float f2 = p_98856_.yBodyRot;
      float f3 = p_98856_.getYRot();
      float f4 = p_98856_.getXRot();
      float f5 = p_98856_.yHeadRotO;
      float f6 = p_98856_.yHeadRot;
      p_98856_.yBodyRot = 180.0F + f * 20.0F;
      p_98856_.setYRot(180.0F + f * 40.0F);
      p_98856_.setXRot(-f1 * 20.0F);
      p_98856_.yHeadRot = p_98856_.getYRot();
      p_98856_.yHeadRotO = p_98856_.getYRot();
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderdispatcher.overrideCameraOrientation(quaternion1);
      entityrenderdispatcher.setRenderShadow(false);
      MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> {
         entityrenderdispatcher.render(p_98856_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
      });
      multibuffersource$buffersource.endBatch();
      entityrenderdispatcher.setRenderShadow(true);
      p_98856_.yBodyRot = f2;
      p_98856_.setYRot(f3);
      p_98856_.setXRot(f4);
      p_98856_.yHeadRotO = f5;
      p_98856_.yHeadRot = f6;
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   @Override
   protected void renderLabels(@Nonnull PoseStack stack, int mouseX, int mouseY) {
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752,
            false);
         graphics.drawString(this.font, this.title, this.titleLabelX + 110 - (this.font.width(this.title) / 2), this.titleLabelY + 20, 4210752,
               false);
   }
}