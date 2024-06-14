package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.init.ModModelLayers;
import com.lazrproductions.cuffed.restraints.layer.FuzzyHandcuffEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.HandcuffEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.LegShacklesEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.LegcuffsEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.ShacklesEntityLayer;
import com.lazrproductions.cuffed.restraints.model.FuzzyHandcuffsModel;
import com.lazrproductions.cuffed.restraints.model.HandcuffsModel;
import com.lazrproductions.cuffed.restraints.model.LegShacklesModel;
import com.lazrproductions.cuffed.restraints.model.LegcuffsModel;
import com.lazrproductions.cuffed.restraints.model.ShacklesModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

   @Inject(at = @At("TAIL"), method = "<init>")
   public void constructor(EntityRendererProvider.Context context, boolean slim, CallbackInfo callback) {
      PlayerRenderer m = (PlayerRenderer) (Object) this;
      m.addLayer(new HandcuffEntityLayer<>(m, new HandcuffsModel<>(context.bakeLayer(ModModelLayers.HANDCUFFS_LAYER))));
      m.addLayer(new ShacklesEntityLayer<>(m, new ShacklesModel<>(context.bakeLayer(ModModelLayers.SHACKLES_LAYER))));
      m.addLayer(new FuzzyHandcuffEntityLayer<>(m, new FuzzyHandcuffsModel<>(context.bakeLayer(ModModelLayers.FUZZY_HANDCUFFS_LAYER))));
      m.addLayer(new LegcuffsEntityLayer<>(m, new LegcuffsModel<>(context.bakeLayer(ModModelLayers.LEGCUFFS_LAYER))));
      m.addLayer(new LegShacklesEntityLayer<>(m, new LegShacklesModel<>(context.bakeLayer(ModModelLayers.LEG_SHACKELS_LAYER))));
   }
}
