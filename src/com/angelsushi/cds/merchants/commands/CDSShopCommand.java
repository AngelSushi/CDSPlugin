package com.angelsushi.cds.merchants.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.manager.VillagerType;
import com.angelsushi.cds.merchants.events.CDSAddMerchantEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class CDSShopCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {

            Player p = (Player)sender;

            if(p.isOp()) {
                if(args.length == 1) {
                    Villager villager = (Villager)p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
                    villager.setCustomName(args[0]);
                    villager.setCustomNameVisible(true);
                    CDS.getInstance().getCdsVillagers().put(villager.getUniqueId(), VillagerType.MERCHANT);

                    CDSAddMerchantEvent addMerchantEvent = new CDSAddMerchantEvent(villager,Message.ADD_MERCHANT);
                    Bukkit.getServer().getPluginManager().callEvent(addMerchantEvent);

                    p.sendMessage(addMerchantEvent.getMessage());
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
