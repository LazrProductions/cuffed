package com.lazrproductions.cuffed.api;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;

public interface ICuffedCapability {
    /**
     * Copy data from another capability to this one, the given capabilit must be the same type to copy it's data.
     * @param cap The other capability to copy from
     */
    public void copyFrom(ICuffedCapability cap);

    public CompoundTag serializeNBT();
    public void deserializeNBT(CompoundTag nbt);

    
    /**
     * Called on the server when this player joins the world.
     * @param player The player that is joining
     */
    public void server_joinWorld(ServerPlayer player);

    /**
     * Called on the server when this player leaves the world.
     * @param player The player that is leaving
     */
    public void server_leaveWorld(ServerPlayer player);

    /**
     * Called every tick on the server
     * 
     * @param player The (Server) player that is getting ticked
     */
    public void server_tick(ServerPlayer player);


    /**
     * Called on the logical client when this player joins the world.
     * @param player The client that is joining
     */
    public void client_joinWorld(Player player);

    /**
     * Called on the logical client when this player leaves the world.
     * @param player The client that is leaving
     */
    public void client_leaveWorld(Player player);

    /**
     * Called every tick only on the local client
     * 
     * @param player The client that is getting ticked
     */
    public void client_tick(Player player);

    /**
     * Render any overlay this capability may have onto the screen.
     */
    public void client_renderOverlay(Minecraft instance, LocalPlayer player, GuiGraphics graphics, float partialTick, Window window);

    /**
     * Called when the client pressed any key
     */
    public void client_onKeyPressed(Minecraft instace, InputEvent.Key event);
}
