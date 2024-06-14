package com.lazrproductions.cuffed.config;

import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class CuffedCommonConfig {
    @CreativeConfig(name = "Max Keys Per Ring", type = ConfigSynchronization.UNIVERSAL)
    public int maxKeysPerRing = 16;
    @CreativeConfig(name = "Slots In Safe", type = ConfigSynchronization.UNIVERSAL)
    public int safeSlots = 36;


    @CreativeConfig(name = "Anchoring Configuration")
    public AnchoringSettings anchoringSettings = new AnchoringSettings();


    @CreativeConfig(name = "Lockpicking Settings")
    public LockpickingSettings lockpickingSettings = new LockpickingSettings();
    

    @CreativeConfig(name = "Nickname Settings")
    public NicknameSettings nicknameSettings = new NicknameSettings();


    @CreativeConfig(name = "Handcuffs Configuration")
    public BreakableRestraintConfig handcuffsConfig = new BreakableRestraintConfig(false, true, 6, 12);
    @CreativeConfig(name = "Fuzzy Handcuffs Configuration")
    public BreakableRestraintConfig fuzzyHandcuffsConfig = new BreakableRestraintConfig(false, true, 6, 14);
    @CreativeConfig(name = "Shackles Configuration")
    public BreakableRestraintConfig shacklesConfig = new BreakableRestraintConfig(false, true, 8, 10);

    @CreativeConfig(name = "Legcuffs Configuration")
    public BreakableRestraintConfig legcuffsConfig = new BreakableRestraintConfig(false, true, 6, 12);
    @CreativeConfig(name = "Leg Shackles Configuration")
    public BreakableRestraintConfig legShacklesConfig = new BreakableRestraintConfig(false, true, 8, 10);

    
    
    public static class AnchoringSettings {
        @CreativeConfig(name = "Maximum Chain Length", type = ConfigSynchronization.UNIVERSAL)
        public float maxChainLength = 5.0F;
        @CreativeConfig(name = "Chain Suffocate Length", type = ConfigSynchronization.UNIVERSAL)
        public float chainSuffocateLength = 12.0F;

        @CreativeConfig(name = "Allow Anchoring to Fences", type = ConfigSynchronization.UNIVERSAL)
        public boolean allowAnchoringToFences = true;
        @CreativeConfig(name = "Allow Anchoring to Tripwire Hooks", type = ConfigSynchronization.UNIVERSAL)
        public boolean allowAnchoringToTripwireHook = true;
        @CreativeConfig(name = "Allow Anchoring to Weighted Anchors", type = ConfigSynchronization.UNIVERSAL)
        public boolean allowAnchoringToWeightedAnchors = true;
    }

    public static class NicknameSettings {
        @CreativeConfig(name = "Nickname Persists on Death", type =  ConfigSynchronization.UNIVERSAL)
        public boolean nicknamePersistsOnDeath = true;

        @CreativeConfig(name = "Nickname Persists on Logout", type =  ConfigSynchronization.UNIVERSAL)
        public boolean nicknamePersistsOnLogout = true;
    }
    
    public static class LockpickingSettings {
        @CreativeConfig(name = "Progress Per Pick For Breaking Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int progressPerPickForBreakingPadlocks = 8;
        @CreativeConfig(name = "Lockpicking Speed Increase Per Pick For Breaking Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int speedIncreasePerPickForBreakingPadlocks = 10;
        

        @CreativeConfig(name = "Progress Per Pick For Breaking Reinforced Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int progressPerPickForBreakingReinforcedPadlocks = 6;
        @CreativeConfig(name = "Lockpicking Speed Increase Per Pick For Breaking Reinforced Padlocks", type = ConfigSynchronization.UNIVERSAL)
        public int speedIncreasePerPickForBreakingReinforcedPadlocks = 13;

        
        @CreativeConfig(name = "Progress Per Pick For Breaking Cell Doors", type = ConfigSynchronization.UNIVERSAL)
        public int progressPerPickForBreakingCellDoors = 6;
        @CreativeConfig(name = "Lockpicking Speed Increase Per Pick For Breaking Cell Doors", type = ConfigSynchronization.UNIVERSAL)
        public int speedIncreasePerPickForBreakingCellDoors = 14;

        
        @CreativeConfig(name = "Progress Per Pick For Breaking Safes", type = ConfigSynchronization.UNIVERSAL)
        public int progressPerPickForBreakingSafes = 3;
        @CreativeConfig(name = "Lockpicking Speed Increase Per Pick For Breaking Safes", type = ConfigSynchronization.UNIVERSAL)
        public int speedIncreasePerPickForBreakingSafes = 10;
    }

    public static class BreakableRestraintConfig  {
        public BreakableRestraintConfig() {
            dropItemWhenBroken = false;
            lockpickable = true;
            lockpickingProgressPerPick = 6;
            lockpickingSpeedIncreasePerPick = 12;
        }
        public BreakableRestraintConfig(boolean default_dropItemWhenBroken, boolean lockpickable, int lockpickingProgressPerPick, int lockpickingSpeedIncreasePerPick) {
            this.dropItemWhenBroken = default_dropItemWhenBroken; 
            this.lockpickable = lockpickable;
            this.lockpickingProgressPerPick = lockpickingProgressPerPick;
            this.lockpickingSpeedIncreasePerPick = lockpickingSpeedIncreasePerPick;
        } 

        @CreativeConfig(name = "Drop Item When Broken", type = ConfigSynchronization.UNIVERSAL)
        public boolean dropItemWhenBroken = false;

        @CreativeConfig(name = "Can be Lockpicked", type = ConfigSynchronization.UNIVERSAL)
        public boolean lockpickable = true;
        @CreativeConfig(name = "Lockpicking Progress Per Pick", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingProgressPerPick = 6;
        @CreativeConfig(name = "Lockpicking Speed Increase Per Pick", type = ConfigSynchronization.UNIVERSAL)
        public int lockpickingSpeedIncreasePerPick = 12;
    }
}
