package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class ModSounds {
    public static final SoundEvent HANDCUFFED_SOUND = SoundEvent
        .createVariableRangeEvent(new ResourceLocation(CuffedMod.MODID, "apply_handcuffs"));
    public static final SoundEvent PILLORY_USE_SOUND = SoundEvent
            .createVariableRangeEvent(new ResourceLocation(CuffedMod.MODID, "block.pillory.use"));

    public static void register(RegisterEvent event) {
        event.register(Keys.SOUND_EVENTS, x -> {
            x.register(new ResourceLocation(CuffedMod.MODID, "apply_handcuffs"), HANDCUFFED_SOUND);
            x.register(new ResourceLocation(CuffedMod.MODID, "block.pillory.use"), PILLORY_USE_SOUND);
        });
    }
}
