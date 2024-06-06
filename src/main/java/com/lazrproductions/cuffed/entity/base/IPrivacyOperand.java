package com.lazrproductions.cuffed.entity.base;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public interface IPrivacyOperand {

    public static final String RESTRAINING_RESTRICTION = CuffedMod.MODID + ":restraining";
    public static final String NICKNAMING_RESTRICTION = CuffedMod.MODID + ":nicknaming";
    public static final String ANCHORING_RESTRICTION = CuffedMod.MODID + ":anchoring";
    public static final String DETAINING_RESTRICTION = CuffedMod.MODID + ":detaining";

    public static final String TAG_RESTRICTIONS = "PrivacyRestrictions";
    public static final String TAG_RESTRICTION_ID = "Id";
    public static final String TAG_RESTRICTION = "RestrictionLevel";

    public void setRestrainingRestrictions(PrivacyRestriction newRestriction);

    public void setNicknamingRestrictions(PrivacyRestriction newRestriction);

    public void setAnchoringRestrictions(PrivacyRestriction newRestriction);

    public void setDetainingRestrictions(PrivacyRestriction newRestriction);

    void setRestriction(String key, PrivacyRestriction newRestriction);



    PrivacyRestriction getRestriction(String key);

    public PrivacyRestriction getRestrainingRestrictions();

    public PrivacyRestriction getNicknamingRestrictions();

    public PrivacyRestriction getAnchoringRestrictions();

    public PrivacyRestriction getDetainingRestrictions();

    ListTag serializeRestrictions();

    void deserializeRestrictions(CompoundTag tag);

    void writeDefaultRestrictions();

    public static enum PrivacyRestriction {
        ALWAYS,
        ASK,
        ONLY_WHEN_RESTRAINED,
        NEVER;

        public static PrivacyRestriction fromInteger(int integer) {
            switch (integer) {
                case 0:
                    return ALWAYS;
                case 1:
                    return ASK;
                case 2:
                    return ONLY_WHEN_RESTRAINED;
                default:
                    return NEVER;
            }
        }

    
        public int toInteger() {
            switch (this) {
                case ALWAYS:
                    return 0;
                case ASK:
                    return 1;
                case ONLY_WHEN_RESTRAINED:
                    return 2;
                default:
                    return 3;
            }
        }
    }
}
