package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.entity.model.GuillotineBlockEntityModel;
import com.lazrproductions.cuffed.entity.model.ChainKnotEntityModel;
import com.lazrproductions.cuffed.entity.model.CrumblingBlockModel;
import com.lazrproductions.cuffed.entity.model.PadlockEntityModel;
import com.lazrproductions.cuffed.entity.model.WeightedAnchorModel;
import com.lazrproductions.cuffed.restraints.model.BundleModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeArmsModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeHeadModel;
import com.lazrproductions.cuffed.restraints.model.DuckTapeLegsModel;
import com.lazrproductions.cuffed.restraints.model.FuzzyHandcuffsModel;
import com.lazrproductions.cuffed.restraints.model.HandcuffsModel;
import com.lazrproductions.cuffed.restraints.model.LegShacklesModel;
import com.lazrproductions.cuffed.restraints.model.LegcuffsModel;
import com.lazrproductions.cuffed.restraints.model.ShacklesModel;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CuffedMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModModelLayers {
    // Restraints
    public static final ModelLayerLocation HANDCUFFS_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "handcuffs_layer"), "main");
    public static final ModelLayerLocation SHACKLES_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "shackles_layer"), "main");
    public static final ModelLayerLocation FUZZY_HANDCUFFS_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "fuzzy_handcuffs_layer"), "main");
    public static final ModelLayerLocation LEGCUFFS_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "legcuffs_layer"), "main");
    public static final ModelLayerLocation LEG_SHACKELS_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "leg_shackles_layer"), "main");
    public static final ModelLayerLocation DUCK_TAPE_HEAD_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "duck_tape_head_layer"), "main");
    public static final ModelLayerLocation DUCK_TAPE_ARM_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "duck_tape_arm_layer"), "main");
    public static final ModelLayerLocation DUCK_TAPE_LEG_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "duck_tape_leg_layer"), "main");
    public static final ModelLayerLocation BUNDLE_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "bundle_layer"), "main");

    // Entities
	public static final ModelLayerLocation CHAIN_KNOT_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "chain_knot"), "main");
    public static final ModelLayerLocation PADLOCK_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "padlock"), "main");
    public static final ModelLayerLocation WEIGHTED_ANCHOR_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "weighted_anchor"), "main");
    public static final ModelLayerLocation CRUMBLING_BLOCK_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "crumbling_block"), "main");

    // Block Entities
	public static final ModelLayerLocation GUILLOTINE_LAYER = new ModelLayerLocation(new ResourceLocation(CuffedMod.MODID, "guillotine_block_entity"), "main");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CHAIN_KNOT_LAYER, ChainKnotEntityModel::getModelData);
        event.registerLayerDefinition(PADLOCK_LAYER, PadlockEntityModel::getModelData);
        event.registerLayerDefinition(WEIGHTED_ANCHOR_LAYER, WeightedAnchorModel::getModelData);
        event.registerLayerDefinition(CRUMBLING_BLOCK_LAYER, CrumblingBlockModel::getModelData);

        event.registerLayerDefinition(HANDCUFFS_LAYER, HandcuffsModel::createArmorLayer);
        event.registerLayerDefinition(SHACKLES_LAYER, ShacklesModel::createArmorLayer);
        event.registerLayerDefinition(FUZZY_HANDCUFFS_LAYER, FuzzyHandcuffsModel::createArmorLayer);
        event.registerLayerDefinition(LEGCUFFS_LAYER, LegcuffsModel::createArmorLayer);
        event.registerLayerDefinition(LEG_SHACKELS_LAYER, LegShacklesModel::createArmorLayer);
        event.registerLayerDefinition(DUCK_TAPE_HEAD_LAYER, DuckTapeHeadModel::createArmorLayer);
        event.registerLayerDefinition(DUCK_TAPE_ARM_LAYER, DuckTapeArmsModel::createArmorLayer);
        event.registerLayerDefinition(DUCK_TAPE_LEG_LAYER, DuckTapeLegsModel::createArmorLayer);
        event.registerLayerDefinition(BUNDLE_LAYER, BundleModel::createArmorLayer);

        event.registerLayerDefinition(GUILLOTINE_LAYER, GuillotineBlockEntityModel::createBodyLayer);
    }
}
