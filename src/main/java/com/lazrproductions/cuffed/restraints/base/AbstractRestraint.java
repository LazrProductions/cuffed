package com.lazrproductions.cuffed.restraints.base;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.compat.ArsNouveauCompat;
import com.lazrproductions.cuffed.compat.IronsSpellsnSpellbooksCompat;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public abstract class AbstractRestraint {

    private ServerPlayer s_player;
    private Player c_player;
    private CompoundTag itemData;
    @Nonnull private UUID captor;



    public AbstractRestraint(){ captor = UUID.randomUUID(); }
    public AbstractRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
        //this.onEquippedServer(player, captor);

        if(this instanceof IEnchantableRestraint enchantable)
            enchantable.setEnchantments(stack.getEnchantmentTags());
        
        itemData = new CompoundTag();
        stack.save(itemData);
        
        this.captor = captor.getUUID();    
    } 

    //#region Restraint Properties

    /** Get the item version of this restraint. */
    public abstract String getId();
    /** Get where this restraint is applied, arms or legs. */
    public abstract RestraintType getType();
    /** Get the label to display on the action bar while this restraint is equipped. */
    public abstract String getActionBarLabel();
    /** Get the name of this restraint. */
    public abstract String getName();
    /** Get the item version of this restraint. */
    public abstract Item getItem();
    /** Get the item used to unlock and unequip this restraint. */
    public abstract Item getKeyItem();

    public abstract boolean AllowItemUse();
    public abstract boolean AllowBreakingBlocks();
    public abstract boolean AllowMovement();
    public abstract boolean AllowJumping();

    /** Get the sound played when this restraint is equipped */
    public abstract SoundEvent getEquipSound();
    /** Get the sound played when this restraint is unequipped */
    public abstract SoundEvent getUnequipSound();

    /** Whether or not this restraint can be broken out of. */
    public abstract boolean getCanBeBrokenOutOf();
    /** Whether or not this restraint can be lockpicked. */
    public abstract boolean getLockpickable();
    /** Get the lockpick speed increase per pick for this restraint. */
    public abstract int getLockpickingSpeedIncreasePerPick();
    /** Get the lockpick progress per pick for this restraint. */
    public abstract int getLockpickingProgressPerPick();

    public abstract ArrayList<Integer> getBlockedKeyCodes();

    //#endregion

    //#region Getters

    /** Get the player of this restraint, server-side or client-side. */
    public Player getPlayer() {
        if(isClientSide())
            return c_player;
        return s_player;
    }
    /** Get the captor of this restraint, server-side only */
    @Nullable
    public Player getCaptor(@Nonnull ServerLevel level) {
        return level.getPlayerByUUID(captor);
    }
    /** Get whether or not this instance is client-side or not. */
    public boolean isClientSide() {
        return s_player == null;
    }

    //#endregion

    //#region Event Handling 

    /** Called each tick on the server. */
    public void onTickServer(ServerPlayer player) {
        s_player = player;

        ModStatistics.awardTimeSpentRestrained(player, this);

        if(this instanceof IEnchantableRestraint e) {
            if(e.getEnchantmentLevel(ModEnchantments.FAMINE.get()) >= 1)
                if(!player.hasEffect(MobEffects.HUNGER))
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));

            if(e.getEnchantmentLevel(ModEnchantments.SHROUD.get()) >= 1)
                if(!player.hasEffect(MobEffects.BLINDNESS))
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));
                    
            if(e.getEnchantmentLevel(ModEnchantments.EXHAUST.get()) >= 1) {
                if(!player.hasEffect(MobEffects.DIG_SLOWDOWN)) player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
                if(!player.hasEffect(MobEffects.WEAKNESS)) player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            }     

            if(e.getEnchantmentLevel(ModEnchantments.SILENCE.get()) >= 1) {
                if(CuffedMod.ArsNouveauInstalled) ArsNouveauCompat.DrainMana(player, 2);
                if(CuffedMod.IronsSpellsnSpellbooksInstalled) IronsSpellsnSpellbooksCompat.DrainMana(player, 2);
            }
        }
    }
    /** Called each tick on the client. */
    public void onTickClient(Player player) {
        c_player = player;
    }

    /** Called on the server when this restraint is equipped. */
    public void onEquippedServer(ServerPlayer player, ServerPlayer captor)  {
        s_player = player;
        this.captor = captor.getUUID();

        ModStatistics.awardRestrained(player, this);

        Random random = new Random();
        player.level().playSound(null, player.blockPosition(), getEquipSound(), SoundSource.PLAYERS, 0.8f, (random.nextFloat() * 0.2f) + 0.9f);
    }
    /** Called on the client when this restraint is equipped. */
    public void onEquippedClient(Player player, Player captor) {
        c_player = player;
    }

    /** Called on the server when this restraint is equipped. */
    public void onUnequippedServer(ServerPlayer player) {
        Random random = new Random();
        player.level().playSound(null, player.blockPosition(), getUnequipSound(), SoundSource.PLAYERS, 0.8f, (random.nextFloat() * 0.2f) + 0.9f);
    }
    /** Called on the client when this restraint is equipped. */
    public void onUnequippedClient(Player player) {

    }

    /** Called on the server when the wearer logs in. */
    public abstract void onLoginServer(ServerPlayer player);
    /** Called on the client when the wearer logs in. */
    public abstract void onLoginClient(Player player);

    /** Called on the server when the wearer logs out. */
    public abstract void onLogoutServer(ServerPlayer player);
    /** Called on the client when the wearer logs out. */
    public abstract void onLogoutClient(Player player);

    /** Called on the server when the wearer dies. */
    public abstract void onDeathServer(ServerPlayer player);
    /** Called on the client when the wearer dies. */
    public abstract void onDeathClient(Player player);

    /** Called on the server when the wearer jumps. */
    public abstract void onJumpServer(ServerPlayer player);
    /** Called on the client when the wearer jumps. */
    public abstract void onJumpClient(Player player);
    
    /** Called on the server when the wearer lands. */
    public abstract float onLandServer(ServerPlayer player, float distance, float damageMultiplier);
    /** Called on the client when the wearer lands. */
    public abstract void onLandClient(Player player, float distance, float damageMultiplier);

    //#endregion
    
    //#region Client-Side Handles

    /** Called each frame only on the client to render overlays and such. */
    public abstract void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window);


    protected int clientSidedDurability = 100;
    /** Called on the client any time the client presses any key. */
    public void onKeyInput(Player player, int keyCode, int action) {
        Minecraft instance = Minecraft.getInstance();
        
        if(action == 1) {
            if(this instanceof IBreakableRestraint breakable) {
                breakable.attemptToBreak(player, keyCode, action, instance.options);
            }
        }
    }
    /** Called on the client any time the client presses any mouse button. */
    public void onMouseInput(Player player, int keyCode, int action) {
        Minecraft instance = Minecraft.getInstance();
        
        if(action == 1)
            if(this instanceof IBreakableRestraint breakable)
                breakable.attemptToBreak(player, keyCode, action, instance.options);
    }

    //#endregion

    //#region Server-Side Handles

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", getId());
        tag.putString("Type", "arm");
        if(this instanceof IEnchantableRestraint ench)
            tag.put("Enchantments", ench.getEnchantments());

        tag.putUUID("Captor", captor);
        tag.put("ItemData", itemData);
        return tag;
    }
    public void deserializeNBT(CompoundTag nbt) {
        if(this instanceof IEnchantableRestraint ench) {
            ListTag t = nbt.getList("Enchantments", 10);
            ench.setEnchantments(t);
        }
        this.captor = nbt.getUUID("Captor");
        this.itemData = nbt.getCompound("ItemData");
    }

    /** Save this restraint to an item stack */
    public ItemStack saveToItemStack() {
        ItemStack stack = ItemStack.of(itemData);
        stack.setCount(1);
        if(this instanceof IBreakableRestraint breakable)
            stack.setDamageValue(breakable.getMaxDurability() - breakable.getDurability());
        if(this instanceof IEnchantableRestraint enchantable)
            EnchantmentHelper.setEnchantments(EnchantmentHelper.deserializeEnchantments(enchantable.getEnchantments()), stack);
        return stack;
    }

    
    /* UTILITY PACKET CODEX
     * 0-100 -> General Use Codes (mostly unsued)
     * 101-200 -> Durability Codes
     *    (101 - sync durability to client)  
     *    (102 - increment durability from client)  
     *    (103 - relay restraint broken event to client)
     * 201->300 -> Enchantment Codes
     */

    /**
     * Called on the server when this player receives a restraint utility packet for their restraint.
     */
    public void receiveUtilityPacketServer(ServerPlayer player, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
        if(utiltiyCode == 102) {
            if(this instanceof IBreakableRestraint breakable) {
                breakable.incrementDurability(player, integerArg);
            }
        }
    }
    /**
     * Called when this client receives a restraint utility packet for their restraint.
     */
    public void receiveUtilityPacketClient(Player player, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
        if(utiltiyCode == 101) {
            this.clientSidedDurability = integerArg;
        } else if(utiltiyCode == 103) {
            if(this instanceof IBreakableRestraint breakable)
                breakable.onBrokenClient(player);
        }
    }
    //#endregion
}
