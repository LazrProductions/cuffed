package com.lazrproductions.cuffed.command;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.restraints.Restraints;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class HandcuffCommand {
    public HandcuffCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(
                Commands.literal("cuffed").requires((player) -> {
                    return player.hasPermission(3);
                 }).then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("apply")
                            .then(Commands.argument("item", ItemArgument.item(ctx))
                                .then(Commands.argument("type", EnumArgument.enumArgument(RestraintType.class))
                                    .executes(this::executeApply))))
                        .then(Commands.literal("remove")
                            .then(Commands.argument("type", EnumArgument.enumArgument(RestraintType.class))
                                .executes(this::executeRemove)))
                        .then(Commands.literal("anchor")
                                .then(Commands.literal("set")
                                    .then(Commands.argument("entity", EntityArgument.entity())
                                        .executes(this::executeSetAnchor)))
                                .then(Commands.literal("remove")
                                    .executes(this::executeRemoveAnchor)))
                        .then(Commands.literal("nickname")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("new nickname", ComponentArgument.textComponent())
                                            .executes(this::executeSetNickname)))
                                .then(Commands.literal("reset")
                                    .executes(this::executeResetNickname)))));
    }

    private int executeApply(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            RestraintType type = ctx.getArgument("type", RestraintType.class);
            ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);
            if (player != null && sender != null && stack != null && !stack.isEmpty()) {
                if(type == RestraintType.Arm) {
                    if (Restraints.GetRestraintFromStack(stack, RestraintType.Arm, player, sender) instanceof AbstractArmRestraint arm) {
                        RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                                .getRestrainableCapability(player);
                        if (c.TryEquipRestraint(player, sender, arm))
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.arms.success", player.getName(), stack.getDisplayName()));
                        else
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.arms.failure.alreadyrestrained", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                    } else
                        sender.sendSystemMessage(Component.translatable("command.cuffed.apply.arms.failure.wrong_type", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                } else if(type == RestraintType.Leg) {
                    if (Restraints.GetRestraintFromStack(stack, RestraintType.Leg,  player, sender) instanceof AbstractLegRestraint leg) {
                        RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                                .getRestrainableCapability(player);
                        if (c.TryEquipRestraint(player, sender, leg))
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.legs.success",
                                    player.getName(), stack.getDisplayName()));
                        else
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.legs.failure.alreadyrestrained", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                    } else
                        sender.sendSystemMessage(Component.translatable("command.cuffed.apply.legs.failure.wrong_type", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                } else if(type == RestraintType.Head) {
                    if (Restraints.GetRestraintFromStack(stack, RestraintType.Head, player, sender) instanceof AbstractHeadRestraint head) {
                        RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                                .getRestrainableCapability(player);
                        if (c.TryEquipRestraint(player, sender, head))
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.head.success", player.getName(), stack.getDisplayName()));
                        else
                            sender.sendSystemMessage(Component.translatable("command.cuffed.apply.head.failure.alreadyrestrained", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                    } else
                        sender.sendSystemMessage(Component.translatable("command.cuffed.apply.head.failure.wrong_type", stack.getDisplayName(), player.getName()).withStyle(ChatFormatting.RED));
                } else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.apply.failure", stack.getDisplayName(), player.getName()));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeRemove(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            RestraintType t = ctx.getArgument("type", RestraintType.class);
            if (player != null && sender != null) {
                RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                        .getRestrainableCapability(player);
                if (c.TryUnequipRestraint(player, sender, t))
                    sender.sendSystemMessage(Component.translatable("command.cuffed.remove.success", player.getName(),
                        Component.translatable(t == RestraintType.Arm ? "info.cuffed.arms" : t == RestraintType.Head ? "info.cuffed.head" : "info.cuffed.legs")));
                else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.remove.failure", player.getName(),
                        Component.translatable(t == RestraintType.Arm ? "info.cuffed.arms" : t == RestraintType.Head ? "info.cuffed.head" : "info.cuffed.legs")));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeSetNickname(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            Component n = ComponentArgument.getComponent(ctx, "new nickname");
            if (player != null && sender != null && n != null) {
                INicknamable nicknamable = (INicknamable) player;
                nicknamable.setNickname(n);
                sender.sendSystemMessage(Component.translatable("command.cuffed.nickname.set", player.getName(), n));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeResetNickname(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                INicknamable nicknamable = (INicknamable) player;
                nicknamable.setNickname(null);
                sender.sendSystemMessage(Component.translatable("command.cuffed.nickname.reset", player.getName()));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeSetAnchor(CommandContext<CommandSourceStack> ctx) {
        try {
            Entity anchor = EntityArgument.getEntity(ctx, "entity");
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (anchor != null && player != null && sender != null) {
                IAnchorableEntity anchorable = (IAnchorableEntity)player;
                anchorable.setAnchoredTo(anchor);
                sender.sendSystemMessage(Component.translatable("command.cuffed.anchor.set", player.getDisplayName(), 
                    anchor.getDisplayName()));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int executeRemoveAnchor(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            if (player != null && sender != null) {
                IAnchorableEntity anchorable = (IAnchorableEntity)player;
                anchorable.setAnchoredTo(null);
                sender.sendSystemMessage(Component.translatable("command.cuffed.anchor.remove", 
                    player.getDisplayName()));
                return 1;
            }
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
