package com.lazrproductions.cuffed.items.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Predicate;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.gui.screens.Screen;

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


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> components,
            @Nonnull TooltipFlag tooltipFlag) {


        Client.ShowExtendedInfo(components);

            
        super.appendHoverText(stack, level, components, tooltipFlag);
    }
    
    
    public static boolean dispenseRestraint(BlockSource source, ItemStack stack) {
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        
        
        Predicate<Entity> restraintSelector = new PlayerCanEquipArmRestraintEntitySelector(stack);
        RestraintType typeToEquip = RestraintType.Arm;
        boolean isAmbiguousRestraint = false;

        if(stack.getItem() instanceof AbstractHeadRestraintItem) {
            restraintSelector = new PlayerCanEquipHeadRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Head;
        } else if(stack.getItem() instanceof AbstractArmRestraintItem) {
            restraintSelector = new PlayerCanEquipArmRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Arm;
        } else if(stack.getItem() instanceof AbstractLegRestraintItem) {
            restraintSelector = new PlayerCanEquipLegRestraintEntitySelector(stack);
            typeToEquip = RestraintType.Leg;
        } else if(stack.getItem() instanceof AbstractRestraintItem) {
            restraintSelector = new PlayerEntitySelector(stack);
            isAmbiguousRestraint = true;
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

            if(isAmbiguousRestraint) {
                if((player.position().y() + 1d) > (double)blockpos.getY())  {
                    // dispenser is lower than player's waist
                    typeToEquip = RestraintType.Leg;
                } else {
                    // dispenser is higher than player's waist
                    typeToEquip = RestraintType.Arm;
                }

                AbstractRestraint restraint = RestraintAPI.getRestraintFromStack(itemstack, typeToEquip, player, player);

                if(typeToEquip == RestraintType.Arm && restraint instanceof AbstractArmRestraint && !entity.armsRestrained())
                    return entity.TryEquipRestraint(player, player, (AbstractArmRestraint)restraint);
                else if(typeToEquip == RestraintType.Leg && restraint instanceof AbstractLegRestraint && !entity.legsRestrained())
                    return entity.TryEquipRestraint(player, player, (AbstractLegRestraint)restraint);

                return false;
            }
            else if(typeToEquip == RestraintType.Arm && !entity.armsRestrained()) {
                AbstractRestraint restraint = RestraintAPI.getRestraintFromStack(itemstack, typeToEquip, player, player);
                return entity.TryEquipRestraint(player, player, (AbstractArmRestraint)restraint);
            }
            else if(typeToEquip == RestraintType.Leg && !entity.legsRestrained()) {
                AbstractRestraint restraint = RestraintAPI.getRestraintFromStack(itemstack, typeToEquip, player, player);
                return entity.TryEquipRestraint(player, player, (AbstractLegRestraint)restraint);
            }
            else if(typeToEquip == RestraintType.Head && !entity.headRestrained()) {
                AbstractRestraint restraint = RestraintAPI.getRestraintFromStack(itemstack, typeToEquip, player, player);
                return entity.TryEquipRestraint(player, player, (AbstractHeadRestraint)restraint);
            }
            else return false;
        }
    }

    public static class PlayerEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;

        public PlayerEntitySelector(ItemStack stack) {
            this.itemStack = stack;
        }

        public boolean test(@Nullable Entity entity) {
            if(entity == null){
                return false;
            } else if (!entity.isAlive()) {
                return false;
            } else if (!(itemStack.getItem() instanceof AbstractRestraintItem)) {
                return false;
            } else if (entity instanceof ServerPlayer a) {
                IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(a);
                return cap != null;
            } else
                return false;
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


    public static class Client {
        @OnlyIn(Dist.CLIENT)
        public static void ShowExtendedInfo(@Nonnull List<Component> components) {
            components.add(Component.empty());
            if(Screen.hasShiftDown())
                components.add(Component.translatable("info.cuffed.restraint_type.extra").withStyle(ChatFormatting.WHITE));
            else
                components.add(Component.translatable("info.cuffed.restraint_type.showextra").withStyle(ChatFormatting.GRAY));
        }
    }
}
