package com.lazrproductions.cuffed.packet;

import java.util.UUID;

import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.server.CuffedServer;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.network.CreativePacket;


public class CuffedUpdatePacket extends CreativePacket {
    
    public UUID uuid;
    public CompoundTag nbt;
    
    public CuffedUpdatePacket(Player player) {
        this.nbt = CuffedServer.getHandcuffed(player).serializeNBT();
        this.uuid = player.getUUID();
    }
    
    public CuffedUpdatePacket() {
        
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void executeClient(Player player) {
        Minecraft inst = Minecraft.getInstance();
        Level level = inst.level;
        if (level != null) {
            Player member = level.getPlayerByUUID(uuid);
            if (member != null) {
                IHandcuffed handcuffed = CuffedServer.getHandcuffed(member);
                handcuffed.deserializeNBT(nbt);
            }
        }
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        
    }
    
}