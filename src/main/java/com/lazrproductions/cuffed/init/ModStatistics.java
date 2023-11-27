package com.lazrproductions.cuffed.init;

import java.util.ArrayList;
import java.util.List;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStatistics {
    private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(
			Registries.CUSTOM_STAT, CuffedMod.MODID
	);
	private static final List<Runnable> RUN_IN_SETUP = new ArrayList<>();

	public static final RegistryObject<ResourceLocation> PLAYERS_HANDCUFFED = registerCustomStat("players_handcuffed", StatFormatter.DEFAULT);
	public static final RegistryObject<ResourceLocation> TIMES_HANDCUFFED = registerCustomStat("times_handcuffed", StatFormatter.DEFAULT);
	public static final RegistryObject<ResourceLocation> HANDCUFFS_BROKEN = registerCustomStat("handcuffs_broken", StatFormatter.DEFAULT);
	public static final RegistryObject<ResourceLocation> HANDCUFFS_INTERUPTED = registerCustomStat("handcuffs_interupted", StatFormatter.DEFAULT);
	public static final RegistryObject<ResourceLocation> TIME_SPENT_HANDCUFFED = registerCustomStat("time_spent_handcuffed", StatFormatter.TIME);

	public static final RegistryObject<ResourceLocation> SUCCESSFUL_LOCKPICKS = registerCustomStat("successful_lockpicks", StatFormatter.DEFAULT);
	public static final RegistryObject<ResourceLocation> LOCKPICKS_BROKEN = registerCustomStat("lockpicks_broken", StatFormatter.DEFAULT);

	public static void register(IEventBus bus)
	{
		REGISTER.register(bus);
	}

	public static void setup()
	{
		RUN_IN_SETUP.forEach(Runnable::run);
	}

	private static RegistryObject<ResourceLocation> registerCustomStat(String name, StatFormatter formatter)
	{
		return REGISTER.register(name, () -> {
			ResourceLocation regName = new ResourceLocation(CuffedMod.MODID, name);
			RUN_IN_SETUP.add(() -> Stats.CUSTOM.get(regName, formatter));
			return regName;
		});
	}
}
