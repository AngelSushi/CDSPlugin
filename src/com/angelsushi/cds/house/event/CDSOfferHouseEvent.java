package com.angelsushi.cds.house.event;

import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.CDSOffer;
import com.angelsushi.cds.manager.CDSPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSOfferHouseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private CDSOffer offer;
    private CDSPlayer buyer;
    private CDSPlayer seller;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
