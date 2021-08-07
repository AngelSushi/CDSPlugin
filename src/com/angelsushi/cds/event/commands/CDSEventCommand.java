package com.angelsushi.cds.event.commands;

import com.angelsushi.cds.CDSCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CDSEventCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        return false;
    }
}
