package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.custom.BundleRestraint;
import com.lazrproductions.cuffed.restraints.custom.DuckTapeArmsRestraint;
import com.lazrproductions.cuffed.restraints.custom.DuckTapeHeadRestraint;
import com.lazrproductions.cuffed.restraints.custom.DuckTapeLegsRestraint;
import com.lazrproductions.cuffed.restraints.custom.FuzzyHandcuffsRestraint;
import com.lazrproductions.cuffed.restraints.custom.HandcuffsArmsRestraint;
import com.lazrproductions.cuffed.restraints.custom.HandcuffsLegsRestraint;
import com.lazrproductions.cuffed.restraints.custom.PilloryRestraint;
import com.lazrproductions.cuffed.restraints.custom.ShacklesArmsRestraint;
import com.lazrproductions.cuffed.restraints.custom.ShacklesLegsRestraint;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ModRestraints {
    private static boolean isInitialized = false;

    public static final DeferredRegister<AbstractRestraint> RESTRAINTS = DeferredRegister.create(new ResourceLocation(CuffedMod.MODID, "restraints"), CuffedMod.MODID);

    public static final RegistryObject<AbstractRestraint> BUNDLE = RESTRAINTS.register("bundle", BundleRestraint::new);
    public static final RegistryObject<AbstractRestraint> PILLORY = RESTRAINTS.register("pillory", PilloryRestraint::new);
    public static final RegistryObject<AbstractRestraint> DUCK_TAPE_HEAD = RESTRAINTS.register("duck_tape_head", DuckTapeHeadRestraint::new);

    public static final RegistryObject<AbstractRestraint> HANDCUFFS_ARMS = RESTRAINTS.register("handcuffs_arms", HandcuffsArmsRestraint::new);
    public static final RegistryObject<AbstractRestraint> SHACKLES = RESTRAINTS.register("shackles_arms", ShacklesArmsRestraint::new);
    public static final RegistryObject<AbstractRestraint> DUCK_TAPE_ARMS = RESTRAINTS.register("duck_tape_arms", DuckTapeArmsRestraint::new);

    public static final RegistryObject<AbstractRestraint> HANDCUFFS_LEGS = RESTRAINTS.register("handcuffs_legs", HandcuffsLegsRestraint::new);
    public static final RegistryObject<AbstractRestraint> SHACKLES_LEGS = RESTRAINTS.register("shackles_legs", ShacklesLegsRestraint::new);
    public static final RegistryObject<AbstractRestraint> DUCK_TAPE_LEGS = RESTRAINTS.register("duck_tape_legs", DuckTapeLegsRestraint::new);


    // Supporter only restraints:
    public static final RegistryObject<AbstractRestraint> FUZZY_HANDCUFFS = RESTRAINTS.register("fuzzy_handcuffs", FuzzyHandcuffsRestraint::new);



    public static void register(final IEventBus modEventBus) {
        if (isInitialized) {
            throw new IllegalStateException("Restraints already initialized");
        }
        RESTRAINTS.makeRegistry(RegistryBuilder::new);
        RESTRAINTS.register(modEventBus);
        isInitialized = true;
        CuffedMod.LOGGER.info("Registered restraints");
    }
}
