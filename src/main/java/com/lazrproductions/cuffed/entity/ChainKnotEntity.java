package com.lazrproductions.cuffed.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.server.CuffedServer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;

public class ChainKnotEntity extends HangingEntity {

    public ChainKnotEntity(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    public ChainKnotEntity(Level world, BlockPos pos) {
        super(ModEntityTypes.CHAIN_KNOT.get(), world, pos);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);
    }

    public ArrayList<Player> chainedPlayers = new ArrayList<>();

    public ArrayList<Player> getChained() {
        return chainedPlayers;
    }

    public Player getChained(int index) {
        return chainedPlayers.get(index);
    }

    public boolean getChained(Player player) {
        return chainedPlayers.contains(player);
    }

    public void addChained(Player player) {
        chainedPlayers.add(player);
    }

    public void removeChained(Player player) {
        chainedPlayers.remove(player);
        if(chainedPlayers.size()<=0)
            this.discard();
    }

    public void removeChained(int index) {
        chainedPlayers.remove(index);
        if(chainedPlayers.size()<=0)
            this.discard();
    }

    public void clearChained() {
        chainedPlayers.clear();
        if(chainedPlayers.size()<=0)
            this.discard();
    }

    /**
     * Get whether or not this knot is on a fence block.
     * 
     * @return (boolean) True if this knot is on a fence block.
     */
    public boolean isOnFence() {
        return !this.level().getBlockState(pos).is(BlockTags.FENCES);
    }

    @Override
    public void dropItem(@Nullable Entity p_31837_) {
        this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
        for (int i = 0; i < chainedPlayers.size(); i++) {
            IHandcuffed cuffed = CuffedServer.getHandcuffed(chainedPlayers.get(i));
            cuffed.setAnchor(null);
        }
    }

    @Override
    public InteractionResult interact(Player interactor, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            boolean flag = false;
            List<Player> list = this.level().getEntitiesOfClass(Player.class,
                    new AABB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D,
                            this.getY() + 7.0D, this.getZ() + 7.0D));

            for (Player player : list) {
                IHandcuffed cuffed = CuffedServer.getHandcuffed(player);
                if (cuffed.getAnchor() == interactor) {
                    cuffed.setAnchor(interactor);
                    flag = true;
                }
            }

            boolean flag1 = false;
            if (!flag) {
                this.discard();
                for (Player player : list) {
                    IHandcuffed cuffed = CuffedServer.getHandcuffed(player);
                    if (cuffed.isChained() && cuffed.getAnchor() == this) {
                        cuffed.setAnchor(null);
                        flag1 = true;
                    }
                }
            }

            if (flag || flag1) {
                this.gameEvent(GameEvent.BLOCK_ATTACH, interactor);
            }

            return InteractionResult.CONSUME;
        }
    }

    public static ChainKnotEntity getOrCreateKnot(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (ChainKnotEntity leashfenceknotentity : level.getEntitiesOfClass(ChainKnotEntity.class,
                new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D,
                        (double) k + 1.0D))) {
            if (leashfenceknotentity.getPos().equals(pos)) {
                return leashfenceknotentity;
            }
        }

        ChainKnotEntity leashfenceknotentity1 = new ChainKnotEntity(level, pos);
        level.addFreshEntity(leashfenceknotentity1);
        return leashfenceknotentity1;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, (double) Mth.floor(y) + 0.5D, (double) Mth.floor(z) + 0.5D);
    }

    @Override
    protected void setDirection(Direction pFacingDirection) {
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    protected float getEyeHeight(Pose p_31839_, EntityDimensions p_31840_) {
        return 0.0625F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_31835_) {
        return p_31835_ < 1024.0D;
    }

    public void syncAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ArrayList<Player> c = new ArrayList<>();
        ListTag a = tag.getList("AnchoredPlayers", 10);
        for (int i = 0; i < a.size(); i++)
            if(a.getCompound(i).hasUUID("UUID"))
                if(this.level().getPlayerByUUID(a.getCompound(i).getUUID("UUID"))!=null)
                    c.add(this.level().getPlayerByUUID(a.getCompound(i).getUUID("UUID")));
        this.chainedPlayers = c;
    }

    @Override
    public boolean survives() {
        return this.level().getBlockState(this.pos).is(BlockTags.FENCES)
                || this.level().getBlockState(this.pos).is(Blocks.TRIPWIRE_HOOK);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, 0, this.getPos());
    }

    @Override
    public Vec3 getRopeHoldPosition(float partialTick) {
        return this.getPosition(partialTick).add(getLeashOffset(partialTick));
    }

    @Override
    public Vec3 getLeashOffset(float partialTick) {
        return new Vec3(0.0D, (double) getEyeHeight() + (!isOnFence() ? 0.2D : 0D), 0.0D);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.CHAIN);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.375D,
                (double) this.pos.getZ() + 0.5D);
        double d0 = (double) this.getType().getWidth() / 2.0D;
        double d1 = (double) this.getType().getHeight();
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0,
                this.getY() + d1, this.getZ() + d0));
    }
}
