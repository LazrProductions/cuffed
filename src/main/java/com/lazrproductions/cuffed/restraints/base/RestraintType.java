package com.lazrproductions.cuffed.restraints.base;

public enum RestraintType {
    Arm,
    Leg,
    Head;

    public int toInteger() {
        switch (this) {
            case Arm:
                return 0;
            case Leg:
                return 1;         
            default:
                return 2;
        }
    }
    public static RestraintType fromInteger(int value) {
        switch (value) {
            case 0:
                return Arm;
            case 1:
                return Leg;
            default:
                return Head;
        }
    }
}
