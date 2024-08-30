package com.lazrproductions.cuffed.compat;

//import com.alrex.parcool.config.ParCoolConfig;

public class ParcoolCompat {
    public static void load() {
    }

    static boolean wasEnabled = false;
    static boolean wasDisabledRecently = false;

    // public static void Disable() {
    //     if(wasDisabledRecently)
    //         return;
    //     try {
    //         wasEnabled = ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
    //         if(wasEnabled) {
    //             wasDisabledRecently = true;
    //             ParCoolConfig.Client.Booleans.ParCoolIsActive.set(false);
    //         }
    //     } catch(Exception e) {
    //         return;
    //     }
    // }

    // public static void Reset() {
    //     if(!wasDisabledRecently)
    //         return;
    //     wasDisabledRecently = false;
    //     try{
    //         ParCoolConfig.Client.Booleans.ParCoolIsActive.set(wasEnabled);
    //     } catch(Exception e) {
    //         return;
    //     }
    // }
}
