package com.lazrproductions.cuffed.config;

import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class CuffedCommonConfig {
    @CreativeConfig(name = "Max Keys Per Ring", type = ConfigSynchronization.UNIVERSAL)
    public int maxKeysPerRing = 16;

    @CreativeConfig(name = "Handcuff Settings")
    public HandcuffSettings handcuffSettings = new HandcuffSettings();

    @CreativeConfig(name = "Lockpicking Settings")
    public LockpickingSettings lockpickingSettings = new LockpickingSettings();

    public static class HandcuffSettings {
        
        @CreativeConfig(name = "Enable Interupting While Getting Handcuffed", type =  ConfigSynchronization.UNIVERSAL)
        public boolean enableInteruptingHandcuffs = true;

        @CreativeConfig(name = "Interupt Phase Speed", type = ConfigSynchronization.UNIVERSAL)
        public float interuptPhaseSpeed = 4.0F;



        @CreativeConfig(name = "Enable Breaking Out of Handcuffs", type =  ConfigSynchronization.UNIVERSAL)
        public boolean enableHandcuffBreaking = true;

        @CreativeConfig(name = "Handcuff Break Out Speed", type =  ConfigSynchronization.UNIVERSAL)
        public float handcuffBreakSpeed = 0.5f;

        @CreativeConfig(name = "Handcuff Break Out Progress Regeneration Per Second", type =  ConfigSynchronization.UNIVERSAL)
        public float handcuffHealPerSecond = 0.76f;

        @CreativeConfig(name = "Breaking Out Destroys Handcuffs", type =  ConfigSynchronization.UNIVERSAL)
        public boolean breakingDeletesHandcuffs = true;



        @CreativeConfig(name = "Maximum Chain Length", type = ConfigSynchronization.UNIVERSAL)
        public float maxChainLength = 5.0F;


        @CreativeConfig(name = "Requires in Handcuffs to Nickname", type =  ConfigSynchronization.UNIVERSAL)
        public boolean requireHandcuffedToNickname = false;
        @CreativeConfig(name = "Nickname Persists Without Handcuffs", type =  ConfigSynchronization.UNIVERSAL)
        public boolean persistantNickname = false;
    }

    public static class LockpickingSettings {
        @CreativeConfig(name = "Lockpicking Speed Increase Per Phase", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingSpeedIncreasePerPhase = 20;

        @CreativeConfig(name = "Phases For Breaking Handcuffs", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingPhasesForBreakingHandcuffs = 4;

        @CreativeConfig(name = "Phases For Breaking Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingPhasesForBreakingPadlocks = 7;
        
        @CreativeConfig(name = "Phases For Breaking Reinforced Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingPhasesForBreakingReinforcedPadlocks = 14;
    }
}
