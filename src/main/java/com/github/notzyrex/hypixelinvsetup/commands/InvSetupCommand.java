package com.github.notzyrex.hypixelinvsetup.commands;

import com.github.notzyrex.hypixelinvsetup.main.SetupManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class InvSetupCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "invsetup";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, "load", "delay");
        return Arrays.asList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Usage: /invsetup load OR /invsetup delay <ticks>"));
            return;
        }

        if (args[0].equalsIgnoreCase("load")) {
            if (SetupManager.isRunning()) return;

            SetupManager.start();
        } else if (args[0].equalsIgnoreCase("delay")) {
            if (args.length < 2) {
                sender.addChatMessage(new ChatComponentText("Please provide a delay in ticks."));
                return;
            }

            try {
                int ticks = Integer.parseInt(args[1]);
                SetupManager.setDelayTicks(ticks);
                sender.addChatMessage(new ChatComponentText("Delay set to " + ticks + " ticks."));
            } catch (NumberFormatException e) {
                sender.addChatMessage(new ChatComponentText("Invalid number: " + args[1]));
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Unknown subcommand: " + args[0]));
        }
    }

    public void onUpdate() {
        SetupManager.update();
    }
}
