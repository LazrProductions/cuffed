package com.lazrproductions.cuffed.config;

import com.lazrproductions.lazrslib.common.config.ConfigCategory;
import com.lazrproductions.lazrslib.common.config.ConfigProperty;
import com.lazrproductions.lazrslib.common.config.LazrConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

public class CuffedServerConfig extends LazrConfig {

    public CuffedServerConfig(String name, Type type) {
        super(name, type);
    }


    public ConfigProperty<Integer> MAX_KEYS_PER_RING;
    public ConfigProperty<Integer> SAFE_SLOTS;
    public ConfigProperty<Boolean> REQUIRE_LOW_HEALTH_TO_RESTRAIN;
    public ConfigProperty<Boolean> ALLOW_BREAKING_OUT_OF_PILLORY;

    public ConfigCategory ANCHORING_SETTINGS;
    public ConfigProperty<Boolean> ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED;
    public ConfigProperty<Float> ANCHORING_MAX_CHAIN_LENGTH;
    public ConfigProperty<Float> ANCHORING_SUFFOCATION_LENGTH;
    public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_FENCES;
    public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS;
    public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS;

    public ConfigCategory NICKNAME_SETTINGS;
    public ConfigProperty<Boolean> NICKNAME_PERSISTS_ON_DEATH;
    public ConfigProperty<Boolean> NICKNAME_PERSISTS_ON_LOGOUT;

    public ConfigCategory LOCKPICKING_SETTINGS;
    public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_PADLOCKS;
    public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_PADLOCKS;
    public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS;
    public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS;
    public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_CELL_DOORS;
    public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_CELL_DOORS;
    public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_SAFES;
    public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_SAFES;

    public BreakableRestraintConfig HANDCUFFS_SETTINGS;
    public BreakableRestraintConfig FUZZY_HANDCUFFS_SETTINGS;
    public BreakableRestraintConfig SHACKLES_SETTINGS;

    public BreakableRestraintConfig LEGCUFFS_SETTINGS;
    public BreakableRestraintConfig LEG_SHACKLES_SETTINGS;



