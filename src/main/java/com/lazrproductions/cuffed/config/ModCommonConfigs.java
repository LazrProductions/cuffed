package com.lazrproductions.cuffed.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Handcuffing
    // interupt phase speed
    public static final ForgeConfigSpec.ConfigValue<Float> INTERUPT_PHASE_SPEED;

    // Chaining
    // max Chain length
    public static final ForgeConfigSpec.ConfigValue<Float> MAX_CHAIN_LENGTH;

    // Keys
    // max keys on a single ring.
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_KEYS_ON_RING;

    // Lockpicking
    // speed increase percent per phase 0-100
    public static final ForgeConfigSpec.ConfigValue<Integer> LOCKPICK_SPEED_INCREASE_PER_PHASE;
    // phases for breaking handcuffs
    public static final ForgeConfigSpec.ConfigValue<Integer> BREAK_HANDCUFFS_PHASES;
    // phases for breaking padlock
    public static final ForgeConfigSpec.ConfigValue<Integer> BREAK_PADLOCK_PHASES;
    // phases for breaking reinforced padlock
    public static final ForgeConfigSpec.ConfigValue<Integer> BREAK_REINFORCED_PADLOCK_PHASES;

    static {
        MAX_KEYS_ON_RING = BUILDER.comment("The maximum amount of keys that can be put on a single key ring. DEFAULT: 16").comment("[Integer]")
                .define("Max Keys Per Key Ring", 16);

        BUILDER.push("Handcuff Settings");

                INTERUPT_PHASE_SPEED = BUILDER.comment("The speed of the interupt phase when getting handcuffed. DEFAULT: 4.0").comment("[Float]")
                        .define("Interupt Phase Speed", 4.0f);

                MAX_CHAIN_LENGTH = BUILDER.comment("The maximum distance, in blocks, that a chained player can get from their anchor. DEFAULT: 5.0").comment("[Float]")
                        .define("Maximum Chain Length", 5.0f);

        BUILDER.pop();

        BUILDER.push("Lockpicking Settings");

                LOCKPICK_SPEED_INCREASE_PER_PHASE = BUILDER.comment("The speed increase percentage per phase completed when lockpicking. DEFAULT: 20").comment("[Integer]")
                        .define("Lockpicking Speed Increase Per Phase", 20);

                BREAK_HANDCUFFS_PHASES = BUILDER.comment("The amount of lockpicking phases it takes to break someone's handcuffs. DEFAULT: 4").comment("[Integer]")
                        .define("Lockpicking Phases For Breaking Handcuffs", 4);
                BREAK_PADLOCK_PHASES = BUILDER.comment("The amount of lockpicking phases it takes to break a padlock. DEFAULT: 7").comment("[Integer]")
                        .define("Lockpicking Phases For Breaking Padlocks", 7);
                BREAK_REINFORCED_PADLOCK_PHASES = BUILDER.comment("The amount of lockpicking phases it takes to break a reinforced padlock. DEFAULT: 14").comment("[Integer]")
                        .define("Lockpicking Phases For Breaking Reinforced Padlocks", 14);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
