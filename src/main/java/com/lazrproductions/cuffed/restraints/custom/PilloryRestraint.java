package com.lazrproductions.cuffed.restraints.custom;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PilloryRestraint extends AbstractHeadRestraint {

    static final ResourceLocation CUFFED_WIDGETS = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "textures/gui/widgets.png");

    static final ScreenTexture CHAIN_ICON = new ScreenTexture(CUFFED_WIDGETS, 44, 24, 16, 16, 192, 192);
    
    public PilloryRestraint() {

    }
    public PilloryRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        super(stack, player, captor);
    }

    // #region Restraint Properties

    public static final ResourceLocation ID = ModRestraints.PILLORY.getId();
    public ResourceLocation getId() {
        return ID;
    }

    public String getActionBarLabel() {
        return "info.cuffed.restraints.pillory.action_bar";
    }
    public String getName() {
        return "info.cuffed.restraints.pillory.name";
    }

    public static final Item ITEM =  ModItems.PILLORY_ITEM.get();
    public Item getItem() {
        return ITEM;
    } 
    public static final Item KEY = null;
    public Item getKeyItem() {
        return KEY;
    }

    public SoundEvent getEquipSound() {
        return SoundEvents.WOOD_BREAK;
    }
    public SoundEvent getUnequipSound() {
        return SoundEvents.WOOD_BREAK;
    }

    public boolean AllowBreakingBlocks() {
        return false;
    }
    public boolean AllowItemUse() {
        return false;
    }
    public boolean AllowMovement() {
        return true;
    }
    public boolean AllowJumping() {
        return true;
    }
    public boolean AllowSprinting() {
        return true;
    }

    public boolean canBeBrokenOutOf() {
        return false;
    }
    public boolean getLockpickable() {
        return false;
    }
    public int getLockpickingProgressPerPick() {
        return 5;
    }
    public int getLockpickingSpeedIncreasePerPick() {
        return 0;
    }


    public ArmRestraintAnimationFlags getArmAnimationFlags() {
        return ArmRestraintAnimationFlags.NONE;
    }
    public LegRestraintAnimationFlags getLegAnimationFlags() {
        return LegRestraintAnimationFlags.NONE;
    }
    
    // #endregion

    // #region Events

    public void onTickServer(ServerPlayer player) {
        super.onTickServer(player);
    }

    public void onTickClient(Player player) {
        super.onTickClient(player);
    }

    public void onEquippedServer(ServerPlayer player, ServerPlayer captor) {
        super.onEquippedServer(player, captor);
    }

    public void onEquippedClient(Player player, Player captor) {
        super.onEquippedClient(player, captor);
    }

    public void onUnequippedServer(ServerPlayer player) {
        super.onUnequippedServer(player);
    }

    public void onUnequippedClient(Player player) {
        super.onUnequippedClient(player);
    }

    public void onLoginServer(ServerPlayer player) {
    }

    public void onLoginClient(Player player) {
    }

    public void onLogoutServer(ServerPlayer player) {
    }

    public void onLogoutClient(Player player) {
    }

    public void onDeathServer(ServerPlayer player) {
    }

    public void onDeathClient(Player player) {
    }

    public void onJumpServer(ServerPlayer player) {
    }

    public void onJumpClient(Player player) {
    }

    public float onLandServer(ServerPlayer player, float distance, float damageMultiplier) {
        return 1;
    }

    public void onLandClient(Player player, float distance, float damageMultiplier) {
    }

    // #endregion

    // #region Client-Side operations

    int lastBarIndex = 0;
    public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
        super.renderOverlay(player, graphics, partialTick, window);
        
        float f = 1;
        graphics.setColor(f, f, f, 1);
                
        int iconWidth = (int) (16 * 1.75f);
        int iconHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (iconWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (iconHeight) - 100;

        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, iconWidth, iconHeight), CHAIN_ICON);
        graphics.setColor(1, 1, 1, 1);
    }

    public void onKeyInput(Player player, int keyCode, int action) {
        super.onKeyInput(player, keyCode, action);
    }

    public void onMouseInput(Player player, int keyCode, int action) {
        super.onMouseInput(player, keyCode, action);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public RestraintModelInterface getModelInterface() {
        return new PilloryRestraintModelInterface();
    }
    

    // #endregion

    @OnlyIn(Dist.CLIENT)
    public static class PilloryRestraintModelInterface extends RestraintModelInterface { 
        static final Class<? extends HumanoidModel<? extends LivingEntity>> MODEL_CLASS = null;
        static final ModelLayerLocation MODEL_LAYER = null;
        static final ResourceLocation MODEL_TEXTURE = null;

        @Override
        public Class<? extends HumanoidModel<? extends LivingEntity>> getRenderedModel() {
            return MODEL_CLASS;
        }
        @Override
        public ModelLayerLocation getRenderedModelLayer() {
            return MODEL_LAYER;
        }
        @Override
        public ResourceLocation getRenderedModelTexture() {
            return MODEL_TEXTURE;
        }
    }
}
