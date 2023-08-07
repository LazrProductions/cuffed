package com.lazrproductions.cuffed.cap;

import java.util.UUID;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.config.ModCommonConfigs;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.packet.HandcuffingPacket;
import com.lazrproductions.cuffed.server.CuffedServer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class Handcuffed implements IHandcuffed {

    public Handcuffed(ServerPlayer p) {
        serverPlayer = p;
    }

    boolean valid;

    public ServerPlayer serverPlayer;

    private boolean handcuffed;
    private float progress = 0;

    public Player self;

    public Player cuffingPlayer = null;

    public boolean canInterupt;

    public boolean shouldShowGraphic;

    public boolean hasBrokenCuffs;

    // Chaining and soft cuffs
    public boolean softCuffed;
    public Entity anchor;

    int suffocateAir;
    int suffocateTick;
    boolean isSuffocating;
    boolean wasHanging;

    public Handcuffed() {
    }

    @Override
    public void tick(ServerPlayer player) {
        valid = true;

        if (self == null)
            self = player;

        if (cuffingPlayer != null) {
            if (hasBrokenCuffs)
                cuffingPlayer = null;
            else if (progress >= 42) {
                cuffingPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                ((ServerLevel) player.level()).playSound(null, player.blockPosition(), CuffedMod.HANDCUFFED_SOUND,
                        SoundSource.PLAYERS, 1f, 1f);
                cuffingPlayer = null;
            }
        }

        if (anchor != null) {
            double maxDist = ModCommonConfigs.MAX_CHAIN_LENGTH.get();

            if (self.distanceTo(anchor) > maxDist - 2 && self.getY() < anchor.getY() - 1.5F
                    && !self.onGround()) {
                if (!wasHanging) {
                    suffocateTick = 20;
                    suffocateAir = self.getMaxAirSupply();
                    wasHanging = true;
                }

                if (wasHanging && suffocateAir > -16)
                    suffocateAir -= 2;

                if (suffocateAir <= 0 && suffocateTick == 0) {
                    self.hurt(ModDamageTypes.GetModSource(self, ModDamageTypes.HANG, null), 2);
                }

                self.setAirSupply(suffocateAir);
                suffocateTick--;
                if (suffocateTick < 0)
                    suffocateTick = 20;
            } else
                wasHanging = false;
        }

        if (cuffingPlayer == null) {
            shouldShowGraphic = false;
            // if(progress>0)
            // {
            // removeHandcuffs();
            // }

        } else if (applyingHandcuffs()) {
            shouldShowGraphic = true;
            progress += ModCommonConfigs.INTERUPT_PHASE_SPEED.get(); // increase progress over time, untill the player is completely handcuffed.
            
            if (player.swinging)
                interupt(player);
        } else {
            shouldShowGraphic = false;
        }

        if (hasBrokenCuffs && progress > 0) {
            progress = 0;
        }

        if (hasBrokenCuffs && !isGettingOrCurrentlyCuffed())
            hasBrokenCuffs = false;

        SendUpdatePacket();
    }

    @Override
    public void reset() {
        valid = false;

        handcuffed = false;
        progress = 0;

        cuffingPlayer = null;
        canInterupt = false;
        shouldShowGraphic = false;
        hasBrokenCuffs = false;
        softCuffed = false;

        setAnchor(null);

        SendUpdatePacket();

        self = null;
    }

    @Override
    public void interupt(Player player) {
        CuffedMod.LOGGER.info("Attempting to interupt application of cuffs"
                + "\ncanInterupt     = " + canInterupt);

        if (!applyingHandcuffs() || hasBrokenCuffs)
            return;

        int maxProgress = 42;

        // within interuption interval
        if (progress > 0 && progress < maxProgress) {
            // automatically calculate the "safe zone" tick interval
            if (progress >= ((float) maxProgress / (21f / 11f)) && progress <= ((float) maxProgress / (21f / 16f))) {
                // play item break sound
                ((ServerLevel) player.level()).playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK,
                        SoundSource.PLAYERS, 1f, 1f);
                cuffingPlayer.getCooldowns().addCooldown(ModItems.HANDCUFFS.get(), 20);
                hasBrokenCuffs = true;
                removeHandcuffs();

            } else {
                cuffingPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                progress = 42;
                ((ServerLevel) player.level()).playSound(null, player.blockPosition(), CuffedMod.HANDCUFFED_SOUND,
                        SoundSource.PLAYERS, 1f, 1f);
            }
        }

        SendUpdatePacket();
    }

    @Override
    public float getProgress() {
        return progress;
    }

    /**
     * Get whether or not this player is not handcuffed whatsoever.
     * 
     * @return (boolean) True if this player is not being handcuffed and they are
     *         not in handcuffs.
     */
    @Override
    public boolean uncuffed() {
        return !handcuffed;
    }

    /**
     * Get whether or not this player is currently being put into handcuffs.
     * 
     * @return (boolean) True if this player is not completely handcuffed, but is
     *         currently being handcuffed.
     */
    @Override
    public boolean applyingHandcuffs() {
        return progress < 42 && handcuffed && !hasBrokenCuffs;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("progress", progress);
        nbt.putBoolean("handcuffed", handcuffed);
        nbt.putBoolean("softcuffed", softCuffed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        progress = nbt.getFloat("progress");
        handcuffed = nbt.getBoolean("handcuffed");
        softCuffed = nbt.getBoolean("softcuffed");

        SendUpdatePacket();
    }

    /**
     * Get whether or not this player is currently completely handcuffed.
     * 
     * @return (boolean) True if this player is completely handcuffed.
     */
    @Override
    public boolean isHandcuffed() {
        return progress >= 42 && handcuffed && !hasBrokenCuffs;
    }

    /**
     * Immedietely puts a player into handcuffs.
     * 
     * @param player (Player) The player to put in handcuffs.
     * @param source (Player) The player applying the handcuffs. Can be null
     */
    @Override
    public void applyHandcuffs(Player player) {
        this.handcuffed = true;
        this.progress = 42;

        self = player;
        CuffedServer.addHandcuffed(player);

        SendUpdatePacket();
    }

    /**
     * Remove this player's handcuffs if they are handcuffed, does nothing
     * otherwise.
     */
    @Override
    public void removeHandcuffs() {
        this.handcuffed = false;
        this.cuffingPlayer = null;
        this.canInterupt = false;
        setAnchor(null);
        this.softCuffed = false;
        this.progress = 0;

        CuffedServer.removeHandcuffed(self);

        SendUpdatePacket();
    }

    @Override
    public Player cuffingPlayer() {
        return cuffingPlayer;
    }

    /**
     * Set the handcuffing player of this person.
     * 
     * @param player (Player) The player that is applying the handcuffs.<br>
     *               </br>
     *               If null and was being handcuffed, then resets the player and
     *               removes the cuffs.
     */
    @Override
    public void setCuffingPlayer(Player player) {
        if (player != null) {
            handcuffed = true;
            cuffingPlayer = player;
        } else {
            if (applyingHandcuffs()) {
                handcuffed = false;
                cuffingPlayer = null;
            } else {
                // They were not getting handcuffed at the time, so reset just the cuffing
                // player
                cuffingPlayer = player;
                progress = 0;
            }
        }
    }

    @Override
    public Player getSelf() {
        return self;
    }

    @Override
    public boolean getShouldShowGraphic() {
        return shouldShowGraphic;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isGettingOrCurrentlyCuffed() {
        return isHandcuffed() || applyingHandcuffs();
    }

    @Override
    public Entity getAnchor() {
        if (isChained())
            return anchor;
        return null;
    }

    @Override
    public void setAnchor(Entity entity) {
        Entity oldAnchor = anchor;

        if (anchor != null && entity == null) {
            Vec3 mid = new Vec3((self.getX() + anchor.getX()) / 2, (self.getY() + anchor.getY()) / 2,
                    (self.getZ() + anchor.getZ()) / 2);

            CuffedMod.LOGGER.info("is this bineg called?");
            ItemEntity itementity = new ItemEntity(self.level(), mid.x, mid.y, mid.z,
                    new ItemStack(Items.CHAIN));

            itementity.setDefaultPickUpDelay();
            self.level().addFreshEntity(itementity);

            if (anchor instanceof ChainKnotEntity a) {
                a.removeChained(self);

                if (a.getChained().size() <= 0) {
                    a.discard();
                }
            }
        }
        if (entity != null && entity instanceof ChainKnotEntity a) {
            a.addChained(self);
        }
        anchor = entity;

        if (oldAnchor != anchor) {
            CuffedMod.LOGGER.info("Changing anchor.");
            if (anchor == null)
                CuffedServer.removeChainedFrom(self);
            else
                CuffedServer.addChainedPair(self, anchor);
        }


        SendUpdatePacket();
    }

    @Override
    public void setAnchor(Entity entity, boolean ignoreNull) {
        Entity oldAnchor = anchor;


        if (!ignoreNull && anchor != null && entity == null) {
            Vec3 mid = new Vec3((self.getX() + anchor.getX()) / 2, (self.getY() + anchor.getY()) / 2,
                    (self.getZ() + anchor.getZ()) / 2);
            ItemEntity itementity = new ItemEntity(self.level(), mid.x, mid.y, mid.z,
                    new ItemStack(Items.CHAIN));
            itementity.setDefaultPickUpDelay();
            self.level().addFreshEntity(itementity);

            CuffedMod.LOGGER
                    .info("attempting to spawn chain item because\n entity -> " + entity + "\nOld anchor -> " + anchor);

            if (anchor instanceof ChainKnotEntity a) {
                a.removeChained(self);

                if (a.getChained().size() <= 0) {
                    a.discard();
                }
            }
        }
        if (entity != null && entity instanceof ChainKnotEntity a) {
            a.addChained(self);
        }
        CuffedMod.LOGGER.info("Set anchor lol.");
        anchor = entity;

        if (oldAnchor != anchor) {
            CuffedMod.LOGGER.info("Changing anchor.");
            if (anchor == null)
                CuffedServer.removeChainedFrom(self);
            else
                CuffedServer.addChainedPair(self, anchor);
        }

        SendUpdatePacket();
    }

    @Override
    public boolean isChained() {
        return (anchor != null);
    }

    @Override
    public boolean isSoftCuffed() {
        return softCuffed || anchor != null;
    }

    @Override
    public void setSoftCuffed(boolean value) {
        softCuffed = anchor != null ? true : value;
        SendUpdatePacket();
    }

    @Override
    public void SendUpdatePacket() {
        if (serverPlayer == null) {
            CuffedMod.LOGGER.warn("ServerPlayer player is not set! (com.lazrproductions.cuffed.cap.Handcuffed:314)");
            return;
        }
        UUID u = null;
        if (cuffingPlayer != null)
            u = cuffingPlayer.getUUID();
        int a = -1;
        if (anchor != null)
            a = anchor.getId();
        CuffedMod.NETWORK.sendToClient(new HandcuffingPacket(u, getShouldShowGraphic(), isHandcuffed(),
                applyingHandcuffs(), isSoftCuffed(), isChained(), a, progress), serverPlayer);
    }

    @Override
    public void SetServerPlayer(ServerPlayer player) {
        serverPlayer = player;
    }
}
