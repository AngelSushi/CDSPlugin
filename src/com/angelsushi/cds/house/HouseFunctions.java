package com.angelsushi.cds.house;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.house.event.CDSBuyHouseEvent;
import com.angelsushi.cds.house.event.CDSOfferHouseEvent;
import com.angelsushi.cds.house.event.CDSSellHouseEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class HouseFunctions {

    public static void changeHouseSign(SignChangeEvent e) {
        if(CDSFunctions.getHouseByName(e.getLine(0).replace("$","")) != null) {
            e.setLine(0,e.getLine(0).replace("$","§c"));
            e.setLine(1,"§aBuy");
            e.setLine(2,e.getLine(2).replace("$","§a"));
            e.setLine(3,"Unknown");
            CDS.getInstance().getSignHouses().add(e.getBlock());
            // Check que la deuxieme ligne est bien un chiffre
        }
        else
            ErrorFunctions.error(e.getPlayer(), ErrorType.NO_HOUSE);
    }

    public static void changeHouseSignAction(Sign sign) {
        String[] values = {"§aBuy","§cSell","§6Offer"};
        int index = -1;
        for(int i = 0;i<values.length;i++) {
            if(values[i].equals(sign.getLine(1)))
                index = i + 1;
        }

        if(index > 2)
            index = 0;

        sign.setLine(1,values[index]);
        sign.update();
    }

    public static void buyHouse(PlayerInteractEvent e, CDSHouse house, CDSPlayer player, int price, Sign sign) {
        if(player.hasTeam()) {
            if( !house.isBuy()){ // Vérifier avec la banque de la team
                if(price > 0) {
                    Team team = player.getTeam();
                    if(team.isAccountSet() && player.canBuyWithBank(price)) {
                        team.setAccount(team.getAccount() - price);
                        house.getOwners().add(e.getPlayer().getUniqueId().toString());
                        e.getPlayer().sendMessage("Maison achetée");
                        // Peu etre nul si le joueur est pas co attention
                        sign.setLine(3, Bukkit.getPlayer(UUID.fromString(house.getOwners().get(0))).getName());
                        sign.update();

                        CDSBuyHouseEvent buyHouseEvent = new CDSBuyHouseEvent(house,player,Message.BUY_HOUSE);
                        Bukkit.getServer().getPluginManager().callEvent(buyHouseEvent);

                        e.getPlayer().sendMessage(buyHouseEvent.getMessage());
                    }
                    else
                        ErrorFunctions.error(e.getPlayer(),ErrorType.NOT_ENOUGH_EMERALDS);
                }
            }
            else
                ErrorFunctions.error(e.getPlayer(),ErrorType.ALREADY_BUY);
        }
        else
            ErrorFunctions.error(e.getPlayer(),ErrorType.NO_PLAYER_TEAM);
    }

    public static void sellHouse(PlayerInteractEvent e,CDSHouse house,CDSPlayer player,int price,Sign sign) {
        if(house.getOwners().get(0).toString().equals(e.getPlayer().getUniqueId().toString())) { // if he is the first owner
            if(player.hasTeam()) {
                Team team = player.getTeam();
                if(team.isAccountSet())
                    team.setAccount(team.getAccount() + price);
                else {
                    team.setAccountSet(true);
                    team.setAccount(price);
                }

                sign.setLine(3,"Unknown");
                sign.update();
                e.getPlayer().sendMessage("Maison vendue pour : " + price);

                CDSSellHouseEvent sellHouseEvent = new CDSSellHouseEvent(house,null,player, Message.SELL_HOUSE);
                Bukkit.getServer().getPluginManager().callEvent(sellHouseEvent);

                e.getPlayer().sendMessage(sellHouseEvent.getMessage());
            }
            else
                ErrorFunctions.error(e.getPlayer(),ErrorType.NO_PLAYER_TEAM);
        }
        else
            ErrorFunctions.error(e.getPlayer(),ErrorType.NOT_HOUSE_BUYER);
    }

    public static void offerHouse(PlayerInteractEvent e,CDSHouse house) {
        if(!house.getOwners().contains(e.getPlayer().getUniqueId().toString())) {
            SignGUI gui = new SignGUI(CDS.getInstance());

            gui.open(e.getPlayer(), new String[] { "", "", "", "" }, new SignGUI.SignGUIListener() {
                @Override
                public void onSignDone(Player player, String[] lines) {
                    int offerPrice = -1;
                    try {
                        offerPrice = Integer.parseInt(lines[0]);
                    }catch(Exception e) {
                        e.printStackTrace();
                        ErrorFunctions.error(player,ErrorType.CONVERT_INTEGER);
                    }

                    CDSPlayer writer = CDSFunctions.getCDSPlayerById(player.getUniqueId());

                    if(writer.hasSendOffer(house.getName())) {
                        ErrorFunctions.error(player,ErrorType.ALREADY_SEND_OFFER);
                        return;
                    }

                    if(offerPrice >= 0) {
                        if(Bukkit.getPlayer(UUID.fromString(house.getOwners().get(0))) != null) {  // Faire en sorte que ca marche aussi pour la déconnexion
                            CDSOffer offer = new CDSOffer(e.getPlayer(),Bukkit.getPlayer(UUID.fromString(house.getOwners().get(0))),offerPrice,house);
                            CDS.getInstance().getCdsOffers().add(offer);

                            CDSOfferHouseEvent offerHouseEvent = new CDSOfferHouseEvent(offer, CDSFunctions.getCDSPlayerById(player.getUniqueId()),CDSFunctions.getCDSPlayerById(UUID.fromString(house.getOwners().get(0))));
                            Bukkit.getServer().getPluginManager().callEvent(offerHouseEvent);
                        }
                        else
                            ErrorFunctions.error(player,ErrorType.NO_PLAYER);
                    }
                    else
                        ErrorFunctions.error(player,ErrorType.CONVERT_INTEGER);
                }
            });
        }
        else
            ErrorFunctions.error(e.getPlayer(),ErrorType.IS_HOUSE_OWNER);
    }
}
