package com.lazrproductions.cuffed.client.gui.screen;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.inventory.FriskingMenu;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
   }

   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(graphics);
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
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
                  renderEntityInInventoryFollowsMouse(graphics, i + 51, j + 71, 30, (float) (i + 51) - mouseX,
                        (float) (j + 75 - 50) - mouseY, actualEntity);
               }
            }
         }
      }
   }

   public static void renderEntityInInventoryFollowsMouse(GuiGraphics graphics, int x, int y, int z, float mouseX,
         float mouseY, LivingEntity entity) {
      float f = (float) Math.atan((double) (mouseX / 40.0F));
      float f1 = (float) Math.atan((double) (mouseY / 40.0F));
      // Forge: Allow passing in direct angle components instead of mouse position
      renderEntityInInventoryFollowsAngle(graphics, x, y, z, f, f1, entity);
   }
   public static void renderEntityInInventoryFollowsAngle(GuiGraphics graphics, int x, int y,
         int z, float angleXComponent, float angleYComponent, LivingEntity entity) {
      float f = angleXComponent;
      float f1 = angleYComponent;
      Quaternionf quaternionf = (new Quaternionf()).rotateZ((float) Math.PI);
      Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float) Math.PI / 180F));
      quaternionf.mul(quaternionf1);
      float f2 = entity.yBodyRot;
      float f3 = entity.getYRot();
      float f4 = entity.getXRot();
      float f5 = entity.yHeadRotO;
      float f6 = entity.yHeadRot;
      entity.yBodyRot = 180.0F + f * 20.0F;
      entity.setYRot(180.0F + f * 40.0F);
      entity.setXRot(-f1 * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      renderEntityInInventory(graphics, x, y, z, quaternionf, quaternionf1, entity);
      entity.yBodyRot = f2;
      entity.setYRot(f3);
      entity.setXRot(f4);
      entity.yHeadRotO = f5;
      entity.yHeadRot = f6;
   }
   public static void renderEntityInInventory(GuiGraphics graphics, int x, int y, int z,
         Quaternionf rotationA, @Nullable Quaternionf rotationB, LivingEntity entity) {
      graphics.pose().pushPose();
      graphics.pose().translate((double) x, (double) y, 50.0D);
      graphics.pose()
            .mulPoseMatrix((new Matrix4f()).scaling((float) z, (float) z, (float) (-z)));
      graphics.pose().mulPose(rotationA);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (rotationB != null) {
         rotationB.conjugate();
         entityrenderdispatcher.overrideCameraOrientation(rotationB);
      }

      entityrenderdispatcher.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> {
         entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, graphics.pose(),
               graphics.bufferSource(), 15728880);
      });
      graphics.flush();
      entityrenderdispatcher.setRenderShadow(true);
      graphics.pose().popPose();
      Lighting.setupFor3DItems();
   }

   @Override
   protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752,
            false);
         graphics.drawString(this.font, this.title, this.titleLabelX + 110 - (this.font.width(this.title) / 2), this.titleLabelY + 20, 4210752,
               false);
   }
}