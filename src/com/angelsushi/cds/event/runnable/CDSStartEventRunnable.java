package com.angelsushi.cds.event.runnable;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.event.events.CDSEventStart;
import com.angelsushi.cds.manager.Message;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Date;

public class CDSStartEventRunnable extends BukkitRunnable {


    @Override
    public void run() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginDate = CDS.getInstance().getBeginDate();

        if(beginDate.getYear() == now.getYear() && beginDate.getMonthValue() == now.getMonthValue() && beginDate.getDayOfMonth() == now.getDayOfMonth()) {
            if(beginDate.getHour() == now.getHour() && beginDate.getMinute() == now.getMinute()) {
                if(!CDS.getInstance().isStart()) {
                    CDSEventStart eventStart = new CDSEventStart(CDS.getInstance(), Message.EVENT_START);
                    Bukkit.getServer().getPluginManager().callEvent(eventStart);

                    Bukkit.broadcastMessage(eventStart.getMessage());
                    cancel();
                }
            }
        }

    }
}
