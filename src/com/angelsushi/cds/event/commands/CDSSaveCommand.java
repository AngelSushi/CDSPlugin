package com.angelsushi.cds.event.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.event.events.CDSChangeSaveEvent;
import com.angelsushi.cds.manager.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CDSSaveCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(p.isOp()) {
                if(args.length == 1 && (args[0].equals("file") || args[0].equals("db"))) {
                    boolean saveFile = args[0].equals("file");
                    CDS.getInstance().setSaveFile(saveFile);
                    CDSChangeSaveEvent saveEvent = new CDSChangeSaveEvent(p,saveFile,Message.SAVE_MOD);
                    Bukkit.getServer().getPluginManager().callEvent(saveEvent);

                    p.sendMessage(saveEvent.getMessage());
                }
                else
                    ErrorFunctions.error(p, ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p,ErrorType.NOT_OP);

            return true;
        }
        return false;
    }
}
