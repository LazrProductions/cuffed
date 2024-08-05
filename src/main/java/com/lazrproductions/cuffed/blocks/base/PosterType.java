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
    ZOOOM;

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
            default:
                return 0;
        }
    }

    public PosterType next() {
        int t = this.toInt();
        t++;
        if(t > 4)
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
            default:
                return "none";
        }
    }

    public static PosterType getfromItem(@Nonnull ItemStack stack) {
        return PosterBlockItem.getPosterType(stack);
    }
}
