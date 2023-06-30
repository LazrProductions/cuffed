package com.lazrproductions.cuffed.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHandcuffed extends INBTSerializable<CompoundTag> {
    
    public void tick(ServerPlayer player);
    
    /**
     * Get whether or not this player is currently completely handcuffed.
     * @return (boolean) True if this player is completely handcuffed.
     */
    public boolean isHandcuffed();
    
    public float getProgress();

    /**
     * Immedietely puts a player into handcuffs.
     * @param player (Player) The player to put in handcuffs.
     * @param source (Player) The player applying the handcuffs. Can be null
    */
    public void applyHandcuffs(Player player);
    
    /**
     * Attempt to interupt applying handcuffs.
     */
    public void interupt(Player player);

    /**
     * Get whether or not this player is not handcuffed whatsoever.
     * @return (boolean) True if this player is not being handcuffed and they are not in handcuffs.
     */
    public boolean uncuffed();
    
    /**
     * Get whether or not this player is currently being put into handcuffs.
     * @return (boolean) True if this player is not completely handcuffed, but is currently being handcuffed.
     */
    public boolean applyingHandcuffs();

    /**
    * Remove this player's handcuffs if they are handcuffed, does nothing otherwise.
    */
    public void removeHandcuffs();
    
    public Player cuffingPlayer();
    

    /**
    * Set the handcuffing player of this person.
    * @param player (Player) The player that is applying the handcuffs.<br></br>If null and was being handcuffed, then resets the player and removes the cuffs.
    */
    public void setCuffingPlayer(Player player);

    public Player getSelf();

    public boolean getShouldShowGraphic();

    public boolean isValid();

    public void reset();

    public boolean isGettingOrCurrentlyCuffed();

    /**
     * Whether or not the player can walk around while cuffed.
     * @return (boolean) True if the player is soft cuffed. 
     */
    public boolean isSoftCuffed();

    /**
     * Set this player as softCuffed.\n(!) The player is always soft cuffed if they are anchored.
     * @param value the value to set soft cuffed to.
     */
    public void setSoftCuffed(boolean value);

    /**
     * Get whether the player is currently chained to a fence, or being led by chain by a player.
     * @return (boolean) True if the player is chained to a fence or is  being led by chain by a player.
     */
    public boolean isChained();

    /**
     * If the player is anchored.
     * If this player is chained to a fence, get the entity they are chained to.
     * If not returns the player who is leading them.
     * @return (Player) The entity of the anchor.
     */
    public Entity getAnchor();

    /**
     * Set this player's anchor, if input is null then remove the current anchor and do some special events.
     * @param entity (Entity) The new anchor to set.
     */
    public void setAnchor(Entity entity);

    /**
     * Set this player's anchor, if input is null then remove the current anchor and do some special events if "ignoreNull" is false.
     * @param entity (Entity) The new anchor to set.
     * @param ignoreNull (boolean) Whether or not to ignore a special events when entity is null.
     */
    public void setAnchor(Entity entity, boolean ignoreNull);


    /**
     * Sends an update packet to the given client to update the client-side variables.
     * @param player (Player) The player to send the packet to.
     */
    public void SendUpdatePacket();

    public void SetServerPlayer(ServerPlayer player);
}
