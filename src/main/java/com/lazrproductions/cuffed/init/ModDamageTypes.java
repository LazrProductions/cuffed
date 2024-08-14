package com.lazrproductions.cuffed.init;

import net.minecraft.world.damagesource.DamageSource;


public class ModDamageTypes {
    public static final DamageSource HANG = (new DamageSource("hang")).bypassArmor().bypassEnchantments().bypassMagic();


    //public static final ResourceKey<DamageSource> HANG = register("hang");

    // public static ResourceKey<DamageSource> register (String key){
    //     return ResourceKey.create(.DAMAGE_TYPE, new ResourceLocation(CuffedMod.MODID, key));
    // }

    // public static DamageSource GetModSource(Entity entity, ResourceKey<DamageType> type, @Nullable Entity other) {
    //    return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HANG), entity, other == null ? entity : other);
    // }

    // public static void bootstrap(BootstapContext<DamageType> context) {
    //     context.register(HANG, new DamageType("hang", 0.1F));
    // }
}