    @Override
    public void registerProperties() {
        MAX_KEYS_PER_RING = createGenericProperty(new ConfigProperty<Integer>(this, "Maximum Keys Per Ring", "The maximum number of keys that can fit on a single key ring.", 16));
        SAFE_SLOTS = createGenericProperty(new ConfigProperty<Integer>(this, "Safes Slots", "The total number of slots in the safe.", 36));

        REQUIRE_LOW_HEALTH_TO_RESTRAIN = createGenericProperty(new ConfigProperty<Boolean>(this, "Require Low Health To Restrain", "Whether or not to require players to be under 30% health to be restrained. If PlayerRevive is installed, then it requires players to be bleeding out. If a player is already restrained then this setting doesn't take effect.", false));

        ALLOW_BREAKING_OUT_OF_PILLORY = createGenericProperty(new ConfigProperty<Boolean>(this, "Allow breaking out of the pillory", "Whether or not to allow players to spam crouch to break out of pillories.", true));

        ANCHORING_SETTINGS = createCategory(new ConfigCategory(this, "Anchoring Settings"), (c) -> {
            ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED = c.putProperty(new ConfigProperty<Boolean>(this, "Only Restrained Players Can Be Restrained", "Whether or not to require players to be restrained to get anchored.", false));

            ANCHORING_MAX_CHAIN_LENGTH = c.putProperty(new ConfigProperty<Float>(this, "Max Chain Length", "The maximum length of the chain when anchoring.", 5.0F));
            ANCHORING_SUFFOCATION_LENGTH = c.putProperty(new ConfigProperty<Float>(this, "Suffocation Length", "The distance when anchored entites start suffocating.", 12.0F));

            ANCHORING_ALLOW_ANCHORING_TO_FENCES = c.putProperty(new ConfigProperty<Boolean>(this, "Allow Anchoring To Fences", "Whether or not players should be allowed to anchor entities to FENCES.", true));
            ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS = c.putProperty(new ConfigProperty<Boolean>(this, "Allow Anchoring To Tripwire Hook", "Whether or not players should be allowed to anchor entities to TRIPWIRE HOOKS.", true));
            ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS = c.putProperty(new ConfigProperty<Boolean>(this, "Allow Anchoring To Weighted Anchors", "Whether or not players should be allowed to anchor entities to WEIGHTED ANCHORS.", true));
        });

        NICKNAME_SETTINGS = createCategory(new ConfigCategory(this, "Nickname Settings"), (c) -> {
            NICKNAME_PERSISTS_ON_DEATH = c.putProperty(new ConfigProperty<Boolean>(this, "Nickname Persists On Death", "Whether or not nicknames should persist on death.", true));
            NICKNAME_PERSISTS_ON_LOGOUT = c.putProperty(new ConfigProperty<Boolean>(this, "Nickname Persists On Logout", "Whether or not nicknames should persist on logout.", true));
        });

        LOCKPICKING_SETTINGS = createCategory(new ConfigCategory(this, "Lockpicking Settings"), (c) -> {
            LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_PADLOCKS = c.putProperty(new ConfigProperty<Integer>(this, "Progress Per Pick For Breaking Padlocks", "How much progress is gained on a successfull pick when lockpicking a PADLOCK.", 8));
            LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_PADLOCKS = c.putProperty(new ConfigProperty<Integer>(this, "Speed Increase Per Pick For Breaking Padlocks", "How much the progress-loss speeds up per pick when lockpicking a PADLOCK.", 10));
            
            LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS = c.putProperty(new ConfigProperty<Integer>(this, "Progress Per Pick For Breaking Reinforced Padlocks", "How much progress is gained on a successfull pick when lockpicking a REINFORCED PADLOCK.", 6));
            LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS = c.putProperty(new ConfigProperty<Integer>(this, "Speed Increase Per Pick For Breaking Reinforced Padlocks", "How much the progress-loss speeds up per pick when lockpicking a REINFORCED PADLOCK.", 13));
            
            LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_CELL_DOORS = c.putProperty(new ConfigProperty<Integer>(this, "Progress Per Pick For Breaking Cell Doors", "How much progress is gained on a successfull pick when lockpicking a CELL DOOR.", 6));
            LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_CELL_DOORS = c.putProperty(new ConfigProperty<Integer>(this, "Speed Increase Per Pick For Breaking Cell Doors", "How much the progress-loss speeds up per pick when lockpicking a CELL DOOR.", 14));

            LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_SAFES = c.putProperty(new ConfigProperty<Integer>(this, "Progress Per Pick For Breaking Safes", "How much progress is gained on a successfull pick when lockpicking a SAFES.", 3));
            LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_SAFES = c.putProperty(new ConfigProperty<Integer>(this, "Speed Increase Per Pick For Breaking Safes", "How much the progress-loss speeds up per pick when lockpicking a SAFES.", 10));
        });

        HANDCUFFS_SETTINGS = (BreakableRestraintConfig)createCategory(new BreakableRestraintConfig(this, "Handcuff", true, false, true, 6, 12), (c)->{});
        FUZZY_HANDCUFFS_SETTINGS = (BreakableRestraintConfig)createCategory(new BreakableRestraintConfig(this, "Fuzzy Handcuff", true, false, true, 6, 14), (c)->{});
        SHACKLES_SETTINGS = (BreakableRestraintConfig)createCategory(new BreakableRestraintConfig(this, "Shackle", true, false, true, 8, 10), (c)->{});
        
        LEGCUFFS_SETTINGS = (BreakableRestraintConfig)createCategory(new BreakableRestraintConfig(this, "Legcuff", true, false, true, 6, 12), (c)->{});
        LEG_SHACKLES_SETTINGS = (BreakableRestraintConfig)createCategory(new BreakableRestraintConfig(this, "Leg Shackle", true, false, true, 8, 10), (c)->{});
    }


    public static class BreakableRestraintConfig extends ConfigCategory {
        public BreakableRestraintConfig(LazrConfig config, String name, boolean canBeBrokenOutOf, boolean dropItemWhenBroken, boolean lockpickable, int lockpickingProgressPerPick, int lockpickingSpeedIncreasePerPick) {
            super(config, name);
            
            this.canBeBrokenOutOf = this.putProperty(new ConfigProperty<Boolean>(config, "Can Be Broken Out Of", "Whether or not "+name+" can be broken out of.", canBeBrokenOutOf));
            this.dropItemWhenBroken = this.putProperty(new ConfigProperty<Boolean>(config, "Drop Item When Broken", "Whether or not "+name+"s drop as item when broken out of.", dropItemWhenBroken));
            this.lockpickable = this.putProperty(new ConfigProperty<Boolean>(config, "Drop Item When Broken", "Whether or not "+name+"s drop as item when broken out of.", lockpickable));
            this.lockpickingProgressPerPick = this.putProperty(new ConfigProperty<Integer>(config, "Drop Item When Broken", "Whether or not "+name+"s drop as item when broken out of.", lockpickingProgressPerPick));
            this.lockpickingSpeedIncreasePerPick = this.putProperty(new ConfigProperty<Integer>(config, "Drop Item When Broken", "Whether or not "+name+"s drop as item when broken out of.", lockpickingSpeedIncreasePerPick));
        } 

        public ConfigProperty<Boolean> canBeBrokenOutOf;
        public ConfigProperty<Boolean> dropItemWhenBroken;
        public ConfigProperty<Boolean> lockpickable;
        public ConfigProperty<Integer> lockpickingProgressPerPick;
        public ConfigProperty<Integer> lockpickingSpeedIncreasePerPick;
    }   
}
