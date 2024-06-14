package com.lazrproductions.cuffed.compat;

import net.bettercombat.client.BetterCombatClient;

public class BetterCombatCompat {
    public static void load() {
    }

    static boolean wasEnabled;
    static boolean wasRecentlyDisabled;
    
    public static void Disable() {
        if(wasRecentlyDisabled)
            return;
        try {
            wasEnabled = BetterCombatClient.ENABLED;
            wasRecentlyDisabled = true;
            BetterCombatClient.ENABLED = false;
        } catch (Exception e) {
            return;
        }
    }

    public static void Reset() {
        if(!wasRecentlyDisabled)
            return;
        wasRecentlyDisabled = false;
        try {
            BetterCombatClient.ENABLED = wasEnabled;
        } catch (Exception e) {
            return;
        }
    }
}
