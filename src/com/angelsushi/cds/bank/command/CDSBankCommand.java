package com.angelsushi.cds.bank.command;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.bank.event.CDSBankCreateEvent;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.manager.VillagerType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class CDSBankCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(p.isOp()) {
                if(args.length == 1) {
                    if(args[0].equals("villager")) {
                        Villager villager = (Villager)p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
                        villager.setCustomName("§aBanque");
                        villager.setCustomNameVisible(true);
                        CDS.getInstance().getCdsVillagers().put(villager.getUniqueId(), VillagerType.BANK);

                        CDSBankCreateEvent bankCreateEvent = new CDSBankCreateEvent(villager,null,Message.CREATE_VILLAGER_BANK);
                        Bukkit.getServer().getPluginManager().callEvent(bankCreateEvent);

                        p.sendMessage(bankCreateEvent.getMessage());
                    }
                    else
                        ErrorFunctions.error(p, ErrorType.ARGS_UNAVAILABLE);
                }
                else
                    ErrorFunctions.error(p, ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p, ErrorType.NOT_OP);

            return true;
        }

        return false;
    }
}
