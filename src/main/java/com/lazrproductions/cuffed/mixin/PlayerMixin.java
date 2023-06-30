package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.server.CuffedServer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }
    
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/entity/player/Player;canBeSeenAsEnemy()Z", cancellable = true)
    public void isHandcuffed(CallbackInfoReturnable<Boolean> cir) {
        IHandcuffed handcuffed = CuffedServer.getHandcuffed((Player) (Object) this);
        if (handcuffed.isHandcuffed())
            cir.setReturnValue(false);
    }
}