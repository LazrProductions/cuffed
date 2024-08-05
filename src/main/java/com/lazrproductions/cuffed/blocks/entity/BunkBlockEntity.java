package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BunkBlockEntity extends BlockEntity { 
    public BunkBlockEntity(BlockPos pos, BlockState state) {
       super(ModBlockEntities.BUNK_BLOCK_ENTITY.get(), pos, state);
    }
 
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
       return ClientboundBlockEntityDataPacket.create(this);
    }
 }