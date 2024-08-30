package com.lazrproductions.cuffed.items.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Predicate;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.IRestrainableCapability;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.restraints.Restraints;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class AbstractRestraintItem extends Item {
    public AbstractRestraintItem(Properties p) {
        super(p);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.UNBREAKING)
            return true;
        if (enchantment == Enchantments.BINDING_CURSE)
            return true;
        if (enchantment == ModEnchantments.IMBUE.get())
            return true;
        if (enchantment == ModEnchantments.FAMINE.get())
            return true;
        if (enchantment == ModEnchantments.SHROUD.get())
            return true;
        if (enchantment == ModEnchantments.EXHAUST.get())
            return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    public static boolean dispenseRestraint(BlockSource source, ItemStack stack) {


        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        
        
        Predicate<Entity> restraintSelector = new PlayerCanEquipArmRestraintEntitySelector(stack);
        RestraintType typeToEquip = RestraintType.Arm; 
        if(stack.getItem() instanceof AbstractLegRestraintItem) {
            restraintSelector = new PlayerCanEquipLegRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Leg;
        } else if(stack.getItem() instanceof AbstractHeadRestraintItem) {
            restraintSelector = new PlayerCanEquipHeadRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Head;
        } else if(stack.is(Items.BUNDLE) && BundleItem.getFullnessDisplay(stack) <= 0) {
            restraintSelector = new PlayerCanEquipHeadRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Head;
        } 


        List<Player> list = source.getLevel().getEntitiesOfClass(Player.class, new AABB(blockpos),
                EntitySelector.NO_SPECTATORS.and(restraintSelector));
        if (list.isEmpty()) {
            return false;
        } else {
            ServerPlayer player = (ServerPlayer)list.get(0);
            RestrainableCapability entity = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(player);
            ItemStack itemstack = stack.copyWithCount(1);

            AbstractRestraint restraint = Restraints.GetRestraintFromStack(itemstack, player, player);

            if(typeToEquip == RestraintType.Arm)
                return entity.TryEquipRestraint(player, player, (AbstractArmRestraint)restraint);
            else if(typeToEquip == RestraintType.Leg)
                return entity.TryEquipRestraint(player, player, (AbstractLegRestraint)restraint);
            else if(typeToEquip == RestraintType.Head)
                return entity.TryEquipRestraint(player, player, (AbstractHeadRestraint)restraint);
            else return false;
        }
    }

    public static class PlayerCanEquipArmRestraintEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;

        public PlayerCanEquipArmRestraintEntitySelector(ItemStack stack) {
            this.itemStack = stack;
        }

        public boolean test(@Nullable Entity entity) {
            if(entity == null){
                return false;
            } else if (!entity.isAlive()) {
                return false;
            } else if (!(itemStack.getItem() instanceof AbstractArmRestraintItem)) {
                return false;
            } else if (entity instanceof ServerPlayer a) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(a);
                return !cap.armsRestrained();
            } else
                return false;
        }
    }
    public static class PlayerCanEquipLegRestraintEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;

        public PlayerCanEquipLegRestraintEntitySelector(ItemStack stack) {
            this.itemStack = stack;
        }

        public boolean test(@Nullable Entity entity) {
            if(entity == null) {
                return false;
            } else if (!entity.isAlive()) {
                return false;
            } else if (!(itemStack.getItem() instanceof AbstractLegRestraintItem)) {
                return false;
            } else if (entity instanceof ServerPlayer a) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(a);
                return !cap.legsRestrained();
            } else
                return false;
        }
    }
    public static class PlayerCanEquipHeadRestraintEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;

        public PlayerCanEquipHeadRestraintEntitySelector(ItemStack stack) {
            this.itemStack = stack;
        }

        public boolean test(@Nullable Entity entity) {
            if(entity == null) {
                return false;
            } else if (!entity.isAlive()) {
                return false;
            } else if ((!(itemStack.getItem() instanceof AbstractHeadRestraintItem)) && !(itemStack.is(Items.BUNDLE) && BundleItem.getFullnessDisplay(itemStack) <= 0)) {
                return false;
            } else if (entity instanceof ServerPlayer a) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(a);
                return !cap.headRestrained();
            } else
                return false;
        }
    }
}
