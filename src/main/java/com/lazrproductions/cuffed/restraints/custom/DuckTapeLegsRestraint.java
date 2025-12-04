package com.lazrproductions.cuffed.restraints.custom;

import java.util.Random;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.IBreakableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.lazrproductions.cuffed.restraints.client.model.DuckTapeLegsModel;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DuckTapeLegsRestraint extends AbstractLegRestraint implements IBreakableRestraint {

    static final ResourceLocation CUFFED_WIDGETS = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "textures/gui/widgets.png");

    static final ScreenTexture CHAIN_ICON = new ScreenTexture(CUFFED_WIDGETS, 44, 24, 16, 16, 192, 192);
    
    public DuckTapeLegsRestraint() {

    }
    public DuckTapeLegsRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        super(stack, player, captor);
    }

    // #region Restraint Properties

    public static final ResourceLocation ID = ModRestraints.DUCK_TAPE_LEGS.getId();
    public ResourceLocation getId() {
        return ID;
    }

    public String getActionBarLabel() {
        return "info.cuffed.restraints.duck_tape_legs.action_bar";
    }
    public String getName() {
        return "info.cuffed.restraints.duck_tape_legs.name";
    }

    public static final Item ITEM =  ModItems.DUCK_TAPE.get();
    public Item getItem() {
        return ITEM;
    } 
    public static final Item KEY = null; // TODO: should there be a key here?
    public Item getKeyItem() {
        return KEY;
    }

    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }
    public SoundEvent getUnequipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    public boolean AllowBreakingBlocks() {
        return true;
    }
    public boolean AllowItemUse() {
        return true;
    }
    public boolean AllowMovement() {
        return false;
    }
    public boolean AllowJumping() {
        return false;
    }
    public boolean AllowSprinting() {
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
    
    public static final LegRestraintAnimationFlags LEG_ANIMATION_FLAGS = LegRestraintAnimationFlags.NONE;
    public ArmRestraintAnimationFlags getArmAnimationFlags() {
        return ArmRestraintAnimationFlags.NONE;
    }
    public LegRestraintAnimationFlags getLegAnimationFlags() {
        return LEG_ANIMATION_FLAGS;
    }
    // #endregion

    // #region Events

    public void onTickServer(ServerPlayer player) {
        super.onTickServer(player);
    }

    public void onTickClient(Player player) {
        super.onTickClient(player);
        
        if(breakCooldown>0)
            breakCooldown--;
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
        
        // Display Icon and chain overlay
        float f = (Mth.clamp(breakCooldown / 10, 0, 1)+1);
        graphics.setColor(f, f, f, 1);

        int iconWidth = (int) (16 * 1.75f);
        int iconHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (iconWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (iconHeight) - 30;
        
        ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, iconWidth, iconHeight), CHAIN_ICON);
        graphics.setColor(1, 1, 1, 1);

        // Display break progress
        float p = Mth.clamp((float)clientSidedDurability / (float)getMaxDurability(), 0, 1);
        ScreenUtilities.drawGenericProgressBar(graphics, new BlitCoordinates(x, y+iconHeight-2, iconWidth, iconHeight), p);
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
        return new DuckTapeLegsRestraintModelInterface();
    }    
    
    // #endregion

    // #region Breakable Restraint Management

    public SoundEvent getBreakSound() {
        return SoundEvents.ITEM_BREAK;
    }

    public boolean isKeyToAttemptBreak(int keyCode, Options options) {
        return keyCode == options.keyLeft.getKey().getValue() || keyCode == options.keyRight.getKey().getValue();
    }

    public boolean requireAlternateKeysToAttemptBreak() {
        return true;
    }

    public int getMaxDurability() {
        return CuffedMod.SERVER_CONFIG.DUCK_TAPE_ON_ARMS_DURABILITY.get();
    }

    public boolean dropItemOnBroken() {
        return CuffedMod.SERVER_CONFIG.DUCK_TAPE_ON_ARMS_DROP_ITEM_WHEN_BROKEN.get();
    }

    public boolean canBeBrokenOutOf() {
        return CuffedMod.SERVER_CONFIG.DUCK_TAPE_ON_ARMS_CAN_BE_BROKEN_OUT_OF.get();
    }

    /** Changed only server-side. changes are synced to client. */
    private int durability = 100;

    public int getDurability() {
        return durability;
    }

    float breakCooldown = 4;
    int lastKeyPressed = -1;

    public void attemptToBreak(Player player, int keyCode, int action, Options options) {
        if (breakCooldown <= 0 && canBeBrokenOutOf()) {
            if (isKeyToAttemptBreak(keyCode, options)) {
                if (!requireAlternateKeysToAttemptBreak() || keyCode != lastKeyPressed) {
                    Random r = new Random();
                    double chance = 0.5f;
                    double cooldownMultiplier = 1;
                    if (r.nextDouble() < chance) {
                        lastKeyPressed = keyCode;
                        player.playNotifySound(SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 1f,
                                Mth.nextFloat(player.getRandom(), 0.9f, 1.1f));

                        CuffedAPI.Networking.sendRestraintUtilityPacketToServer(getType(), 102, -1, false, 0D, "");

                        breakCooldown = r.nextInt(20) + Mth.floor(20 * cooldownMultiplier);
                    }
                }
            }
        }
    }

    public void setDurability(ServerPlayer player, int value) {
        if (value > getMaxDurability())
            value = getMaxDurability();
        if (value < 0)
            value = 0;

        durability = value;
        CuffedAPI.Networking.sendRestraintUtilityPacketToClient(player, getType(), 101, durability, false, 0, "");

        if (durability <= 0) {
            onBrokenServer(player);
        }
    }

    public void incrementDurability(ServerPlayer player, int value) {
        int newValue = getDurability() + value;
        setDurability(player, newValue);
    }

    public void onBrokenServer(ServerPlayer player) {
        CuffedAPI.Networking.sendRestraintUtilityPacketToClient(player, getType(), 103, 0, false, 0, "");

        Random random = new Random();
        player.level().playSound(null, player.blockPosition(), getBreakSound(), SoundSource.PLAYERS, 0.8f,
                (random.nextFloat() * 0.2f) + 0.9f);
                
        ModStatistics.awardRestraintBroken(player, this);

        if (dropItemOnBroken()) {
           ItemStack stack = this.saveToItemStack();
           stack.setDamageValue(stack.getMaxDamage() - 1); // instead of 0 durability
           ItemEntity e = new ItemEntity(player.level(), player.getX(), player.getY() + 0.6D, player.getZ(), stack);
           e.setDefaultPickUpDelay();
           player.level().addFreshEntity(e);
        }
                
        IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
        if (getType() == RestraintType.Arm) {
            cap.setArmRestraintWithoutWarning(player, null);
        } else
            cap.setLegRestraintWithoutWarning(player, null);
    }

    public void onBrokenClient(Player player) {
    }

    // #endregion

    @OnlyIn(Dist.CLIENT)
    public static class DuckTapeLegsRestraintModelInterface extends RestraintModelInterface { 
        @SuppressWarnings("unchecked")
        static final Class<? extends HumanoidModel<? extends LivingEntity>> MODEL_CLASS = (Class<? extends HumanoidModel<? extends LivingEntity>>)(Class<?>)DuckTapeLegsModel.class;
        static final ModelLayerLocation MODEL_LAYER = ModModelLayers.DUCK_TAPE_LEG_LAYER;
        static final ResourceLocation MODEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, "textures/entity/duck_tape.png");
        
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
