package com.angelsushi.cds.house;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.house.event.CDSBuyHouseEvent;
import com.angelsushi.cds.house.event.CDSHouseCreateEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class HouseListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        CDSHouse house = CDSFunctions.isInHouse(e.getBlockPlaced().getLocation());

        if(house != null) {
            if(!house.getOwners().contains(e.getPlayer().toString())) {
                e.setCancelled(true);
                ErrorFunctions.error(e.getPlayer(),ErrorType.PLACE_BLOCK);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        CDSHouse house = CDSFunctions.isInHouse(e.getBlock().getLocation());

        if(house != null) {
            if(!house.getOwners().contains(e.getPlayer().toString())) {
                e.setCancelled(true);
                ErrorFunctions.error(e.getPlayer(), ErrorType.BREAK_BLOCK);
            }
        }
    }

    @EventHandler
    public void onHouseAdd(CDSHouseCreateEvent e) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }

    @EventHandler
    public void onHouseBuy(CDSBuyHouseEvent e) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }
}
