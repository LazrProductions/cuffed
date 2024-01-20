package com.lazrproductions.cuffed.cap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.ICuffedCapability;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.init.ModAttributes;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.utils.ScreenUtils;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.Key;

public class CuffedCapability implements ICuffedCapability {

    @Override
    public void copyFrom(ICuffedCapability cap) {
        if (cap instanceof CuffedCapability o) {
            this.handcuffed = o.handcuffed;
            this.detained = o.detained;
            this.detainedPos = o.detainedPos;
            this.detainedRot = o.detainedRot;
            this.softCuffed = o.softCuffed;
            this.anchor = o.anchor;
            this.progress = o.progress;
        }
    }

    public CompoundTag getDefaultNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("progress", 0);
        nbt.putInt("breakProgress", 0);
        nbt.putBoolean("handcuffed", false);
        nbt.putBoolean("softcuffed", false);
        nbt.putInt("detained", -1);
        nbt.putString("nickname", Component.Serializer.toJson(nickname));        
        return nbt;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("progress", progress);
        nbt.putInt("breakProgress", breakProgress);
        nbt.putBoolean("handcuffed", handcuffed);
        nbt.putBoolean("softcuffed", softCuffed);
        nbt.putInt("detained", detained);
        if(isDetained()>-1) {
            ListTag posTag = new ListTag();
            posTag.add((Tag) DoubleTag.valueOf(detainedPos.x));
            posTag.add((Tag) DoubleTag.valueOf(detainedPos.y));
            posTag.add((Tag) DoubleTag.valueOf(detainedPos.z));
            nbt.put("detainedPos", posTag);
            nbt.putFloat("detainedRot", detainedRot);
        }
        if (anchor > -1) 
            nbt.putInt("anchor", anchor);
        if(serverAnchor!=null)
            nbt.putUUID("anchorId", serverAnchor); //Should only be stored on the server
        
