package com.angelsushi.cds.bank;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.bank.event.CDSBankCreateEvent;
import com.angelsushi.cds.bank.event.CDSBankDepositEvent;
import com.angelsushi.cds.bank.event.CDSBankWithdrawEvent;
import com.angelsushi.cds.leaderboard.CDSLeaderboard;
import com.angelsushi.cds.leaderboard.event.CDSChangeLeaderboardEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import com.angelsushi.cds.team.Team;
import fr.watch54.display.interfaces.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BankListener implements Listener {

    @EventHandler
    public void onBankDeposit(CDSBankDepositEvent e) {

        CDSLeaderboard leaderboard = CDSFunctions.getLeaderboardByType(e.getTeam().getClassementType());

        if(leaderboard != null) {
            CDSChangeLeaderboardEvent changeLeaderboardEvent = new CDSChangeLeaderboardEvent(e.getPlayer(),leaderboard,e.getTeam().getClassementType());
            Bukkit.getServer().getPluginManager().callEvent(changeLeaderboardEvent);
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }

    @EventHandler
    public void onBankWithdraw(CDSBankWithdrawEvent e) {
        CDSLeaderboard leaderboard = CDSFunctions.getLeaderboardByType(e.getTeam().getClassementType());

        if(leaderboard != null) {
            CDSChangeLeaderboardEvent changeLeaderboardEvent = new CDSChangeLeaderboardEvent(e.getPlayer(),leaderboard,e.getTeam().getClassementType());
            Bukkit.getServer().getPluginManager().callEvent(changeLeaderboardEvent);
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }
    }
}
