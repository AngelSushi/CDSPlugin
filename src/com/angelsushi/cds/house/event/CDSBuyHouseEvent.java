package com.angelsushi.cds.house.event;

import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.manager.CDSPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSBuyHouseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private CDSHouse house;
    private CDSPlayer buyer;
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
