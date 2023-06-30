package com.lazrproductions.cuffed;

import java.util.Collection;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lazrproductions.cuffed.api.IHandcuffed;
import com.lazrproductions.cuffed.client.CuffedEventClient;
import com.lazrproductions.cuffed.client.render.entity.ChainKnotEntityRenderer;
import com.lazrproductions.cuffed.client.render.entity.model.ChainKnotEntityModel;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.inventory.tooltip.PossessionsBoxTooltip;
import com.lazrproductions.cuffed.packet.CuffedUpdatePacket;
import com.lazrproductions.cuffed.packet.HandcuffingPacket;
import com.lazrproductions.cuffed.packet.UpdateChainedPacket;
import com.lazrproductions.cuffed.recipes.CellKeyRingAddRecipe;
import com.lazrproductions.cuffed.server.CuffedEventServer;
import com.lazrproductions.cuffed.server.CuffedServer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import team.creative.creativecore.common.network.CreativeNetwork;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CuffedMod.MODID)
public class CuffedMod {
    public static final Logger LOGGER = LogManager.getLogger(CuffedMod.MODID);
    public static final String MODID = "cuffed";

    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER,
            new ResourceLocation(CuffedMod.MODID, "main"));

    public static ResourceLocation res(String n) {
        return new ResourceLocation(MODID, n);
    }

    // #region Registers

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, CuffedMod.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MODID);
    // #endregion

    // #region Recipes
    public static final RegistryObject<RecipeSerializer<CellKeyRingAddRecipe>> CELL_KEY_RING_ADD = RECIPE_SERIALIZERS
            .register("cell_key_ring_add", () -> new SimpleCraftingRecipeSerializer<>(CellKeyRingAddRecipe::new));
    // #endregion

    // #region Creative Mode Tabs
    public static final RegistryObject<CreativeModeTab> CUFFED_TAB = CREATIVE_MODE_TABS.register("cuffed_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup.cuffed"))
                    .icon(() -> ModItems.HANDCUFFS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CELL_DOOR_ITEM.get());
                        output.accept(ModItems.CELL_KEY.get());
                        output.accept(ModItems.CELL_KEY_RING.get());
                        output.accept(ModItems.HANDCUFFS_KEY.get());
                        output.accept(ModItems.HANDCUFFS.get());
                        output.accept(ModItems.POSSESSIONSBOX.get());
                    }).build());
    // #endregion

    // #region Capabilities
    public static final ResourceLocation HANDCUFFED_NAME = new ResourceLocation(MODID, "handcuffed");
    public static final Capability<IHandcuffed> HANDCUFFED = CapabilityManager.get(new CapabilityToken<>() {
    });
    // #endregion

    // #region Resources
    public static ResourceLocation[] BREAKCUFFS_GUI;
    // #endregion

    // #region Sounds
    public static final SoundEvent HANDCUFFED_SOUND = SoundEvent
            .createVariableRangeEvent(new ResourceLocation(MODID, "apply_handcuffs"));
    // #endregion

    // #region Attirbutes
    public static final AttributeModifier HANDCUFFED_ATTIRBUTE = new AttributeModifier("handcuffed", -1,
            AttributeModifier.Operation.MULTIPLY_BASE);
    // #endregion

    public CuffedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModEntityTypes.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        RECIPE_SERIALIZERS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCaps);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSounds);

        // initialize the resource array for the breaking cuffs GUI
        BREAKCUFFS_GUI = new ResourceLocation[21];
        for (int i = 0; i < 21; i++) {
            BREAKCUFFS_GUI[i] = new ResourceLocation("cuffed",
                    "textures/gui/interuptbar/interuptbar" + (i + 1) + ".png");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Running commmon setup for Cuffed");

        NETWORK.registerType(CuffedUpdatePacket.class, CuffedUpdatePacket::new);
        NETWORK.registerType(HandcuffingPacket.class, HandcuffingPacket::new);
        NETWORK.registerType(UpdateChainedPacket.class, UpdateChainedPacket::new);

        MinecraftForge.EVENT_BUS.register(new CuffedEventServer());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(ModItems.CELL_DOOR_ITEM);
    }

    private void registerCaps(RegisterCapabilitiesEvent event) {
        LOGGER.info("Registering Capabilities for Cuffed");
        event.register(IHandcuffed.class);
    }

    private void registerSounds(RegisterEvent event) {
        event.register(Keys.SOUND_EVENTS, x -> {
            x.register(new ResourceLocation(MODID, "apply_handcuffs"), HANDCUFFED_SOUND);
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Running server setup for Cuffed");

        // Register Commands
        event.getServer().getCommands().getDispatcher()
                .register(Commands.literal("handcuff").requires(x -> x.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.players())
                                        .then(Commands.literal("handcuffed").executes(x -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(x,
                                                    "player");
                                            for (ServerPlayer player : players)
                                                if (!CuffedServer.getHandcuffed(player).isHandcuffed()) {
                                                    player.sendSystemMessage(Component
                                                            .literal("Put " + player.getName().getString()
                                                                    + " in handcuffs."));
                                                    CuffedServer.applyHandcuffs(player);
                                                } else {
                                                    player.sendSystemMessage(
                                                            Component.literal(
                                                                    "" + player.getName().getString()
                                                                            + " is in handcuffs."));
                                                }
                                            return 0;
                                        }))
                                        .then(Commands.literal("softcuffed")).executes(x -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(x, "player");
                                            for (ServerPlayer player : players)
                                                if (!CuffedServer.getHandcuffed(player).isHandcuffed()) {
                                                    player.sendSystemMessage(Component
                                                            .literal("Put " + player.getName().getString()
                                                                    + " in handcuffs."));
                                                    CuffedServer.applyHandcuffs(player);
                                                } else {
                                                    player.sendSystemMessage(
                                                            Component.literal(
                                                                    "" + player.getName().getString()
                                                                            + " is in handcuffs."));
                                                }
                                            return 0;
                                        })
                                        .then(Commands.literal("anchor").executes(x -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(x, "player");
                                            for (ServerPlayer player : players) {
                                                player.sendSystemMessage(Component.literal(
                                                        "Removed " + player.getName().getString() + "'s anchor."));
                                                CuffedServer.getHandcuffed(player).setAnchor(null);
                                            }
                                            return 0;
                                        })
                                                .then(Commands.argument("entity", EntityArgument.entity())
                                                        .executes(x -> {
                                                            Collection<ServerPlayer> players = EntityArgument
                                                                    .getPlayers(x, "player");
                                                            Entity entity = EntityArgument.getEntity(x, "entity");
                                                            for (ServerPlayer player : players)
                                                                // if (entity != player) {
                                                                if (CuffedServer.getHandcuffed(player).isHandcuffed()) {
                                                                    player.sendSystemMessage(Component
                                                                            .literal("Set "
                                                                                    + entity.getName().getString()
                                                                                    + " as "
                                                                                    + player.getName().getString()
                                                                                    + "'s anchor."));
                                                                    CuffedServer.getHandcuffed(player)
                                                                            .setAnchor(entity);
                                                                } else {
                                                                    player.sendSystemMessage(
                                                                            Component.literal(
                                                                                    "" + player.getName().getString()
                                                                                            + " is not handcuffed."));
                                                                }
                                                            // } else
                                                            // player.sendSystemMessage(
                                                            // Component.literal("You cannot anchor
                                                            // "+player.getName().getString()+" to themself"));
                                                            return 0;
                                                        }))))));
        event.getServer().getCommands().getDispatcher()
                .register(
                        Commands.literal("handcuff").requires(x -> x.hasPermission(2))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("players", EntityArgument.players()).executes(x -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(x, "players");
                                            for (ServerPlayer player : players)
                                                if (CuffedServer.getHandcuffed(player).isGettingOrCurrentlyCuffed()) {
                                                    player.sendSystemMessage(Component.literal(
                                                            "Removed " + player.getName().getString()
                                                                    + "'s handcuffs."));
                                                    CuffedServer.removeHandcuffs(player);
                                                } else {
                                                    player.sendSystemMessage(Component.literal(
                                                            "" + player.getName().getString()
                                                                    + " is not in handcuffs."));
                                                }
                                            return 0;
                                        }))));
        event.getServer().getCommands().getDispatcher()
                .register(
                        Commands.literal("handcuff").requires(x -> x.hasPermission(2))
                                .then(Commands.literal("get")
                                        .then(Commands.argument("players", EntityArgument.players()).executes(x -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(x, "players");
                                            for (ServerPlayer player : players)
                                                if (CuffedServer.getHandcuffed(player).isGettingOrCurrentlyCuffed()) {
                                                    if (!CuffedServer.getHandcuffed(player).isSoftCuffed()) {
                                                        player.sendSystemMessage(Component.literal(
                                                                "" + player.getName().getString()
                                                                        + " is handcuffed."));
                                                    } else {
                                                        player.sendSystemMessage(Component.literal(
                                                                "" + player.getName().getString()
                                                                        + " is soft-cuffed."));
                                                    }
                                                } else {
                                                    player.sendSystemMessage(Component.literal(
                                                            "" + player.getName().getString()
                                                                    + " is not in handcuffs."));
                                                }
                                            return 0;
                                        }))));

        // TODO: [DEBUG] Remove with build.
        event.getServer().getCommands().getDispatcher()
                .register(
                        Commands.literal("handcuff").requires(x -> x.hasPermission(2))
                                .then(Commands.literal("debug")
                                        .then(Commands.literal("getAttributeUUID")
                                                .executes(x -> {
                                                    x.getSource().source.sendSystemMessage(
                                                            Component.literal("The UUID of HANDCUFFS_ATTRIBUTE is ")
                                                                    .append(ComponentUtils.copyOnClickText(
                                                                            "" + HANDCUFFED_ATTIRBUTE.getId())));
                                                    return 1;
                                                }))));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        protected static ChainKnotEntityRenderer chainKnotEntityRenderer;

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Running client setup for Cuffed");

            ItemProperties.register(ModItems.CELL_KEY_RING.get(),
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
            MinecraftForge.EVENT_BUS.register(new CuffedEventClient());

            EntityRenderers.register(ModEntityTypes.CHAIN_KNOT.get(), ChainKnotEntityRenderer::new);

        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(PossessionsBoxTooltip.class, Function.identity());
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ChainKnotEntityModel.LAYER_LOCATION, ChainKnotEntityModel::getModelData);
        }
    }
}