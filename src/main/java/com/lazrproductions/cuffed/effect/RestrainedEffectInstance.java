package com.lazrproductions.cuffed.effect;

import net.minecraft.resources.ResourceLocation;

public class RestrainedEffectInstance {

    public static final ResourceLocation NEGATIVE_MINE_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_mine_speed");
    public static final ResourceLocation NEGATIVE_MOVE_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_movement_speed");
    public static final ResourceLocation NEGATIVE_SWIM_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_swim_speed");

    public static int encodeRestraintProperties(boolean noMining, boolean noItemUse, boolean noMovement, boolean noJumping) {
        int encodedValue = 0;
        if (noMining) encodedValue |= 1 << 0;
        if (noItemUse) encodedValue |= 1 << 1;
        if (noMovement) encodedValue |= 1 << 2;
        if (noJumping) encodedValue |= 1 << 3;
        return encodedValue;
    }
    
    public static boolean[] decodeRestraintProperties(int v) {
        boolean[] decodedValues = new boolean[4];
        decodedValues[0] = (v & (1 << 0)) != 0;
        decodedValues[1] = (v & (1 << 1)) != 0;
        decodedValues[2] = (v & (1 << 2)) != 0;
        decodedValues[3] = (v & (1 << 3)) != 0;
        return decodedValues;
    }
    
    public static boolean decodeNoMining(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[0];
    }
    public static boolean decodeNoItemUse(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[1];
    }
    public static boolean decodeNoMovement(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[2];
    }
    public static boolean decodeNoJumping(int v) {
        boolean[] decodedValues = decodeRestraintProperties(v);
        return decodedValues[3];
    }
}
