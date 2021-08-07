package com.angelsushi.cds.event.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CDSDateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private LocalDateTime date;
    private int duration;
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
