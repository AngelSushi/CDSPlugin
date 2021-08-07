package com.angelsushi.cds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public abstract class CDSCommand implements CommandExecutor {

    private final Map<String, CommandExecutor> subCommands = new HashMap<>();

    public void registerSubCommand(String label, CommandExecutor subCommand) {
        subCommands.put(label.toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            CommandExecutor child = subCommands.get(args[0].toLowerCase());
            if (child != null) {
                label = args[0];
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                return child.onCommand(sender, command, label, newArgs);
            }
        }
        return runCommand(sender, command, label, args);
    }


    public abstract boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args);

}
