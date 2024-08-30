package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

public class ModSounds {
    public static final SoundEvent HANDCUFFED = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "restraint.apply_handcuffs"));
    public static final SoundEvent SHACKLES_EQUIP = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "restraint.apply_shackles"));
    public static final SoundEvent PILLORY_USE = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "block.pillory.use"));
    public static final SoundEvent GUILLOTINE_USE = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "block.guillotine.use"));
    
    public static final SoundEvent SAFE_OPEN = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "block.safe.open"));
    public static final SoundEvent SAFE_CLOSE = new SoundEvent(new ResourceLocation(CuffedMod.MODID, "block.safe.close"));

    public static void register(RegisterEvent event) {
        event.register(Keys.SOUND_EVENTS, x -> {
            x.register(new ResourceLocation(CuffedMod.MODID, "restraint.apply_handcuffs"), HANDCUFFED);
            x.register(new ResourceLocation(CuffedMod.MODID, "restraint.apply_shackles"), SHACKLES_EQUIP);
            x.register(new ResourceLocation(CuffedMod.MODID, "block.pillory.use"), PILLORY_USE);
            x.register(new ResourceLocation(CuffedMod.MODID, "block.guillotine.use"), GUILLOTINE_USE);
            
            x.register(new ResourceLocation(CuffedMod.MODID, "block.safe.open"), SAFE_OPEN);
            x.register(new ResourceLocation(CuffedMod.MODID, "block.safe.close"), SAFE_CLOSE);
        });
    }
}
