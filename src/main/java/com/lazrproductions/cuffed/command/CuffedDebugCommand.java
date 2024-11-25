package com.lazrproductions.cuffed.command;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class CuffedDebugCommand {
        public CuffedDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
                dispatcher.register(
                        Commands.literal("cuffed").requires((source) -> {
                                return source.hasPermission(3) || !source.isPlayer();
                        }).then(Commands.literal("debug")
                                .then(Commands.literal("registries")
                                        .then(Commands.literal("check")
                                                .executes(this::executeRegistriesCheck))
                                        .then(Commands.literal("list")
                                                .executes(this::executeRegistriesList))
                                        .then(Commands.literal("listItems")
                                                .executes(this::executeRegistriesListItems))
                                        .then(Commands.literal("listRestraintsAndItems")
                                                .executes(this::executeRegistriesListItemAndRestraints)))
                                .then(Commands.literal("get")
                                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                                .then(Commands.argument("type", EnumArgument.enumArgument(RestraintType.class))
                                                        .executes(this::executeGet))))));
        }

        private int executeRegistriesCheck(CommandContext<CommandSourceStack> ctx) {
                ServerPlayer sender = ctx.getSource().getPlayer();
                if (sender != null) {
                        int numReg = RestraintAPI.Registries.size();
                        int numRes = RestraintAPI.Registries.total();
                        if (numReg > 0)
                                sender.sendSystemMessage(Component.literal(
                                        "Found " + numReg + " registries for restraints containing a total of " + numRes + " restraints."));
                        else
                                sender.sendSystemMessage(Component.literal(
                                        "Found no registries contianing restraints, something is wrong!"));
                }
                return 0;

        }

        private int executeRegistriesList(CommandContext<CommandSourceStack> ctx) {
                ServerPlayer sender = ctx.getSource().getPlayer();
                if (sender != null) {
                        int numReg = RestraintAPI.Registries.size();
                        int numRes = RestraintAPI.Registries.total();
                        if (numReg > 0) {
                                String s = "[";
                                for (var i : RestraintAPI.Registries.getAllRestraints()) {
                                        s += i.getId() +", ";
                                }
                                s = s.substring(0, s.length()-2);
                                s += "]";
                                sender.sendSystemMessage(Component.literal("Listing all " + numRes + " restraints registered in the " + numReg + " registries.\n" + s));
                        } else
                                sender.sendSystemMessage(Component.literal("Found no registries contianing restraints, something is wrong!"));
                }
                return 0;

        }

        private int executeRegistriesListItems(CommandContext<CommandSourceStack> ctx) {
                ServerPlayer sender = ctx.getSource().getPlayer();
                if (sender != null) {
                        int numReg = RestraintAPI.Registries.size();
                        int numRes = RestraintAPI.Registries.total();
                        if (numReg > 0) {
                                String s = "[";
                                for (var i : RestraintAPI.Registries.getAllRestraintItems()) {
                                        s += i.getDescriptionId() +", ";
                                }
                                s = s.substring(0, s.length()-2);
                                s += "]";
                                sender.sendSystemMessage(Component.literal("Listing all " + numRes + " restraint items registered in the " + numReg + " registries.\n" + s));
                        } else
                                sender.sendSystemMessage(Component.literal("Found no registries contianing restraints, something is wrong!"));
                }
                return 0;

        }

        private int executeRegistriesListItemAndRestraints(CommandContext<CommandSourceStack> ctx) {
                ServerPlayer sender = ctx.getSource().getPlayer();
                if (sender != null) {
                        int numReg = RestraintAPI.Registries.size();
                        int numRes = RestraintAPI.Registries.total();
                        if (numReg > 0) {
                                String s = "[";
                                for (var i : RestraintAPI.Registries.getAllRestraintItemsAndTheirRestraints()) {
                                        s += "{restraint:\"" +i.getSecond().getId() +"\",item:\"" + i.getFirst().getDescriptionId() +"\"}, ";
                                }
                                s = s.substring(0, s.length()-2);
                                s += "]";
                                sender.sendSystemMessage(Component.literal("Listing all " + numRes + " restraints and their items registered in the " + numReg + " registries.\n" + s));
                        } else
                                sender.sendSystemMessage(Component.literal("Found no registries contianing restraints, something is wrong!"));
                }
                return 0;

        }
        
        private int executeGet(CommandContext<CommandSourceStack> ctx) {
                try {
                        ServerPlayer sender = ctx.getSource().getPlayer();
                        if(sender != null) {
                                RestraintType type = ctx.getArgument("type", RestraintType.class);
                                ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);
                                if (stack != null && !stack.isEmpty()) {
                                        CuffedMod.LOGGER.info("command - getting restraint for " + stack.getHoverName().getString());
                                        var r = RestraintAPI.getRestraintFromStack(stack, type, sender, sender);
                                        if(r != null) {
                                                sender.sendSystemMessage(Component.literal("Found the following restraint for the given item and type:\n"+r.serializeNBT()));
                                        } else {
                                                sender.sendSystemMessage(Component.literal("Could not find a restraint for that item and type!"));
                                        }
                                        return 1;
                                }
                                sender.sendSystemMessage(Component.literal("An error occurred!"));
                        }
                        return 0;
                } catch (CommandSyntaxException e) {
                        ServerPlayer sender = ctx.getSource().getPlayer();
                        if(sender != null)
                                sender.sendSystemMessage(Component.literal("Command syntax error"));
                        return 0;
                }
        }
}
