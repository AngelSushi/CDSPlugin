package com.angelsushi.cds.team;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import com.angelsushi.cds.team.events.CDSTeamCreateEvent;
import com.angelsushi.cds.team.events.CDSTeamJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamListener implements Listener {

    @EventHandler
    public void onTeamCreate(CDSTeamCreateEvent e)  {
        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }

    @EventHandler
    public void onTeamJoin(CDSTeamJoinEvent e) {
        if(e.getTeam().getPlayers().size() >= 2 && e.getTeam().getPlayers().size() <= 3)
            e.getTeam().setClassementType(ClassementType.DUO_TRIO);
        else if(e.getTeam().getPlayers().size() >= 4)
            e.getTeam().setClassementType(ClassementType.QUATUOR);

        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }
}