        nbt.putString("nickname", Component.Serializer.toJson(nickname));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        progress = nbt.getFloat("progress");
        breakProgress = nbt.getInt("breakProgress");
        handcuffed = nbt.getBoolean("handcuffed");
        softCuffed = nbt.getBoolean("softcuffed");
        detained = nbt.getInt("detained");
        if(nbt.contains("detainedPos")) {
        ListTag posTag = (ListTag) nbt.get("detainedPos");
        if (posTag != null)
            detainedPos = new Vec3(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2));
        } else 
            detainedPos = null;
        if(nbt.contains("detainedRot"))
            detainedRot = nbt.getFloat("detainedRot");
        else
            detainedRot = 0;
        if(nbt.contains("anchor"))
            anchor = nbt.getInt("anchor");
        else
            anchor = -1;
        if(nbt.contains("anchorId"))
            serverAnchor = nbt.getUUID("anchorId");
        else
            serverAnchor = null;
        nickname = Component.Serializer.fromJson(nbt.getString("nickname"));
    }

    /**
     * The local version of this player entity, will be of type ServerPlayer on the
     * server's side, and will be of type Player for this clients
     */
    Player localSelf;

    // --> Variables that need syncing to client(s)
    /** Synced between client/server, to change use server methods */
    boolean handcuffed;
    /** Synced between client/server, to change use server methods */
    boolean softCuffed;
    /** Synced between client/server, to change use server methods */
    float progress = 0;

    public float GetHandcuffingProgress() {
        return progress;
    }

    int detained = -1;
    /** Synced between client/server, to change use server methods */
    Vec3 detainedPos;
    /** Synced between client/server, to change use server methods */
    float detainedRot;

    /** Synced between client/server, to change use server methods */
    int anchor = -1;
    /** Stored only on the server, not on clients, used to get the anchor entity when the player joins the world. */
    UUID serverAnchor = null;

    /** Synced between client/server, to change use server methods */
    Component nickname = null;

    /** Synced between client/server, to change use server methods*/
    int breakProgress = 0;
    // <--

    // #region Server Side Methods & Variables
    ServerPlayer server_self;

    Player server_cuffingPlayer;
    boolean server_canInterupt;
    boolean server_hasBrokenCuffs;

    int server_suffocateAir;
    int server_suffocateTick;
    boolean server_isSuffocating;
    boolean server_wasHanging;


    @Override
    public void server_joinWorld(ServerPlayer player) {
        AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
            playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);

        if(serverAnchor!=null) {
            Entity e = player.serverLevel().getEntity(serverAnchor);
            if(e!=null)
                anchor = e.getId();
        }

        if(isGettingHandcuffed()) {
            handcuffed = false;
            server_cuffingPlayer = null;
            server_canInterupt = false;
            //server_setAnchor(null);
            if(!CuffedMod.CONFIG.handcuffSettings.persistantNickname)
                server_setNickname(null);
            softCuffed = false;
            progress = 0;
        }

        CuffedAPI.syncAllOthersToClient(player);
        CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), serializeNBT());
    }

    @Override
    public void server_leaveWorld(ServerPlayer player) {
        AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
            playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);

        if(isHandcuffed())
            CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), serializeNBT());
        else if(isGettingHandcuffed())
            CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), getDefaultNBT());
    }

    int breakRegenDelayTimer = 0;
    int breakRegenTimer = 0;
    @Override
    public void server_tick(ServerPlayer player) {
        boolean needsSyncing = false;
        AttributeInstance playerAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (localSelf == null)
            localSelf = player;
        if (server_self == null)
            server_self = player;

        if(serverAnchor!=null && anchor==-1) {
            Entity e = player.serverLevel().getEntity(serverAnchor); // retreive the server anchor on the server, loaded from save.
            if(e!=null) {
                anchor = e.getId();
                serverAnchor = null;
            }
        }

        if(anchor>-1&&getAnchor()==null)
            server_setAnchor(null);


        if (!player.isAlive()) {
            if(isGettingOrCurrentlyHandcuffed())
                server_removeHandcuffs();
            if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
                playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
            return;
        }


        if(isHandcuffed())
            localSelf.awardStat(ModStatistics.TIME_SPENT_HANDCUFFED.get());

        if (isGettingOrCurrentlyHandcuffed()) {
            if (!isSoftCuffed()) {
                if (playerAtt != null && !playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
                    playerAtt.addPermanentModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
            } else {
                if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
                    playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
            }

            Entity _anchor = getAnchor();
            if (_anchor != null) {
                double maxDist = CuffedMod.CONFIG.handcuffSettings.maxChainLength;

                if (localSelf.distanceTo(_anchor) > maxDist - 2 && localSelf.getY() < _anchor.getY() - 1.5F
                        && !localSelf.onGround()) {
                    localSelf.hurt(ModDamageTypes.GetModSource(localSelf, ModDamageTypes.HANG, null), 2);
                } else
                    server_wasHanging = false;
            }

            if (server_cuffingPlayer != null) {
                if (server_hasBrokenCuffs) {
                    server_cuffingPlayer = null;
                    server_hasBrokenCuffs = false;
                } else if (progress >= 42)
                    server_getHandcuffedByPlayer(server_cuffingPlayer);
            }

            if (CuffedMod.CONFIG.handcuffSettings.enableHandcuffBreaking) {
                if (isHandcuffed() && getBreakProgress() > 0) {
                    if (breakRegenDelayTimer <= 0) {
                        breakRegenTimer++;
                        if (breakRegenTimer >= (CuffedMod.CONFIG.handcuffSettings.handcuffHealPerSecond * 20f)) {
                            server_setBreakProgress(getBreakProgress() - 1);
                            breakRegenTimer = 0;
                        }
                    } else {
                        breakRegenDelayTimer--;
                        breakRegenTimer = 0;
                    }
                } else
                    breakRegenTimer = 0;
            }


            // if (!isHandcuffed())
            //     CuffedAPI.Handcuffing.removeHandcuffs(player);
        } else {
            if (playerAtt != null && playerAtt.hasModifier(ModAttributes.HANDCUFFED_ATTIRBUTE))
                playerAtt.removeModifier(ModAttributes.HANDCUFFED_ATTIRBUTE);
            server_wasHanging = false;
        }

        if(isDetained()==0) {
            player.teleportTo(detainedPos.x, detainedPos.y, detainedPos.z);
            player.setYRot(detainedRot);
        }

        if(server_getCuffingPlayer()!=null && isGettingHandcuffed()) {
            if(!CuffedMod.CONFIG.handcuffSettings.enableInteruptingHandcuffs) {
                progress = 42;
                needsSyncing = true;
            } else {
                progress += CuffedMod.CONFIG.handcuffSettings.interuptPhaseSpeed; // increase progress over time, untill the player is completely handcuffed.
                
                if (player.swinging)
                    interupt(player);
                else
                    needsSyncing = true;
            }
        }

        if (needsSyncing)
            CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), serializeNBT());
    }

    // #region Handcuffing
    /**
     * Get whether or not this player is currently completely handcuffed.
     * 
     * @return (boolean) True if this player is completely handcuffed.
     */
    public boolean isHandcuffed() {
        return progress >= 42 && handcuffed && !server_hasBrokenCuffs;
    }

    /**
     * Get whether or not this player is currently being put into handcuffs.
     * 
     * @return (boolean) True if this player is not completely handcuffed, but is
     *         currently being handcuffed.
     */
    public boolean isGettingHandcuffed() {
        return progress < 42 && handcuffed && !server_hasBrokenCuffs;
    }

    /**
     * Get whether or not this player is currently being put into handcuffs or is
     * already in handcuffs.
     * 
     * @return (boolean) True if this player is completely handcuffed, or is
     *         currently being handcuffed.
     */
    public boolean isGettingOrCurrentlyHandcuffed() {
        return isHandcuffed() || isGettingHandcuffed();
    }

    /**
     * Attempt to interupt applying handcuffs.
     */
    public void interupt(Player player) {
        // CuffedMod.LOGGER.info("Attempting to interupt application of cuffs"
        // + "\ncanInterupt = " + canInterupt);

        if (!isGettingHandcuffed() || server_hasBrokenCuffs)
            return;

        int maxProgress = 42;

        // within interuption interval
        if (progress > 0 && progress < maxProgress) {
            // automatically calculate the "safe zone" tick interval
            if (progress >= ((float) maxProgress / (21f / 11f)) && progress <= ((float) maxProgress / (21f / 16f))) {
                // play item break sound
                ((ServerLevel) player.level()).playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK,
                        SoundSource.PLAYERS, 1f, 1f);
                server_cuffingPlayer.getCooldowns().addCooldown(ModItems.HANDCUFFS.get(), 20);
                localSelf.awardStat(ModStatistics.HANDCUFFS_INTERUPTED.get());
                //CuffedAPI.Handcuffing.removePlayerAsCaptor(server_cuffingPlayer);
                server_removeHandcuffs();
            } else {
                server_getHandcuffedByPlayer(player);
            }
        }

        //CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), serializeNBT());
    }

    /**
     * Immedietely puts a player into handcuffs.
     * 
     * @param player (Player) The player to put in handcuffs.
     * @param source (Player) The player applying the handcuffs. Can be null
     */
    public void server_applyHandcuffs(Player player) {
        this.handcuffed = true;
        this.progress = 42;
        this.breakProgress = 0;

        player.awardStat(ModStatistics.TIMES_HANDCUFFED.get());
        // CuffedServer.addHandcuffed(player, PackageCuffedData());

        CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), serializeNBT());
    }

    /**
     * get fully handcuffed by a player "server_cuffingPlayer"
     * @param player the player getting handcuffed.
     */
    void server_getHandcuffedByPlayer(Player player) {
        // Fixes dupe glitch, if they are not holding cuffs, then find cuffs in their
        // inventory.
        boolean foundCuffs = false;
        if (!server_cuffingPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.HANDCUFFS.get()))
            for (int i = 0; i < server_cuffingPlayer.getInventory().items.size(); i++) {
                if (server_cuffingPlayer.getInventory().items.get(i).is(ModItems.HANDCUFFS.get())) {
                    server_cuffingPlayer.getInventory().items.get(i).shrink(1);
                    foundCuffs = true;
                    break;
                }
            }
        else {
            server_cuffingPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
            foundCuffs = true;
        }

        if (foundCuffs) {
            // If the handcuffer is still holding or has cuffs in their inventory, handcuff
            // the person and use a pair.
            progress = 42;
            ((ServerLevel) server_cuffingPlayer.level()).playSound(null, server_cuffingPlayer.blockPosition(), ModSounds.HANDCUFFED_SOUND,
                    SoundSource.PLAYERS, 1f, 1f);

            server_cuffingPlayer.awardStat(ModStatistics.PLAYERS_HANDCUFFED.get()); // award players handcuffed stat
            localSelf.awardStat(ModStatistics.TIMES_HANDCUFFED.get()); // award times handcuffed stat
            localSelf.awardStat(Stats.ITEM_USED.get(ModItems.HANDCUFFS.get())); // award handcuffs times used stat

            CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
        } else {
            // If the handcuffer has scrolled away, or lost their cuffs, then break out of
            // them.
            ((ServerLevel) player.level()).playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS, 1f, 1f);
            server_cuffingPlayer.getCooldowns().addCooldown(ModItems.HANDCUFFS.get(), 20);
            server_removeHandcuffs();
        }
        server_cuffingPlayer = null;
    }

    /**
     * Remove this player's handcuffs if they are handcuffed, does nothing
     * otherwise.
     */
    public void server_removeHandcuffs() {
        if (detained > -1)
            return; // Cannot remove cuffs from a detained player
        handcuffed = false;
        server_cuffingPlayer = null;
        server_canInterupt = false;
        server_setAnchor(null);
        if(!CuffedMod.CONFIG.handcuffSettings.persistantNickname)
            server_setNickname(null);
        softCuffed = false;
        progress = 0;

        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }

    /**
     * Get the player who is applying handcuffs to this player.
     * 
     * @return (boolean) The player who is applying the handcuffs, null if no one
     *         is.
     */
    public Player server_getCuffingPlayer() {
        return server_cuffingPlayer;
    }
    
    /**
     * Set the player who is applying handcuffs to this player.
     */
    public void server_setCuffingPlayer(Player player) {
        if (player != null) {
            handcuffed = true;
            server_cuffingPlayer = player;
            this.breakProgress = 4;
        } else {
            if (isGettingHandcuffed()) {
                handcuffed = false;
                server_cuffingPlayer = null;
            } else {
                // They were not getting handcuffed at the time, so reset just the cuffing player
                server_cuffingPlayer = player;
                progress = 0;
                this.breakProgress = 4;
            }
        }

        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }
    // #endregion

    // #region Soft-cuffing
    /**
     * Get whether or not this player is 'soft-cuffed' meaning they are handcuffed
     * and cannot interact, but can move freely.
     *
     * @return (boolean) Whether or not this player is soft-cuffed
     */
    public boolean isSoftCuffed() {
        return softCuffed || anchor > -1;
    }

    /**
     * Set this player as soft-cuffed or not.
     * 
     * @param value (boolean) The value to set.
     */
    public void server_setSoftCuffed(boolean value) {
        softCuffed = (anchor > -1) ? true : value;
        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }

    // #endregion

    // #region Anchoring
    public boolean isAnchored() {
        return (anchor > -1);
    }

    public Entity getAnchor() {
        if(localSelf!=null) {
            if (isAnchored()) {
                if(serverAnchor==null || anchor > -1) {
                    Entity e = localSelf.level().getEntity(anchor);
                    if(e!=null)
                        return e;
                } else if(localSelf.getServer()!=null && localSelf.level() instanceof ServerLevel l) 
                    return l.getEntity(serverAnchor);
            }
        }
        return null;
    }

    /**
     * For client-side use, gets the anchor based on it's synced ID on the client level provided.
     * @param level (Level) The level to get the anchor in.
     * @return
     */
    public Entity getAnchor(Level level) {
        if (isAnchored())
            return level.getEntity(anchor);
        
        return null;
    }


    public void server_setAnchor(Entity entity) {
        Entity oldAnchor = getAnchor();
        Entity newAnchor = null;

        if (oldAnchor != null && entity == null) {
            Vec3 mid = new Vec3((localSelf.getX() + oldAnchor.getX()) / 2, (localSelf.getY() + oldAnchor.getY()) / 2,
                    (localSelf.getZ() + oldAnchor.getZ()) / 2);

            ItemEntity itementity = new ItemEntity(localSelf.level(), mid.x, mid.y, mid.z,
                    new ItemStack(Items.CHAIN));

            itementity.setDefaultPickUpDelay();
            localSelf.level().addFreshEntity(itementity);

            if (oldAnchor instanceof ChainKnotEntity a) {
                a.removeChained(localSelf);

                if (a.getChained().size() <= 0) {
                    a.discard();
                }
            }
        }
        if (detained < 0) {
            if (entity != null && entity instanceof ChainKnotEntity a) {
                a.addChained(localSelf);
            }
            anchor = (entity != null) ? entity.getId() : -1;
            if(entity == null)
                serverAnchor = null;
            newAnchor = entity;
        }

        if(anchor>-1 && newAnchor != null)
            serverAnchor = newAnchor.getUUID();

        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }
    
    public void server_setAnchor(Entity entity, boolean ignoreNull) {
        Entity oldAnchor = getAnchor();
        Entity newAnchor = null;

        if (!ignoreNull && anchor > -1 && entity == null) {
            //Set anchor to null (remove anchor)
            Vec3 mid = new Vec3((localSelf.getX() + oldAnchor.getX()) / 2, (localSelf.getY() + oldAnchor.getY()) / 2,
                    (localSelf.getZ() + oldAnchor.getZ()) / 2);
            ItemEntity itementity = new ItemEntity(localSelf.level(), mid.x, mid.y, mid.z,
                    new ItemStack(Items.CHAIN));
            itementity.setDefaultPickUpDelay();
            localSelf.level().addFreshEntity(itementity);

            //CuffedMod.LOGGER.info("attempting to spawn chain item because\n entity -> " + entity + "\nOld anchor -> " + anchor);

            if (oldAnchor instanceof ChainKnotEntity a) {
                a.removeChained(localSelf);

                if (a.getChained().size() <= 0) {
                    a.discard();
                }
            }
        }
        if(detained < 0) {
            if (entity != null && entity instanceof ChainKnotEntity a) {
                a.addChained(localSelf);
            }
            anchor = (entity != null) ? entity.getId() : -1;
            newAnchor = entity;
        }

        if(anchor>-1 && newAnchor != null)
            serverAnchor = newAnchor.getUUID();
        if(newAnchor == null || anchor <= -1)
            serverAnchor = null;

        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }
    // #endregion

    // #region Detaining
    public int isDetained() {
        return detained;
    }

    public void server_setDetained(int v) {
        if(!isAnchored())
            server_setDetained(v, localSelf.position(), localSelf.getYRot());
    }

    /**
     * Set this player as detained
     * 
     * @param value
     * @param position
     * @param yRotation
     */
    public void server_setDetained(int value, Vec3 position, float yRotation) {
        detainedPos = position;
        detainedRot = yRotation;
        detained = value;

        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());
    }

    public Vec3 getDetainedPosition() {
        return detainedPos;
    }

    public float getDetainedRotation() {
        return detainedRot;
    }
    // #endregion
    
    // #region Nicknaming
    public Component getNickname() {
        return nickname;
    }

    public void server_setNickname(Component value) {
        nickname = value;
        CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT());        
    }
    // #endregion
    
    // #region Breaking handcuffs
    public int getBreakProgress() {
        return breakProgress;
    }

    public void server_setBreakProgress(int value) {
        if(!CuffedMod.CONFIG.handcuffSettings.enableHandcuffBreaking)
            return;

        if(value < breakProgress)
            localSelf.level().playSound(null, localSelf.blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS);
        else
            localSelf.level().playSound(null, localSelf.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS);

        breakProgress = Math.max(Math.min(value, 9), 0); //clamp the value to 9
        breakRegenDelayTimer = 30;

        if(breakProgress == 9) {
            localSelf.level().playSound(null, localSelf.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS);

            if(CuffedMod.CONFIG.handcuffSettings.breakingDeletesHandcuffs) {
                ItemEntity itementity = new ItemEntity(localSelf.level(), localSelf.getX(), localSelf.getY(), localSelf.getZ(), new ItemStack(ModItems.HANDCUFFS.get()));
                itementity.setDefaultPickUpDelay();
                localSelf.level().addFreshEntity(itementity);
            }

            localSelf.awardStat(ModStatistics.HANDCUFFS_BROKEN.get()); // award handcuffs broken stat

            server_removeHandcuffs();
        } else
            CuffedAPI.sendCuffedSyncPacketToClient(localSelf.getId(), localSelf.getUUID(), serializeNBT()); 
    }
    // #endregion
    //#endregion
    
    // #region Client Side Methods & Variables
    Player client_self;

    @Override
    public void client_joinWorld(Player player) {

    }

    @Override
    public void client_leaveWorld(Player player) {
    }

    @Override
    public void client_tick(Player player) {
        if (localSelf == null)
            localSelf = player;
        if (client_self == null)
            client_self = player;

        if (addedEffect) {
            player.removeEffect(MobEffects.JUMP);
            addedEffect = false;
        }

        double maxDist = CuffedMod.CONFIG.handcuffSettings.maxChainLength;

        if (isHandcuffed() && isAnchored()) {
            if (getAnchor() != null) {
                if (player.distanceTo(getAnchor()) > maxDist) {
                    float distance = player.distanceTo(getAnchor());

                    double dx = (getAnchor().getX() - player.getX()) / (double) distance;
                    double dy = (getAnchor().getY() - player.getY()) / (double) distance;
                    double dz = (getAnchor().getZ() - player.getZ()) / (double) distance;

                    player.setDeltaMovement(
                            Math.copySign(dx * dx * (distance / 5D) * .45, dx),
                            Math.copySign(dy * dy * (distance / 5D) * .45, dy),
                            Math.copySign(dz * dz * (distance / 5D) * .45, dz));
                }
            }
        }
    }

    boolean addedEffect = false;

    static final ResourceLocation chainedBar = new ResourceLocation(CuffedMod.MODID, "textures/gui/chained_bar.png");
    static final ResourceLocation cuffedWidgets = new ResourceLocation(CuffedMod.MODID, "textures/gui/widgets.png");

    @Override
    public void client_renderOverlay(Minecraft instance, LocalPlayer player, GuiGraphics graphics, float partialTick, Window window) {
        if(isGettingOrCurrentlyHandcuffed()) {
            player.getInventory().selected = 0;

            //Render hotbar overlay
            int screenWidth = 183;
            int screenHeight = 24;
            int x = (window.getGuiScaledWidth()/2) - (screenWidth/2);
            int y = (window.getGuiScaledHeight()) - (screenHeight) + 1;

            float bp = breakProgress / 9f;
            ScreenUtils.drawProgressBar(graphics, chainedBar, bp, 1, 9, x, y, screenWidth, screenHeight, 0, 0, 183, 24, 183, 216);

            if(isDetained()>-1 || isAnchored()) {
                screenWidth = (int)(20 * 1.75f);
                screenHeight = (int)(9*1.75f);
                x = (window.getGuiScaledWidth()/2) - (screenWidth/2);
                y = (window.getGuiScaledHeight()/2) - (screenHeight) - 50;
                ScreenUtils.drawTexture(graphics, cuffedWidgets, x, y, screenWidth, screenHeight, 24,24, 20, 9, 192, 192);       
            } else {
                screenWidth = (int)(24 * 1.75f);
                screenHeight = (int)(12*1.75f);
                x = (window.getGuiScaledWidth()/2) - (screenWidth/2);
                y = (window.getGuiScaledHeight()/2) - (screenHeight) - 50;
                ScreenUtils.drawTexture(graphics, cuffedWidgets, x, y, screenWidth, screenHeight, 0,24, 24, 12, 192, 192);       
            }

            if (progress < 42) {
                renderHandcuffedGUI(instance, graphics, 42, (int) Math.floor(progress));

                if (isGettingHandcuffed()) {
                    List<Component> list = new ArrayList<>();
                    list.add(Component.translatable("info.cuffed.inProgress"));
                    list.add(Component.literal("" + Math.round((progress / 42f) * 100f) + "/100"));
                    renderHandcuffedGUI(instance, graphics, list);
                }
            } else {
                if (!isSoftCuffed())
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 0, -10));

                player.hurtTime = 0;
                addedEffect = true;

                List<Component> list = new ArrayList<>();
                list.add(Component.translatable((detained > 0 || isAnchored()) ? (isAnchored() || isSoftCuffed() ? (isAnchored() ? "info.cuffed.anchored" : "info.cuffed.softcuffed") :  "info.cuffed.detained") : "info.cuffed.handcuffed"));
                renderHandcuffedGUI(instance, graphics, list);
            }
        }

        if(keypressCooldown>0)
            keypressCooldown--;
    }
    
    int lastKeyPressed = -1;
    float ph_bp = 0;
    float keypressCooldown = 0;
    @Override
    public void client_onKeyPressed(Minecraft instace, Key event) {
        if(!CuffedMod.CONFIG.handcuffSettings.enableHandcuffBreaking || !isHandcuffed() || isAnchored() || isDetained() > -1)
            return;

        if(getBreakProgress()<9 && keypressCooldown <= 0)
            if(event.getKey() == instace.options.keyLeft.getKey().getValue() || event.getKey() == instace.options.keyRight.getKey().getValue()) {
                if(lastKeyPressed!=event.getKey()) {
                    localSelf.playNotifySound(SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 1f, Mth.nextFloat(localSelf.getRandom(), 0.9f,1.1f));
                    ph_bp+= CuffedMod.CONFIG.handcuffSettings.handcuffBreakSpeed / 10;
                    if(ph_bp>1f) {
                        CuffedAPI.sendCuffedBreakOutPacketToServer(getBreakProgress()+1);
                        ph_bp = 0;
                        keypressCooldown=4; // max speed that can be clicked is 5 clicks a second, to prevent macros. (or a click every 4 ticks)
                    }
                }
            }
        lastKeyPressed = event.getKey();
    }
    // #endregion


    // #region Network Syncing Methods

    // #endregion


    // #region GUI rendering methods
    /**
     * Render a text header on the screen with the given Component list.
     */
    public static void renderHandcuffedGUI(Minecraft instance, GuiGraphics graphics, List<Component> list) {
        int space = 15;
        int width = 0;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            width = Math.max(width, instance.font.width(text) + 10);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            graphics.drawString(instance.font, text, instance.getWindow().getGuiScaledWidth() / 2 - instance.font.width(text) / 2,
                    instance.getWindow()
                            .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)) - 32,
                    16579836);
        }
        RenderSystem.enableDepthTest();
    }
    /**
     * Render a number range on the screen, (I.E. "0/100")
     */
    public static void renderHandcuffedGUI(Minecraft instance, GuiGraphics graphics, int maxTick, int curTick) {       
        int screenCenterX = instance.getWindow().getGuiScaledWidth() / 2;
        int screenCenterY = instance.getWindow().getGuiScaledHeight() / 2;

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
            */curFrame = (int) Math.floor((float) totalFrames / temp);
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
    /**
     * Render the GUI for lockpicking 
     * @throws Exception
     */
    public static void renderLockpickGUI(Minecraft instance, GuiGraphics graphics, int maxTick, int curTick) {
        int screenCenterX = instance.getWindow().getGuiScaledWidth() / 2;
        int screenCenterY = instance.getWindow().getGuiScaledHeight() / 2;

        int totalFrames = 31;
        int barScale = 64;

        int curFrame = 0;

        if (curTick > maxTick)
            curTick = maxTick;

        if (curTick == 0)
            curTick = 1;
        if (maxTick == 0)
            maxTick = 62;

        if (curTick != 0 && maxTick != 0) {
            float temp = ((float) maxTick / (float) curTick);
            if (temp < 1f)
                temp = 1f;
            curFrame = (int) Math.floor((float) totalFrames / temp);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        if (curFrame > CuffedMod.PICKLOCK_GUI.length - 1)
            curFrame = CuffedMod.PICKLOCK_GUI.length - 1;

        ResourceLocation rl1 = CuffedMod.PICKLOCK_GUI[curFrame];

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

        graphics.blitInscribed(rl1, screenCenterX - (barScale / 2), screenCenterY - (barScale / 2), barScale, barScale,
                64, 64, true, true); // bar

        RenderSystem.enableDepthTest();
    }
    // #endregion
}
