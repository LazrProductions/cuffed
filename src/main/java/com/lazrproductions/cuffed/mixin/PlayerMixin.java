package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    public void getName(CallbackInfoReturnable<Component> callback) {
        Component n = getNickname();
        if (n != null)
            callback.setReturnValue(n);
    }

    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> callback) {
        Component n = getNickname();
        if (n != null)
            callback.setReturnValue(n);
    }



    public Component getNickname() {
        Player t = (Player) (Object) this;
        CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(t);
        if (c.getNickname() != null) {
            if (c.isGettingOrCurrentlyHandcuffed() || CuffedMod.CONFIG.handcuffSettings.persistantNickname)
                return c.getNickname();
        }

        return null;
    }
}
