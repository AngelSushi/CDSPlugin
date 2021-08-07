package com.angelsushi.cds;

import com.angelsushi.cds.bank.BankFunctions;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.HouseFunctions;
import com.angelsushi.cds.manager.*;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;

public class CDSListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!e.getPlayer().hasPlayedBefore()) {
            CDSPlayer player = new CDSPlayer(e.getPlayer().getName(),e.getPlayer().getUniqueId());
            CDS.getInstance().getCdsPlayers().add(player);
        }

        if(CDS.getInstance().getScoreboard() != null) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(e.getPlayer(),new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);

            for(Player p : Bukkit.getOnlinePlayers()) {
                CDSScoreboardLoadEvent changeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
                Bukkit.getServer().getPluginManager().callEvent(changeEvent);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if(e.getPlayer().isOp() && !CDS.getInstance().isEnd()) {
            if(e.getLine(0).startsWith("$") && e.getLine(2).startsWith("$"))
                HouseFunctions.changeHouseSign(e);
            else if(e.getLine(0).equals("[Bank]"))
                BankFunctions.changeBankSign(e);
        }
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent e) {
        if(e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.VILLAGER) {
            if(CDS.getInstance().getCdsVillagers().containsKey(e.getEntity().getUniqueId()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getItem() != null && e.getItem().getType() == Material.STICK && e.getItem().getItemMeta().getDisplayName().equals("§5House Creator")) {
            if(e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.AIR) {
                e.setCancelled(true);
                if(e.getAction() == Action.LEFT_CLICK_BLOCK)
                    CDS.getInstance().setHouseSecond(e.getClickedBlock().getLocation());
                else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    CDS.getInstance().setHouseFirst(e.getClickedBlock().getLocation());
            }
        }

        if(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.WALL_SIGN) { /* TOUT REFAIRE AVEC LA TEAM */
            Sign sign = (Sign)e.getClickedBlock().getState();
            CDSPlayer player = CDSFunctions.getCDSPlayerById(e.getPlayer().getUniqueId());
            if(CDSFunctions.isHouseSign(e.getClickedBlock()) && !CDS.getInstance().isEnd()) {
                if(CDSFunctions.getHouseByName(sign.getLine(0).substring(2)) != null) {
                    e.setCancelled(true);
                    CDSHouse house = CDSFunctions.getHouseByName(sign.getLine(0).substring(2));
                    if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
                        HouseFunctions.changeHouseSignAction(sign);

                    if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        String action = sign.getLine(1).substring(2);
                        int price = -1;
                        try {
                            price = Integer.parseInt(sign.getLine(2).substring(2));
                        }catch(Exception e1) {
                            e1.printStackTrace();
                        }

                        switch(action) {
                            case "Buy":
                                HouseFunctions.buyHouse(e,house,player,price,sign);
                                break;
                            case "Sell":
                                HouseFunctions.sellHouse(e,house,player,price,sign);
                                break;
                            case "Offer":
                                HouseFunctions.offerHouse(e,house);
                                break;
                        }
                    }
                }

            }
            else if(CDSFunctions.isBankSign(sign) && e.getAction() == Action.RIGHT_CLICK_BLOCK && !CDS.getInstance().isEnd()){
                e.setCancelled(true);
                if(player.hasTeam()) {
                    Team team = player.getTeam();
                    if(sign.getLine(1).equalsIgnoreCase("deposit"))
                        BankFunctions.depositEmeralds(e.getPlayer(),player,team,new ItemStack(Material.valueOf(sign.getLine(3).toUpperCase()),Integer.valueOf(sign.getLine(2).substring(2))));
                    else if(sign.getLine(1).equalsIgnoreCase("withdraw"))
                        BankFunctions.withdrawEmeralds(e.getPlayer(),player,team,new ItemStack(Material.valueOf(sign.getLine(3).toUpperCase()),Integer.valueOf(sign.getLine(2).substring(2))));
                }
            }
        }
    }

    @EventHandler
    public void onPingServer(ServerListPingEvent e) {
        if(CDS.getInstance().getBeginDate() != null ) {
            if(!CDS.getInstance().isStart()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime beginDate = CDS.getInstance().getBeginDate();
                int dayRemaining = beginDate.getDayOfMonth() - now.getDayOfMonth();

                if(dayRemaining > 0)
                    e.setMotd("§aServeur Cité des Sables \n §a§lOuverture du serveur dans: §e" + dayRemaining + " jour(s)");
                else {
                    int hoursRemaining = beginDate.getHour() - now.getHour();

                    if(hoursRemaining > 0)
                        e.setMotd("§aServeur Cité des Sables \n §a§lOuverture du serveur dans: §e" + hoursRemaining + " heure(s)");
                    else {
                        int minutesRemaining = beginDate.getMinute() - now.getMinute();

                        if(minutesRemaining > 0)
                            e.setMotd("§aServeur Cité des Sables \n §a§lOuverture du serveur dans: §e" + minutesRemaining + " minute(s)");
                    }
                }
            }
            else {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime beginDate = CDS.getInstance().getBeginDate();

                int monthEndDay = beginDate.getDayOfMonth() + CDS.getInstance().getEventDuration();

                int dayRemaining = monthEndDay - now.getDayOfMonth();
                e.setMotd("§aServeur Cité des Sables \n §a§lFermeture de la cité dans: §e" + dayRemaining + " jour(s)");
            }
        }
    }
}
