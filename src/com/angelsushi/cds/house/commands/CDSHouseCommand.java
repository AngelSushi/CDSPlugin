package com.angelsushi.cds.house.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.house.event.CDSDeleteHouseEvent;
import com.angelsushi.cds.house.event.CDSHouseCreateEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.Cuboid;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CDSHouseCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(p.isOp()) {
                CDSPlayer cdsPlayer = CDSFunctions.getCDSPlayerById(p.getUniqueId());
                if(args.length == 0) {
                    ItemStack item = new ItemStack(Material.STICK);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName("§5House Creator");
                    itemMeta.setLore(Arrays.asList("Right click to select the first position","Left click to select the second position"));
                    item.setItemMeta(itemMeta);
                    p.getInventory().addItem(item);
                }
                else if(args.length == 2 && args[0].equals("create")) {
                    if(CDS.getInstance().getHouseFirst() != null && CDS.getInstance().getHouseSecond() != null) {
                        if(CDSFunctions.getHouseByName(args[1]) == null) {
                            String name = args[1];
                            // Vérifier qu'il y a pas déjà une maison avec ce nom

                            Cuboid cuboid = new Cuboid(CDS.getInstance().getHouseFirst(),CDS.getInstance().getHouseSecond());
                            CDSHouse house = new CDSHouse(name,cuboid);

                            CDS.getInstance().getCdsHouses().add(house);
                            cdsPlayer.setHouseOwner(true);

                            CDSHouseCreateEvent houseCreateEvent = new CDSHouseCreateEvent(house,cdsPlayer,Message.CREATE_HOUSE);
                            Bukkit.getServer().getPluginManager().callEvent(houseCreateEvent);

                            p.sendMessage(houseCreateEvent.getMessage());
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.ALREADY_EXIST_HOUSE);
                    }
                }
                else if(args.length == 2 && args[0].equals("delete") && CDSFunctions.getHouseByName(args[1]) != null) {
                    CDS.getInstance().getCdsHouses().remove(CDSFunctions.getHouseByName(args[1]));

                    CDSDeleteHouseEvent deleteHouseEvent = new CDSDeleteHouseEvent(CDSFunctions.getHouseByName(args[1]),cdsPlayer,Message.DELETE_HOUSE);
                    Bukkit.getServer().getPluginManager().callEvent(deleteHouseEvent);

                    p.sendMessage(deleteHouseEvent.getMessage());

                }
                else
                    ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p, ErrorType.NOT_OP);

            return true;
        }

        return false;
    }
}
