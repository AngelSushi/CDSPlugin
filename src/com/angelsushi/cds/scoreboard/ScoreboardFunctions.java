package com.angelsushi.cds.scoreboard;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.team.Team;
import fr.watch54.display.interfaces.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreboardFunctions {

    public static String ConvertData(String line, Player p, Team t) {
        int index = line.indexOf("%");
        if(index < 0)
            return line;

        String lineIdentifier = line.substring(index);
        String identifier = line.substring(index,index + lineIdentifier.indexOf(" "));

        if(identifier.equalsIgnoreCase(ScoreboardData.PLAYER_NAME.identifier))
            line = line.replace(identifier,p.getName());
        if(identifier.equalsIgnoreCase(ScoreboardData.TEAM_NAME.identifier)) {
            if(t != null)
                line = line.replace(identifier, t.getName());
            else
                line = line.replace(identifier,"Unknown");
        }
        if(identifier.equalsIgnoreCase(ScoreboardData.CLASSEMENT_TYPE.identifier)) {
            if(t != null) {
                line = line.replace(ScoreboardData.PLAYER_NAME.identifier, t.getClassementType().name());
            }
            else
                line = "";
        }
        if(identifier.equalsIgnoreCase(ScoreboardData.TEAM_RANK.identifier))
            line = line.replace(identifier,"" + getRankOfTeam(t));
        if(identifier.equalsIgnoreCase(ScoreboardData.EMERALDS.identifier)){
            if(t != null) {
                line = line.replace(ScoreboardData.PLAYER_NAME.identifier,"" +  t.getAccount());
            }
            else
                line = "";
        }
        if(identifier.equalsIgnoreCase(ScoreboardData.TEAM_PLAYERS.identifier)){
            if(t != null) {
                line = line.replace(ScoreboardData.PLAYER_NAME.identifier,""+ t.getPlayers().size());
            }
            else
                line = "";
        }
        if(identifier.equalsIgnoreCase(ScoreboardData.TEAM_NUMBER.identifier))
            line = line.replace(identifier,"" + CDS.getInstance().getTeams().size());
        if(identifier.equalsIgnoreCase(ScoreboardData.CONNECTED_PLAYERS.identifier))
            line = line.replace(identifier, "" + Bukkit.getServer().getOnlinePlayers().size());
        if(identifier.equalsIgnoreCase(ScoreboardData.HOUSE_NUMBER.identifier))
            line = line.replace(identifier,"" + getHousesPerTeam(t));
        if(identifier.equalsIgnoreCase(ScoreboardData.TOTAL_HOUSE.identifier))
            line = line.replace(identifier,"" + CDS.getInstance().getCdsHouses().size());
        if(identifier.equalsIgnoreCase(ScoreboardData.END_DATE.identifier))
            line = line.replace(identifier,getEndDate());
        if(identifier.equalsIgnoreCase(ScoreboardData.ACTUAL_DAY.identifier))
            line = line.replace(identifier,"" + getActualDay());
        if(identifier.equalsIgnoreCase(ScoreboardData.REMAINING_DAYS.identifier))
            line = line.replace(identifier,"" + getRemainingDays());

        return line;
    }

    private static int getHousesPerTeam(Team team) {
        int number = 0;

        if(team != null) {
           for(CDSHouse house : CDS.getInstance().getCdsHouses()) {
               for(String owner : team.getPlayers()) {
                   if(house.getOwners().contains(owner)) {
                       number++;
                       break;
                   }
               }
           }
        }

        return number;
    }

    private static int getRankOfTeam(Team t) {
        ArrayList<Team> teamRank = CDS.getInstance().getTeams();
        teamRank.sort(Comparator.comparingInt(obj -> obj.getAccount()));

        for(int i = 0;i<teamRank.size();i++) {
            Team team = teamRank.get(i);
            if(t != null && team.getClassementType() == t.getClassementType() && team.getName().equals(t.getName()))
               return i;
        }

        return 0;
    }

    private static String getEndDate() {
        if(CDS.getInstance().getBeginDate() != null) {
            LocalDateTime beginDate = CDS.getInstance().getBeginDate();
            LocalDateTime endDate = beginDate.plusDays(CDS.getInstance().getEventDuration());

            return endDate.getDayOfMonth() + "/" + endDate.getMonthValue() + "/" + endDate.getYear();
        }
        else
            return "";
    }

    private static int getActualDay() {
        if(CDS.getInstance().getBeginDate() != null)
            return (int)Duration.between(LocalDateTime.now().toLocalDate().atStartOfDay(), CDS.getInstance().getBeginDate().toLocalDate().atStartOfDay()).toDays();
        else
            return 0;
    }

    private static int getRemainingDays() {
        if(CDS.getInstance().getBeginDate() != null)
            return (CDS.getInstance().getBeginDate().getDayOfMonth() + CDS.getInstance().getEventDuration()) - LocalDateTime.now().getDayOfMonth();
        else
            return 0;
    }
}
