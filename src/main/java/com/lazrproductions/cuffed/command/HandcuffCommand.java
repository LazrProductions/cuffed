package com.lazrproductions.cuffed.command;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.CuffedCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class HandcuffCommand {
    public HandcuffCommand(CommandDispatcher<CommandSourceStack> dispatcher ) {
        dispatcher.register(Commands.literal("handcuffs").then(Commands.argument("player", EntityArgument.player()).executes(this::executeHandCuff)));
        dispatcher.register(
            Commands.literal("handcuffs")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.literal("get").executes(this::executeGet))
                    .then(Commands.literal("remove").executes(this::executeRemove))
                    .then(Commands.literal("anchor").then(Commands.argument("entity", EntityArgument.entity()).executes(this::executeAnchor)))
                    .then(Commands.literal("toggle")
                        .then(Commands.literal("handcuffed").executes(this::executeToggleHandcuff))
                        .then(Commands.literal("softcuffed").executes(this::executeToggleSoftcuff))
                    )
                    .then(Commands.literal("nickname")
                        .then(Commands.literal("set").then(Commands.argument("new nickname", ComponentArgument.textComponent()).executes(this::executeSetNickname)))
                        .then(Commands.literal("reset").executes(this::executeResetNickname))
                    )
                    //.then(Commands.literal("debug") //TODO: enable for debugging
                    //    .then(Commands.literal("manualsync").executes(this::executeDebugManualSync)))
                )
            );
    }

    private int executeGet(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if(player!=null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                sender.sendSystemMessage(c.isHandcuffed() ? Component.translatable("command.cuffed.get.on", player.getName()) : Component.translatable("command.cuffed.get.off", player.getName()));

                // CuffedMod.LOGGER.info("[COMMAND DEBUG] -> Handcuffed Information [SERVER]:"
                //    + "\n - isHandcuffed: " + c.isHandcuffed() 
                //    + "\n - isSoftCuffed: " + c.isSoftCuffed() 
                //    + "\n - isDetained: " + c.isDetained()
                //    + "\n -  detainedRot: " + c.getDetainedRotation()
                //    + "\n -  detainedPos: " + c.getDetainedPosition()
                //    + "\n - isAnchored: " + c.isAnchored()
                //    + "\n -  anchor: " + (c.getAnchor() != null ? c.getAnchor().getDisplayName().getString() : "null")
                //    + "\n - nbt: " + c.serializeNBT().getAsString());

                //CuffedAPI.sendCuffedDebugPacketToClient(sender, player.getUUID());
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeHandCuff(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                if(!c.isHandcuffed()) {
                    c.server_applyHandcuffs(player);
                    sender.sendSystemMessage(Component.translatable("command.cuffed.handcuff.on", player.getName()));
                } else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.alreadycuffed", player.getName()));
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeRemove(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                if(c.isHandcuffed()) {
                    CuffedAPI.Handcuffing.removeHandcuffs(player);
                    sender.sendSystemMessage(Component.translatable("command.cuffed.handcuff.off", player.getName()));
                } else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.notcuffed", player.getName()));
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeAnchor(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            Entity anchor = EntityArgument.getEntity(ctx, "entity");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                if(c.isHandcuffed()) {
                    c.server_setAnchor(anchor);
                    sender.sendSystemMessage(Component.translatable("command.cuffed.anchor.set", player.getName(), anchor.getName()));
                } else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.notcuffed", player.getName()));
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeToggleHandcuff(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                if(!c.isHandcuffed()) {
                    c.server_applyHandcuffs(player);
                    sender.sendSystemMessage(Component.translatable("command.cuffed.handcuff.on", player.getName()));
                } else {
                    CuffedAPI.Handcuffing.removeHandcuffs(player);
                    sender.sendSystemMessage(Component.translatable("command.cuffed.handcuff.off", player.getName()));
                }
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeToggleSoftcuff(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                if(c.isHandcuffed()) {
                    if(!c.isSoftCuffed()) {
                        c.server_setSoftCuffed(true);
                        sender.sendSystemMessage(Component.translatable("command.cuffed.toggle.softcuffed.on", player.getName()));
                    } else {
                        c.server_setSoftCuffed(false);
                        sender.sendSystemMessage(Component.translatable("command.cuffed.toggle.softcuffed.off", player.getName()));
                    }
                } else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.notcuffed", player.getName()));
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeSetNickname(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            Component n = ComponentArgument.getComponent(ctx, "new nickname");
            if (player != null && sender != null && n != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                sender.sendSystemMessage(Component.translatable("command.cuffed.nickname.set", player.getName(), n));
                c.server_setNickname(n);
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeResetNickname(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                c.server_setNickname(null);
                sender.sendSystemMessage(Component.translatable("command.cuffed.nickname.reset", player.getName()));
                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    private int executeDebugManualSync(CommandContext<CommandSourceStack> ctx) {
        try { 
            ServerPlayer player = EntityArgument.getPlayer(ctx,"player");
            if (player != null) {
                CuffedCapability c = CuffedAPI.Capabilities.getCuffedCapability(player);
                CuffedAPI.sendCuffedSyncPacketToClient(player.getId(), player.getUUID(), c.serializeNBT());
                player.sendSystemMessage(Component.translatable("command.cuffed.debug", player.getName()));

                return 1;
            }
            return 0;
        } catch(CommandSyntaxException e) {
            return 0;
        }
    }
}
