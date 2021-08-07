package com.angelsushi.cds.scoreboard;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onScoreboardLoad(CDSScoreboardLoadEvent e) {
        for(String line : CDS.getInstance().getScoreboard().getModifies())
            e.getScoreboard().addLine(ScoreboardFunctions.ConvertData(line,e.getPlayer(),null));

        e.getPlayer().setScoreboard(e.getScoreboard().getScoreboard());
    }
}
