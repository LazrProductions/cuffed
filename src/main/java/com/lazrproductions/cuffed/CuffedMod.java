package com.lazrproductions.cuffed;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import com.lazrproductions.cuffed.blocks.base.PosterType;
import com.lazrproductions.cuffed.blocks.entity.renderer.GuillotineBlockEntityRenderer;
import com.lazrproductions.cuffed.blocks.entity.renderer.TrayBlockEntityRenderer;
import com.lazrproductions.cuffed.client.gui.screen.FriskingScreen;
import com.lazrproductions.cuffed.command.CuffedDebugCommand;
import com.lazrproductions.cuffed.command.HandcuffCommand;
import com.lazrproductions.cuffed.compat.ArsNouveauCompat;
import com.lazrproductions.cuffed.compat.BetterCombatCompat;
import com.lazrproductions.cuffed.compat.ElenaiDodge2Compat;
import com.lazrproductions.cuffed.compat.EpicFightCompat;
import com.lazrproductions.cuffed.compat.IronsSpellsnSpellbooksCompat;
import com.lazrproductions.cuffed.compat.ManaAndArtificeCompat;
import com.lazrproductions.cuffed.compat.ParcoolCompat;
import com.lazrproductions.cuffed.compat.PlayerReviveCompat;
import com.lazrproductions.cuffed.compat.SimpleVoiceChatCompat;
import com.lazrproductions.cuffed.compat.TacZCompat;
import com.lazrproductions.cuffed.config.CuffedServerConfig;
import com.lazrproductions.cuffed.entity.renderer.ChainKnotEntityRenderer;
import com.lazrproductions.cuffed.entity.renderer.CrumblingBlockRenderer;
import com.lazrproductions.cuffed.entity.renderer.PadlockEntityRenderer;
import com.lazrproductions.cuffed.entity.renderer.WeightedAnchorEntityRenderer;
import com.lazrproductions.cuffed.event.ModClientEvents;
import com.lazrproductions.cuffed.event.ModServerEvents;
import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModCreativeTabs;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModMenuTypes;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.lazrproductions.cuffed.init.ModParticleTypes;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.inventory.tooltip.PossessionsBoxTooltip;
import com.lazrproductions.cuffed.inventory.tooltip.TrayTooltip;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.items.PossessionsBox;
import com.lazrproductions.cuffed.items.TrayItem;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(CuffedMod.MODID)
public class CuffedMod {
    public static final Logger LOGGER = LogManager.getLogger(CuffedMod.MODID);
    public static final String MODID = "cuffed";

    public static final CuffedServerConfig SERVER_CONFIG = new CuffedServerConfig(MODID, ModConfig.Type.SERVER);

    public static boolean BetterCombatInstalled = false;
    public static boolean EpicFightInstalled = false;
    public static boolean ParcoolInstalled = false;
    public static boolean ElenaiDodge2Installed = false;
    public static boolean IronsSpellsnSpellbooksInstalled = false;
    public static boolean ManaAndArtificeInstalled = false;
    public static boolean KnightsOfBritanniaInstalled = false;
    public static boolean ArsNouveauInstalled = false;
    public static boolean PlayerReviveInstalled = false;
    public static boolean VoiceChatInstalled = false;

    public CuffedMod(IEventBus modEventBus, net.neoforged.fml.ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloadHandlers);

        CuffedAPI.Capabilities.ATTACHMENT_TYPES.register(modEventBus);

