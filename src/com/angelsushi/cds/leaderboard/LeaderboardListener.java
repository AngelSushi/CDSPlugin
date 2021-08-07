package com.angelsushi.cds.leaderboard;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.leaderboard.event.CDSChangeLeaderboardEvent;
import com.angelsushi.cds.team.Team;
import fr.watch54.display.interfaces.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardListener implements Listener {

    @EventHandler
    public void onLeaderboardChange(CDSChangeLeaderboardEvent e) {
        ArrayList<Team> teamsRank = CDS.getInstance().getTeams();
        teamsRank.sort(Comparator.comparingInt(obj -> obj.getAccount()));
        Collections.reverse(teamsRank);

        List<Text> listText = new ArrayList<>();
        listText.add(() -> "§aClassement §e" +e.getType().name());

        int index = 0;
        for(Team t : teamsRank) {
            if(t.getClassementType() == e.getType() && index < e.getLeaderboard().getMax()) {
                listText.add(() -> t.getName() + ": §a" + t.getAccount());
                index++;
            }
        }

        e.getLeaderboard().getLeaderboard().setTextList(listText);
    }
}
