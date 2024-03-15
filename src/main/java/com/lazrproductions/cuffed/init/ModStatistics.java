package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

public class ModStatistics {
	public static ResourceLocation PLAYERS_HANDCUFFED_LOCATION = new ResourceLocation(CuffedMod.MODID, "players_handcuffed");
	public static Stat<ResourceLocation> PLAYERS_HANDCUFFED;
	public static ResourceLocation TIMES_HANDCUFFED_LOCATION = new ResourceLocation(CuffedMod.MODID, "times_handcuffed");
	public static Stat<ResourceLocation> TIMES_HANDCUFFED;
	public static ResourceLocation HANDCUFFS_BROKEN_LOCATION = new ResourceLocation(CuffedMod.MODID, "handcuffs_broken");
	public static Stat<ResourceLocation> HANDCUFFS_BROKEN;
	public static ResourceLocation HANDCUFFS_INTERUPTED_LOCATION = new ResourceLocation(CuffedMod.MODID, "handcuffs_interupted");
	public static Stat<ResourceLocation> HANDCUFFS_INTERUPTED;
	public static ResourceLocation TIME_SPENT_HANDCUFFED_LOCATION = new ResourceLocation(CuffedMod.MODID, "time_spent_handcuffed");
	public static Stat<ResourceLocation> TIME_SPENT_HANDCUFFED;

	public static ResourceLocation SUCCESSFUL_LOCKPICKS_LOCATION = new ResourceLocation(CuffedMod.MODID, "successful_lockpicks");
	public static Stat<ResourceLocation> SUCCESSFUL_LOCKPICKS;
	public static ResourceLocation LOCKPICKS_BROKEN_LOCATION = new ResourceLocation(CuffedMod.MODID, "lockpicks_broken");
	public static Stat<ResourceLocation> LOCKPICKS_BROKEN;

	public static void register() {
		Registry.register(Registry.CUSTOM_STAT, PLAYERS_HANDCUFFED_LOCATION, PLAYERS_HANDCUFFED_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, TIMES_HANDCUFFED_LOCATION, TIMES_HANDCUFFED_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, HANDCUFFS_BROKEN_LOCATION, HANDCUFFS_BROKEN_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, HANDCUFFS_INTERUPTED_LOCATION, HANDCUFFS_INTERUPTED_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, TIME_SPENT_HANDCUFFED_LOCATION, TIME_SPENT_HANDCUFFED_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, SUCCESSFUL_LOCKPICKS_LOCATION, SUCCESSFUL_LOCKPICKS_LOCATION);
		Registry.register(Registry.CUSTOM_STAT, LOCKPICKS_BROKEN_LOCATION, LOCKPICKS_BROKEN_LOCATION);
	}

	public static void load() {
		PLAYERS_HANDCUFFED = Stats.CUSTOM.get(PLAYERS_HANDCUFFED_LOCATION);
		TIMES_HANDCUFFED = Stats.CUSTOM.get(TIMES_HANDCUFFED_LOCATION);
		HANDCUFFS_BROKEN = Stats.CUSTOM.get(HANDCUFFS_BROKEN_LOCATION);
		HANDCUFFS_INTERUPTED = Stats.CUSTOM.get(HANDCUFFS_INTERUPTED_LOCATION);
		TIME_SPENT_HANDCUFFED = Stats.CUSTOM.get(TIME_SPENT_HANDCUFFED_LOCATION);
		SUCCESSFUL_LOCKPICKS = Stats.CUSTOM.get(SUCCESSFUL_LOCKPICKS_LOCATION);
		LOCKPICKS_BROKEN = Stats.CUSTOM.get(LOCKPICKS_BROKEN_LOCATION);
	}
}
