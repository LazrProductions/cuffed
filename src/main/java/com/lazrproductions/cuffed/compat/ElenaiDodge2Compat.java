package com.lazrproductions.cuffed.compat;

import com.elenai.elenaidodge2.client.ED2ClientStorage;

public class ElenaiDodge2Compat {
    public static void load() {
    }

    static boolean wasEnabled = false;
    static boolean wasDisabledRecently = false;

    public static void Disable() {
        ED2ClientStorage.setCooldown(Integer.MAX_VALUE);
        wasDisabledRecently = true;
    }

    public static void Reset() {
        if(!wasDisabledRecently)
            return;
        ED2ClientStorage.setCooldown(10);
    }
}
