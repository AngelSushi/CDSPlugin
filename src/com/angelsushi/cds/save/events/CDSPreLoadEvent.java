package com.angelsushi.cds.save.events;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.manager.CDSPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Connection;

@AllArgsConstructor
public class CDSPreLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private CDS instance;
    @Getter
    private Connection conn;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
