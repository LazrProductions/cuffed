package com.lazrproductions.cuffed.restraints.custom;

import java.util.ArrayList;
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
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.IBreakableRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.lazrproductions.cuffed.restraints.client.model.ShacklesModel;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShacklesArmsRestraint extends AbstractArmRestraint implements IBreakableRestraint, IEnchantableRestraint {
    static final ResourceLocation WIDGETS = new ResourceLocation(CuffedMod.MODID, "textures/gui/widgets.png");
    
    static final ScreenTexture CHAIN_ICON = new ScreenTexture(WIDGETS, 44, 24, 16, 16, 192, 192);

    public ShacklesArmsRestraint() {
        enchantments = new ListTag();
    }
    public ShacklesArmsRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        super(stack, player, captor);
        this.durability = getMaxDurability() - stack.getDamageValue();

    }

    // #region Restraint Properties

    public static final ResourceLocation ID = ModRestraints.SHACKLES.getId();
    public ResourceLocation getId() {
        return ID;
    }
    
    public String getActionBarLabel() {
        return "info.cuffed.restraints.shackles.action_bar";
    }
    public String getName() {
        return "info.cuffed.restraints.shackles.name";
    }
    
    public static final Item ITEM = ModItems.SHACKLES.get();
    public Item getItem() {
        return ITEM;
    }
    public static final Item KEY = ModItems.SHACKLES_KEY.get();
    public Item getKeyItem() {
        return KEY;
    }

    public static final ArmRestraintAnimationFlags ARM_ANIMATION_FLAGS = ArmRestraintAnimationFlags.ARMS_TIED_FRONT;
    public ArmRestraintAnimationFlags getArmAnimationFlags() {
        return ARM_ANIMATION_FLAGS;
    }
    public LegRestraintAnimationFlags getLegAnimationFlags() {
        return LegRestraintAnimationFlags.NONE;
    }


    public SoundEvent getEquipSound() {
        return ModSounds.SHACKLES_EQUIP;
    }
    public SoundEvent getUnequipSound() {
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }

    public boolean AllowBreakingBlocks() {
        return false;
    }
    public boolean AllowItemUse() {
        return true;
    }
    public boolean AllowMovement() {
        return true;
    }
    public boolean AllowJumping() {
        return true;
    }

    public boolean canBeBrokenOutOf() {
        return CuffedMod.SERVER_CONFIG.SHACKLES_ON_ARMS_CAN_BE_BROKEN_OUT_OF.get();
    }
    public boolean getLockpickable() {
        return CuffedMod.SERVER_CONFIG.SHACKLES_ON_ARMS_LOCKPICKABLE.get();
    }
    public int getLockpickingProgressPerPick() {
        return CuffedMod.SERVER_CONFIG.SHACKLES_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK.get();
    }
    public int getLockpickingSpeedIncreasePerPick() {
        return CuffedMod.SERVER_CONFIG.SHACKLES_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK.get();
    }

    public ArrayList<Integer> getBlockedKeyCodes() {
        ArrayList<Integer> b = new ArrayList<Integer>();
        Minecraft inst = Minecraft.getInstance();
        
        if(inst == null || inst.options == null)
        return b;

        b.add(inst.options.keyAttack.getKey().getValue());

        for (KeyMapping mapping : inst.options.keyMappings) {
            switch (mapping.getName()) {
                case "key.parcool.Crawl":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Breakfall":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.WallSlide":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Vault":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.Flipping":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.FastRun":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.ClingToCliff":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.HangDown":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.WallJump":
                    b.add(mapping.getKey().getValue());
                    break;
                case "key.parcool.HorizontalWallRun":
                    b.add(mapping.getKey().getValue());
                    break;
            }
        }

        return b;
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

        float f = (Mth.clamp(breakCooldown / 10, 0, 1)+1);
        graphics.setColor(f, f, f, 1);
                
        int iconWidth = (int) (16 * 1.75f);
        int iconHeight = (int) (16 * 1.75f);
        int x = (window.getGuiScaledWidth() / 2) - (iconWidth / 2);
        int y = (window.getGuiScaledHeight() / 2) - (iconHeight) - 65;


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
        return new ShacklesArmsRestraintModelInterface();
    }

    // #endregion

    // #region Breakable Restraint Management

    public SoundEvent getBreakSound() {
        return SoundEvents.ITEM_BREAK;
    }

    public boolean isKeyToAttemptBreak(int keyCode, Options options) {
        return keyCode == options.keyAttack.getKey().getValue() || keyCode == options.keyUse.getKey().getValue();
    }

    public boolean requireAlternateKeysToAttemptBreak() {
        return true;
    }

    public int getMaxDurability() {
        return ModItems.SHACKLES.get().getMaxDamage(ModItems.SHACKLES.get().getDefaultInstance());
    }

    public boolean dropItemOnBroken() {
        return CuffedMod.SERVER_CONFIG.SHACKLES_ON_ARMS_DROP_ITEM_WHEN_BROKEN.get();
    }

    /** Changed only server-side. changes are synced to client. */
    private int durability = 100;

    public int getDurability() {
        return durability;
    }

    float breakCooldown = 4;
    int lastKeyPressed = -1;

    public void attemptToBreak(Player player, int keyCode, int action, Options options) {
        if (breakCooldown <= 0) {
            if (isKeyToAttemptBreak(keyCode, options)) {
                if (!requireAlternateKeysToAttemptBreak() || keyCode != lastKeyPressed) {
                    Random r = new Random();
                    double chance = 0.5f;
                    double cooldownMultiplier = 1;
                    if(this instanceof IEnchantableRestraint && hasEnchantment(Enchantments.UNBREAKING)) {
                        double d = getEnchantmentLevel(Enchantments.UNBREAKING) / 3d;
                        chance = ((MathUtilities.invert01(d / 3d) * 0.7d) + 0.3d)  * 0.5f;
                        cooldownMultiplier = 1 + d;
                    }
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
        if (getType() == RestraintType.Arm)
            cap.setArmRestraintWithoutWarning(player, null);
        else
            cap.setLegRestraintWithoutWarning(player, null);

    }

    public void onBrokenClient(Player player) {
    }

    // #endregion

    // #region Enchantable Restraint Management

    ListTag enchantments;

    public ListTag getEnchantments() {
        return enchantments;
    }
    public void setEnchantments(ListTag tag) {
        enchantments = tag;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        ResourceLocation resourcelocation = EnchantmentHelper.getEnchantmentId(enchantment);
        for (int i = 0; i < enchantments.size(); ++i) {
            CompoundTag compoundtag = enchantments.getCompound(i);
            ResourceLocation resourcelocation1 = EnchantmentHelper.getEnchantmentId(compoundtag);
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
                return true;
            }
        }
        return false;
    }
    public int getEnchantmentLevel(Enchantment enchantment) {
        ResourceLocation resourcelocation = EnchantmentHelper.getEnchantmentId(enchantment);
        for (int i = 0; i < enchantments.size(); ++i) {
            CompoundTag compoundtag = enchantments.getCompound(i);
            ResourceLocation resourcelocation1 = EnchantmentHelper.getEnchantmentId(compoundtag);
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
                return EnchantmentHelper.getEnchantmentLevel(compoundtag);
            }
        }
        return 0;
    }
    public void enchant(Enchantment enchantment, int value) {
        ResourceLocation l = EnchantmentHelper.getEnchantmentId(enchantment);
        enchantments.add(EnchantmentHelper.storeEnchantment(l, value));        
    }
    // #endregion

    @OnlyIn(Dist.CLIENT)
    public static class ShacklesArmsRestraintModelInterface extends RestraintModelInterface { 
        @SuppressWarnings("unchecked")
        static final Class<? extends HumanoidModel<? extends LivingEntity>> MODEL_CLASS = (Class<? extends HumanoidModel<? extends LivingEntity>>)(Class<?>)ShacklesModel.class;
        static final ModelLayerLocation MODEL_LAYER = ModModelLayers.SHACKLES_LAYER;
        static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(CuffedMod.MODID, "textures/entity/shackles.png");

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
