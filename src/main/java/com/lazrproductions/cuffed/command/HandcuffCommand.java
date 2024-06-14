package com.lazrproductions.cuffed.command;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.restraints.Restraints;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
                                .executes(this::executeApply)))
                        .then(Commands.literal("remove")
                            .then(Commands.argument("type", EnumArgument.enumArgument(RestraintType.class))
                                .executes(this::executeRemove)))
                        .then(Commands.literal("nickname")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("new nickname", ComponentArgument.textComponent())
                                            .executes(this::executeSetNickname)))
                                .then(Commands.literal("reset").executes(this::executeResetNickname)))
                    ));
}

    private int executeApply(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);
            if (player != null && sender != null && stack != null && !stack.isEmpty()) {
                if (Restraints.GetRestraintFromStack(stack, player, sender) instanceof AbstractArmRestraint arm) {
                    RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                            .getRestrainableCapability(player);
                    if (c.TryEquipRestraint(player, sender, arm))
                        sender.sendSystemMessage(Component.translatable("command.cuffed.apply.arms.success",
                                player.getName(), stack.getDisplayName()));
                    else
                        sender.sendSystemMessage(Component
                                .translatable("command.cuffed.apply.arms.failure.alreadyrestrained", player.getName()));
                } else if (Restraints.GetRestraintFromStack(stack, player,
                        sender) instanceof AbstractLegRestraint leg) {
                    RestrainableCapability c = (RestrainableCapability) CuffedAPI.Capabilities
                            .getRestrainableCapability(player);
                    if (c.TryEquipRestraint(player, sender, leg))
                        sender.sendSystemMessage(Component.translatable("command.cuffed.apply.legs.success",
                                player.getName(), stack.getDisplayName()));
                    else
                        sender.sendSystemMessage(Component.translatable(
                                "command.cuffed.apply.legs.failure.alreadyrestrained", stack.getDisplayName()));
                } else
                    sender.sendSystemMessage(
                            Component.translatable("command.cuffed.apply.failure.invaliditem", stack.getDisplayName()));
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
                            t == RestraintType.Arm ? "arm" : "leg"));
                else
                    sender.sendSystemMessage(Component.translatable("command.cuffed.remove.failure", player.getName(),
                            t == RestraintType.Arm ? "arm" : "leg"));
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
}
