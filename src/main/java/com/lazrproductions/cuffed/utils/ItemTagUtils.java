package com.lazrproductions.cuffed.utils;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemTagUtils {
    public static CompoundTag getTag(ItemStack stack) {
        if (stack.isEmpty()) return null;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : null;
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        if (stack.isEmpty()) return new CompoundTag();
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        if (stack.isEmpty()) return;
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
        if (stack.isEmpty()) return;
        CompoundTag tag = getOrCreateTag(stack);
        updater.accept(tag);
        setTag(stack, tag);
    }

    public static HolderLookup.Provider getProvider() {
        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.DEDICATED_SERVER) {
            var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            return server != null ? server.registryAccess() : null;
        } else {
            try {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mcInstance = mcClass.getMethod("getInstance").invoke(null);
                Object level = mcClass.getField("level").get(mcInstance);
                if (level != null) {
                    return (HolderLookup.Provider) level.getClass().getMethod("registryAccess").invoke(level);
                }
            } catch (Exception e) {
                // Ignore
            }
            return null;
        }
    }

    public static ItemStack loadItem(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag == null || tag.isEmpty()) return ItemStack.EMPTY;
        if (provider == null) provider = getProvider();
        if (provider == null) return ItemStack.EMPTY;
        return ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY);
    }

    public static CompoundTag saveItem(ItemStack stack, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (stack.isEmpty()) return tag;
        if (provider == null) provider = getProvider();
        if (provider == null) return tag;
        return (CompoundTag) stack.save(provider, tag);
    }
}
