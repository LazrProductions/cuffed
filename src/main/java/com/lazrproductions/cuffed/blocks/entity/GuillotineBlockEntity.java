package com.lazrproductions.cuffed.blocks.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.compat.PlayerReviveCompat;
import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import com.lazrproductions.cuffed.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GuillotineBlockEntity extends BlockEntity {

    static final String TAG_IS_DOWN = "IsDown"; 
    static final String TAG_IS_BLOODY = "IsBloody"; 

    public boolean isBloody;
    public boolean isDown;

    public GuillotineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUILLOTINE.get(), pos, state);
    }

    public void interact(@Nonnull Level l, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if(!l.isClientSide()) {
            this.isDown = !isDown;
            l.playSound(null, pos, isDown ? ModSounds.GUILLOTINE_USE : ModSounds.PILLORY_USE, SoundSource.BLOCKS, 1, 1);
            
            if(isDown)
                chopDelay = 5;
            
            setChanged(l, pos, state);
            l.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void chop(@Nonnull Level l, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        Player player = PilloryBlock.getDetainedEntity(l, state, pos);
        if(player != null) {
            if(CuffedMod.PlayerReviveInstalled) {
                PlayerReviveCompat.Kill(player);
            } else
            player.kill();
                player.hurt(ModDamageTypes.GetModSource(player, ModDamageTypes.HANG, null), Float.MAX_VALUE);


            if(CuffedMod.SERVER_CONFIG.GUILLOTINE_DROPS_HEAD.get()) {
                ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
                stack.getOrCreateTag().putString("SkullOwner", player.getGameProfile().getName());
                ItemEntity entity = new ItemEntity(l, player.position().x(), player.position().y(), player.position().z(), stack);
                l.addFreshEntity(entity);
            }

            this.isBloody = true;
        }
    }

    int chopDelay = 0;
    public void tick(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if(chopDelay > 0) {
            chopDelay--;
            if(chopDelay == 0) {
                chop(level, pos, state);
            }
        }
    }

    
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean(TAG_IS_DOWN, isDown);
        tag.putBoolean(TAG_IS_BLOODY, isBloody);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);

        isDown = tag.getBoolean(TAG_IS_DOWN);
        isBloody = tag.getBoolean(TAG_IS_BLOODY);
    }


    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}
