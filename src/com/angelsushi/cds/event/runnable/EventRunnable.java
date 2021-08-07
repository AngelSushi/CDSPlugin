package com.angelsushi.cds.event.runnable;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.event.events.CDSEventEnd;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class EventRunnable extends BukkitRunnable {

    private LocalDateTime lastTime;
    @Override
    public void run() {
        if(CDS.getInstance().isStart()) {
            LocalDateTime beginDate = CDS.getInstance().getBeginDate();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = beginDate.plusDays(CDS.getInstance().getEventDuration());

            if(now.getYear() == endDate.getYear() && now.getMonthValue() == endDate.getMonthValue() && now.getDayOfMonth() == endDate.getDayOfMonth()) {
                if(now.getHour() == endDate.getHour() && now.getMinute() == endDate.getMinute()) {
                    CDSEventEnd eventEnd = new CDSEventEnd(CDS.getInstance(), Message.EVENT_STOP);
                    Bukkit.getServer().getPluginManager().callEvent(eventEnd);

                    Bukkit.broadcastMessage(eventEnd.getMessage());
                    cancel();
                }
            }

            if(lastTime != null && lastTime.getDayOfMonth() != now.getDayOfMonth()) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(p,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
                    Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
                }
            }

            lastTime = now;

        }
        else
            cancel();

    }
}
