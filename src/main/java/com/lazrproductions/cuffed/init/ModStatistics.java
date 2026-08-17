package com.lazrproductions.cuffed.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.custom.FuzzyHandcuffsRestraint;
import com.lazrproductions.cuffed.restraints.custom.HandcuffsArmsRestraint;
import com.lazrproductions.cuffed.restraints.custom.HandcuffsLegsRestraint;
import com.lazrproductions.cuffed.restraints.custom.ShacklesArmsRestraint;
import com.lazrproductions.cuffed.restraints.custom.ShacklesLegsRestraint;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModStatistics {
    private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(
			Registries.CUSTOM_STAT, CuffedMod.MODID
	);
	private static final List<Runnable> RUN_IN_SETUP = new ArrayList<>();


	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> HANDCUFFS_TIME_RESTRAINED = registerCustomStat("handcuffs_times_restrained", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> HANDCUFFS_BROKEN = registerCustomStat("handcuffs_broken", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> HANDCUFFS_TIME_SPENT_RESTRAINED = registerCustomStat("handcuffs_time_spent_restrained", StatFormatter.TIME);

	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> FUZZY_HANDCUFFS_TIME_RESTRAINED = registerCustomStat("fuzzy_handcuffs_times_restrained", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> FUZZY_HANDCUFFS_BROKEN = registerCustomStat("fuzzy_handcuffs_broken", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> FUZZY_HANDCUFFS_TIME_SPENT_RESTRAINED = registerCustomStat("fuzzy_handcuffs_time_spent_restrained", StatFormatter.TIME);

	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> SHACKLES_TIME_RESTRAINED = registerCustomStat("shackles_times_restrained", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> SHACKLES_BROKEN = registerCustomStat("shackles_broken", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> SHACKLES_TIME_SPENT_RESTRAINED = registerCustomStat("shackles_time_spent_restrained", StatFormatter.TIME);


	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEGCUFFS_TIME_RESTRAINED = registerCustomStat("legcuffs_times_restrained", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEGCUFFS_BROKEN = registerCustomStat("legcuffs_broken", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEGCUFFS_TIME_SPENT_RESTRAINED = registerCustomStat("legcuffs_time_spent_restrained", StatFormatter.TIME);

	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEG_SHACKLES_TIME_RESTRAINED = registerCustomStat("leg_shackles_times_restrained", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEG_SHACKLES_BROKEN = registerCustomStat("leg_shackles_broken", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LEG_SHACKLES_TIME_SPENT_RESTRAINED = registerCustomStat("leg_shackles_time_spent_restrained", StatFormatter.TIME);
	

	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> TIMES_NICKNAMED = registerCustomStat("times_nicknamed", StatFormatter.DEFAULT);


	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> SUCCESSFUL_LOCKPICKS = registerCustomStat("successful_lockpicks", StatFormatter.DEFAULT);
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> LOCKPICKS_BROKEN = registerCustomStat("lockpicks_broken", StatFormatter.DEFAULT);

	
	public static final net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> OPEN_SAFE = registerCustomStat("open_safe", StatFormatter.DEFAULT);


	public static void register(IEventBus bus)
	{
		REGISTER.register(bus);
	}

	public static void setup()
	{
		RUN_IN_SETUP.forEach(Runnable::run);
	}

	private static net.neoforged.neoforge.registries.DeferredHolder<ResourceLocation, ResourceLocation> registerCustomStat(String name, StatFormatter formatter)
	{
		return REGISTER.register(name, () -> {
			ResourceLocation regName = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID, name);
			RUN_IN_SETUP.add(() -> Stats.CUSTOM.get(regName, formatter));
			return regName;
		});
	}


	public static void awardRestraintItemUsed(@Nonnull ServerPlayer player, @Nonnull ItemStack stack) {
		player.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
	}
	public static void awardRestrained(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint restraint) {
		if(restraint instanceof HandcuffsArmsRestraint)
			player.awardStat(HANDCUFFS_TIME_RESTRAINED.get(), 1);
		else if(restraint instanceof FuzzyHandcuffsRestraint)
			player.awardStat(FUZZY_HANDCUFFS_TIME_RESTRAINED.get(), 1);
		else if(restraint instanceof ShacklesArmsRestraint)
			player.awardStat(SHACKLES_TIME_RESTRAINED.get(), 1);
		else if(restraint instanceof HandcuffsLegsRestraint)
			player.awardStat(LEGCUFFS_TIME_RESTRAINED.get(), 1);
		else if(restraint instanceof ShacklesLegsRestraint)
			player.awardStat(LEG_SHACKLES_TIME_RESTRAINED.get(), 1);
	}
	public static void awardRestraintBroken(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint restraint) {
		if(restraint instanceof HandcuffsArmsRestraint)
			player.awardStat(HANDCUFFS_BROKEN.get(), 1);
		else if(restraint instanceof FuzzyHandcuffsRestraint)
			player.awardStat(FUZZY_HANDCUFFS_BROKEN.get(), 1);
		else if(restraint instanceof ShacklesArmsRestraint)
			player.awardStat(SHACKLES_BROKEN.get(), 1);
		else if(restraint instanceof HandcuffsLegsRestraint)
			player.awardStat(LEGCUFFS_BROKEN.get(), 1);
		else if(restraint instanceof ShacklesLegsRestraint)
			player.awardStat(LEG_SHACKLES_BROKEN.get(), 1);
	}
	public static void awardTimeSpentRestrained(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint restraint) {
		if(restraint instanceof HandcuffsArmsRestraint)
			player.awardStat(HANDCUFFS_TIME_SPENT_RESTRAINED.get(), 1);
		else if(restraint instanceof FuzzyHandcuffsRestraint)
			player.awardStat(FUZZY_HANDCUFFS_TIME_SPENT_RESTRAINED.get(), 1);
		else if(restraint instanceof ShacklesArmsRestraint)
			player.awardStat(SHACKLES_TIME_SPENT_RESTRAINED.get(), 1);
		else if(restraint instanceof HandcuffsLegsRestraint)
			player.awardStat(LEGCUFFS_TIME_SPENT_RESTRAINED.get(), 1);
		else if(restraint instanceof ShacklesLegsRestraint)
			player.awardStat(LEG_SHACKLES_TIME_SPENT_RESTRAINED.get(), 1);
	}
}
