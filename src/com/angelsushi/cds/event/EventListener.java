package com.angelsushi.cds.event;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.event.events.CDSEventEnd;
import com.angelsushi.cds.event.events.CDSEventStart;
import com.angelsushi.cds.event.runnable.EventRunnable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

    @EventHandler
    public void onEventStart(CDSEventStart e) {
        e.getInstance().setStart(true);
        Bukkit.setWhitelist(false);

        EventRunnable eventRunnable = new EventRunnable();
        eventRunnable.runTaskTimer(CDS.getInstance(),0,20);
    }

    @EventHandler
    public void onEventEnd(CDSEventEnd e) {
        e.getInstance().setEnd(true);
    }
}
