package com.angelsushi.cds.house.commands;

import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.manager.CDSPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CDSOfferList extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {
            Player p =(Player)sender;

            CDSPlayer player = CDSFunctions.getCDSPlayerById(p.getUniqueId());

            if(player != null) {
                if(player.isHouseOwner()) {
                    for(CDSHouse house : player.getHouses())
                        p.sendMessage("Vous possédez la maison " + house.getName() + " aux coordonnées x:" + house.getPlace().getCenter().getBlockX() + " y:" + house.getPlace().getCenter().getBlockY() + " z: " + house.getPlace().getCenter().getBlockZ());
                }
                else
                    ErrorFunctions.error(p, ErrorType.NOT_HOUSE_OWNER);
            }

            return true;
        }

        return false;
    }
}
