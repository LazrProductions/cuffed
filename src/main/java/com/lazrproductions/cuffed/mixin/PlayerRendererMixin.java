package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.restraints.client.layer.PilloryEntityLayer;
import com.lazrproductions.cuffed.restraints.client.layer.RestraintEntityLayer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

   @Inject(at = @At("TAIL"), method = "<init>")
   public void constructor(EntityRendererProvider.Context context, boolean slim, CallbackInfo callback) {
      PlayerRenderer m = (PlayerRenderer) (Object) this;

      m.addLayer(new RestraintEntityLayer<>(m, context));

      // Restraints can have custom layer renderers, they can be added in the mod's own mixin of this class.
      m.addLayer(new PilloryEntityLayer<>(m, context.getItemInHandRenderer()));
   }
}
