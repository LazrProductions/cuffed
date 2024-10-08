package com.lazrproductions.cuffed.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lazrproductions.cuffed.init.ModModelLayers;
import com.lazrproductions.cuffed.restraints.layer.BundleEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.DuckTapeEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.FuzzyHandcuffEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.HandcuffEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.LegShacklesEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.LegcuffsEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.PilloryEntityLayer;
import com.lazrproductions.cuffed.restraints.layer.ShacklesEntityLayer;
import com.lazrproductions.cuffed.restraints.model.BundleModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeArmsModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeHeadModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeLegsModel;
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
      m.addLayer(new PilloryEntityLayer<>(m, context.getItemInHandRenderer()));
      m.addLayer(new DuckTapeEntityLayer<>(m, 
         new DuckTapeHeadModel<>(context.bakeLayer(ModModelLayers.DUCK_TAPE_HEAD_LAYER)), 
         new DuckTapeArmsModel<>(context.bakeLayer(ModModelLayers.DUCK_TAPE_ARM_LAYER)), 
         new DuckTapeLegsModel<>(context.bakeLayer(ModModelLayers.DUCK_TAPE_LEG_LAYER))));
      m.addLayer(new BundleEntityLayer<>(m, new BundleModel<>(context.bakeLayer(ModModelLayers.BUNDLE_LAYER))));
   }
}
