package com.lazrproductions.cuffed.blocks.base;

import javax.annotation.Nonnull;

import com.lazrproductions.cuffed.items.PosterBlockItem;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public enum PosterType implements StringRepresentable {
    NONE,
    SERENITY,
    SKELETON,
    IMPUNITY,
    ZOOOM,
    A_SHADOW_LOCKED_AWAY,
    PRISONER,
    LANTERN;

    public String toString() {
        return this.getSerializedName();
    }
    public int toInt() {
        switch (this) {
            case SERENITY:
                return 1;
            case SKELETON:
                return 2;
            case IMPUNITY:
                return 3;
            case ZOOOM:
                return 4;
            case A_SHADOW_LOCKED_AWAY:
                return 5;
            case PRISONER:
                return 6;
            case LANTERN:
                return 7;            
            default:
                return 0;
        }
    }

    public PosterType next() {
        int t = this.toInt();
        t++;
        if(t > 7)
            t = 0;
        return fromInt(t);
    }

    public static PosterType fromString(String value) {
        switch (value) {
            case "serenity":
                return SERENITY;
            case "skeleton":
                return SKELETON;
            case "impunity":
                return IMPUNITY;
            case "zooom":
                return ZOOOM;
            case "a_shadow_locked_away":
                return A_SHADOW_LOCKED_AWAY;
            case "prisoner":
                return PRISONER;
            case "lantern":
                return LANTERN;
            default:
                return NONE;
        }
    }
    public static PosterType fromInt(int value) {
        switch (value) {
            case 1:
                return SERENITY;
            case 2:
                return SKELETON;
            case 3:
                return IMPUNITY;
            case 4:
                return ZOOOM;
            case 5:
                return A_SHADOW_LOCKED_AWAY;
            case 6:
                return PRISONER;
            case 7:
                return LANTERN;
            default:
                return NONE;
        }
    }
    
    public String getSerializedName() {
        switch (this) {
            case SERENITY:
                return "serenity";
            case SKELETON:
                return "skeleton";
            case IMPUNITY:
                return "impunity";
            case ZOOOM:
                return "zooom";
            case A_SHADOW_LOCKED_AWAY:
                return "a_shadow_locked_away";
            case PRISONER:
                return "prisoner";
            case LANTERN:
                return "lantern";  
            default:
                return "none";
        }
    }

    public static PosterType getfromItem(@Nonnull ItemStack stack) {
        return PosterBlockItem.getPosterType(stack);
    }
}
