package com.lazrproductions.cuffed;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.api.ICuffedCapability;
import com.lazrproductions.cuffed.client.render.entity.ChainKnotEntityRenderer;
import com.lazrproductions.cuffed.client.render.entity.PadlockEntityRenderer;
import com.lazrproductions.cuffed.client.render.entity.model.ChainKnotEntityModel;
import com.lazrproductions.cuffed.client.render.entity.model.PadlockEntityModel;
import com.lazrproductions.cuffed.command.HandcuffCommand;
import com.lazrproductions.cuffed.config.CuffedCommonConfig;
import com.lazrproductions.cuffed.event.ModClientEvents;
import com.lazrproductions.cuffed.event.ModServerEvents;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModCreativeTabs;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.inventory.tooltip.PossessionsBoxTooltip;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.server.command.ConfigCommand;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;

// The value e should match an entry in the META-INF/mods.toml file
@Mod(CuffedMod.MODID)
public class CuffedMod {
    public static final Logger LOGGER = LogManager.getLogger(CuffedMod.MODID);
    public static final String MODID = "cuffed";

    // #region Resources
    public static ResourceLocation[] BREAKCUFFS_GUI;
    public static ResourceLocation[] PICKLOCK_GUI;
    public static ResourceLocation CHAINED_OVERLAY_TEXTURE = new ResourceLocation(MODID,
            "textures/entity/chained_overlay.png");
    // #endregion

    // #region Configs
    public static CuffedCommonConfig CONFIG;
    // #endregion

    public CuffedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModEntityTypes.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModStatistics.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCaps);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSounds);

        // initialize the resource array for the breaking cuffs GUI
        BREAKCUFFS_GUI = new ResourceLocation[21];
        for (int i = 0; i < 21; i++) {
            BREAKCUFFS_GUI[i] = new ResourceLocation("cuffed",
                    "textures/gui/interuptbar/interuptbar" + (i + 1) + ".png");
        }
        // initialize the resource array for the lockpicking progress GUI
        PICKLOCK_GUI = new ResourceLocation[31];
        for (int i = 0; i < 31; i++) {
            PICKLOCK_GUI[i] = new ResourceLocation("cuffed",
                    "textures/gui/interuptbar/pickprogressbar" + (i + 1) + ".png");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Running commmon setup for Cuffed");

        CuffedAPI.registerPackets();

        ModStatistics.setup();

        MinecraftForge.EVENT_BUS.register(new ModServerEvents());

        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new CuffedCommonConfig());
    }

    private void registerSounds(RegisterEvent event) {
        if(event.getRegistryKey().equals(Keys.SOUND_EVENTS)) {
            LOGGER.info("Registering sound for Cuffed");
            ModSounds.register(event);
        }
    }
    private void registerCaps(RegisterCapabilitiesEvent event) {
        LOGGER.info("Registering Capabilities for Cuffed");
        event.register(ICuffedCapability.class);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Running server setup for Cuffed");

    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        new HandcuffCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Running client setup for Cuffed");

            ItemProperties.register(ModItems.KEY_RING.get(),
                    new ResourceLocation(MODID, "keys"), (stack, level, living, id) -> {
                        var tag = stack.getTag();
                        if (tag != null && tag.contains("Keys"))
                            return tag.getInt("Keys");
                        return 0;
                    });
            ItemProperties.register(ModItems.POSSESSIONSBOX.get(),
                    new ResourceLocation(MODID, "filled"), (stack, level, living, id) -> {
                        CompoundTag compoundtag = stack.getOrCreateTag();
                        if (!compoundtag.contains("Items")) {
                            return 0;
                        } else {
                            ListTag listtag = compoundtag.getList("Items", 10);
                            return listtag.size() > 0 ? 1 : 0;
                        }
                    });
            MinecraftForge.EVENT_BUS.register(new ModClientEvents());

            EntityRenderers.register(ModEntityTypes.CHAIN_KNOT.get(), ChainKnotEntityRenderer::new);
            EntityRenderers.register(ModEntityTypes.PADLOCK.get(), PadlockEntityRenderer::new);

        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(PossessionsBoxTooltip.class, Function.identity());
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ChainKnotEntityModel.LAYER_LOCATION, ChainKnotEntityModel::getModelData);
            event.registerLayerDefinition(PadlockEntityModel.LAYER_LOCATION, PadlockEntityModel::getModelData);
        }

    }
}