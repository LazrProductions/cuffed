package com.lazrproductions.cuffed.restraints.base;

public enum RestraintType {
    Arm,
    Leg;

    public static int toInteger(RestraintType value) {
        switch (value) {
            case Arm:
                return 0;         
            default:
                return 1;
        }
    }
    public static RestraintType fromInteger(int value) {
        switch (value) {
            case 0:
                return Arm;
            default:
                return Leg;
        }
    }
}
