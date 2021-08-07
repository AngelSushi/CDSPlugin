package com.angelsushi.cds.event.events;

import com.angelsushi.cds.CDS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSEventEnd extends Event {

    private static final HandlerList handlers = new HandlerList();

    private CDS instance;
    @Setter
    private String message;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