        ModEntityTypes.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModParticleTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModStatistics.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRestraints.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerSounds);
        modEventBus.addListener(ModEntityTypes::registerAttributes);

        if (ModList.get().isLoaded("bettercombat")) {
            BetterCombatInstalled = true;
            BetterCombatCompat.load();
        }
        if (ModList.get().isLoaded("epicfight")) {
            EpicFightInstalled = true;
            EpicFightCompat.load();
        }
        if (ModList.get().isLoaded("parcool")) {
            ParcoolInstalled = true;
            ParcoolCompat.load();
        }
        if (ModList.get().isLoaded("elenaidodge2")) {
            ElenaiDodge2Installed = true;
            ElenaiDodge2Compat.load();
        }
        if (ModList.get().isLoaded("irons_spellbooks")) {
            IronsSpellsnSpellbooksInstalled = true;
            IronsSpellsnSpellbooksCompat.load();
        }
        if (ModList.get().isLoaded("ars_nouveau")) {
            ArsNouveauInstalled = true;
            ArsNouveauCompat.load();
        }
        if (ModList.get().isLoaded("mna")) {
            ManaAndArtificeInstalled = true;
            ManaAndArtificeCompat.load();
        }
        if (ModList.get().isLoaded("playerrevive")) {
            PlayerReviveInstalled = true;
            PlayerReviveCompat.load();
        }
        if (ModList.get().isLoaded("voicechat")) {
            VoiceChatInstalled = true;
            SimpleVoiceChatCompat.load();
        }
        if (ModList.get().isLoaded("tacz")) {
            VoiceChatInstalled = true;
            TacZCompat.load(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Running commmon setup for Cuffed");
        ModStatistics.setup();
        NeoForge.EVENT_BUS.register(new ModServerEvents());
        
        // Dispenser mekanikleri 1.21'de değiştiği için ana iskelet kurulana kadar geçici olarak devre dışı bırakıldı.
    }

    private void registerSounds(RegisterEvent event) {
        // Eski IForgeRegistry kontrolleri kaldırıldı, ses kayıtları ModSounds içinde halledilecek.
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Running server setup for Cuffed");
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        new HandcuffCommand(event.getDispatcher(), event.getBuildContext());
        new CuffedDebugCommand(event.getDispatcher(), event.getBuildContext());
    }
    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        if (net.neoforged.fml.loading.FMLEnvironment.dist == Dist.CLIENT) {
            com.lazrproductions.cuffed.client.ClientPayloadRegister.register(registrar);
        } else {
            ServerPayloadRegister.register(registrar);
        }
    }

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Running client setup for Cuffed");

            ItemProperties.register(ModItems.KEY_RING.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "keys"), (stack, level, living, id) -> {
                        var tag = ItemTagUtils.getTag(stack);
                        if (tag != null && tag.contains(KeyRingItem.TAG_KEYS))
                            return tag.getInt(KeyRingItem.TAG_KEYS);
                        return 0;
                    });
            ItemProperties.register(ModItems.POSSESSIONSBOX.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "filled"), (stack, level, living, id) -> {
                        CompoundTag compoundtag = ItemTagUtils.getOrCreateTag(stack);
                        if (!compoundtag.contains(PossessionsBox.TAG_ITEMS)) {
                            return 0;
                        } else {
                            ListTag listtag = compoundtag.getList(PossessionsBox.TAG_ITEMS, 10);
                            return listtag.size() > 0 ? 1 : 0;
                        }
                    });
            ItemProperties.register(ModItems.TRAY.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "filled"), (stack, level, living, id) -> {
                        return TrayItem.trayHasFoodItem(stack) || TrayItem.trayHasSpoon(stack)
                                || TrayItem.trayHasFork(stack) || TrayItem.trayHasKnife(stack) ? 1 : 0;
                    });
            ItemProperties.register(ModItems.POSTER_ITEM.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "poster"), (stack, level, living, id) -> {
                        return PosterType.getfromItem(stack).toInt();
                    });

            NeoForge.EVENT_BUS.register(new ModClientEvents());
        }

        @SubscribeEvent
        public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.FRISKING_MENU.get(), FriskingScreen::new);
        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(PossessionsBoxTooltip.class, Function.identity());
            event.register(TrayTooltip.class, Function.identity());
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            ModModelLayers.registerLayers(event);
        }

        @SubscribeEvent
        public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
            ModParticleTypes.registerSprites(event);
        }
        
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntityTypes.CHAIN_KNOT.get(), ChainKnotEntityRenderer::new);
            event.registerEntityRenderer(ModEntityTypes.PADLOCK.get(), PadlockEntityRenderer::new);
            event.registerEntityRenderer(ModEntityTypes.WEIGHTED_ANCHOR.get(), WeightedAnchorEntityRenderer::new);
            event.registerEntityRenderer(ModEntityTypes.CRUMBLING_BLOCK.get(), CrumblingBlockRenderer::new);

            event.registerBlockEntityRenderer(ModBlockEntities.GUILLOTINE.get(), GuillotineBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.TRAY.get(), TrayBlockEntityRenderer::new);
        }
    }
}