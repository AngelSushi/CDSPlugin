package com.angelsushi.cds.house.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.house.event.CDSBuyHouseEvent;
import com.angelsushi.cds.house.event.CDSSellHouseEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.house.CDSOffer;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CDSOfferAccept extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {

            Player p = (Player)sender;
            CDSPlayer player = CDSFunctions.getCDSPlayerById(p.getUniqueId());

            if(player != null && player.isHouseOwner()) {
                if(args.length == 2) {
                    Player senderOffer = Bukkit.getPlayer(args[1]);
                    CDSPlayer cdsSender = CDSFunctions.getCDSPlayerById(senderOffer.getUniqueId()); // Attention si le joueur est déco
                    String houseName = args[0];
                    if(senderOffer != null && cdsSender != null) {
                        if(CDSFunctions.getHouseByName(houseName) != null) {
                            if(cdsSender.hasSendOffer(houseName)) {
                                if(player.hasTeam() && cdsSender.hasTeam()) {
                                    Team playerTeam = player.getTeam();
                                    Team senderTeam = cdsSender.getTeam();

                                    CDSOffer offer = CDSFunctions.getOffer(houseName,cdsSender);
                                    offer.getHouse().getOwners().clear();
                                    offer.getHouse().getOwners().add(cdsSender.getId().toString());

                                    if(senderTeam.isAccountSet() && cdsSender.canBuyWithBank(offer.getOfferPrice())) {
                                        senderTeam.setAccount(senderTeam.getAccount() - offer.getOfferPrice());

                                        if(playerTeam.isAccountSet())
                                            playerTeam.setAccount(playerTeam.getAccount() + offer.getOfferPrice());
                                        else  {
                                            playerTeam.setAccountSet(true);
                                            playerTeam.setAccount(offer.getOfferPrice());
                                        }

                                        Sign sign = (Sign)CDSFunctions.getSignByHouseName(houseName).getState();
                                        sign.setLine(3,cdsSender.getName());
                                        sign.update();

                                        CDS.getInstance().getCdsOffers().remove(offer);

                                        CDSSellHouseEvent sellHouseEvent = new CDSSellHouseEvent(offer.getHouse(),cdsSender,player, Message.SELL_HOUSE);
                                        Bukkit.getServer().getPluginManager().callEvent(sellHouseEvent);

                                        CDSBuyHouseEvent buyHouseEvent = new CDSBuyHouseEvent(offer.getHouse(),cdsSender,Message.BUY_HOUSE);
                                        Bukkit.getServer().getPluginManager().callEvent(buyHouseEvent);

                                        senderOffer.sendMessage(buyHouseEvent.getMessage());
                                        p.sendMessage(sellHouseEvent.getMessage());
                                    }
                                    else
                                        ErrorFunctions.error(p,ErrorType.NOT_ENOUGH_EMERALDS);
                                }
                                else
                                    ErrorFunctions.error(p, ErrorType.NO_PLAYER_TEAM);
                            }
                            else
                                ErrorFunctions.error(p,ErrorType.NO_OFFER);
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.ALREADY_EXIST_HOUSE);
                    }
                    else {
                        // Check s'il est déco
                    }
                }
                else
                    ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p,ErrorType.NOT_HOUSE_OWNER);

            return true;
        }
        return false;
    }
}
