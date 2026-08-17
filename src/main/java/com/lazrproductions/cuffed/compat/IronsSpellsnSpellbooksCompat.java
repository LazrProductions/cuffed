package com.lazrproductions.cuffed.compat;

import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.core.Holder;
import java.lang.reflect.Method;

public class IronsSpellsnSpellbooksCompat {
    public static void load() {
    }

    public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
        try {
            Class<?> magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
            Method getPlayerMagicDataMethod = magicDataClass.getMethod("getPlayerMagicData", net.minecraft.world.entity.player.Player.class);
            Object data = getPlayerMagicDataMethod.invoke(null, player);
            if (data != null) {
                Method addManaMethod = data.getClass().getMethod("addMana", float.class);
                addManaMethod.invoke(data, (float) -amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
        try {
            Class<?> magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
            Method getPlayerMagicDataMethod = magicDataClass.getMethod("getPlayerMagicData", net.minecraft.world.entity.player.Player.class);
            Object data = getPlayerMagicDataMethod.invoke(null, player);
            if (data != null) {
                Class<?> attributeRegistryClass = Class.forName("io.redspace.ironsspellbooks.api.registry.AttributeRegistry");
                Object maxManaRegistryObject = attributeRegistryClass.getField("MAX_MANA").get(null);
                Method getMethod = maxManaRegistryObject.getClass().getMethod("get");
                Object attributeObj = getMethod.invoke(maxManaRegistryObject);
                
                float maxMana = 0.0f;
                if (attributeObj instanceof Attribute attr) {
                    maxMana = (float) player.getAttributeValue(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr));
                } else if (attributeObj instanceof Holder) {
                    maxMana = (float) player.getAttributeValue((Holder<Attribute>) attributeObj);
                }
                
                Method addManaMethod = data.getClass().getMethod("addMana", float.class);
                addManaMethod.invoke(data, -maxMana * (float) amountPercentage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
