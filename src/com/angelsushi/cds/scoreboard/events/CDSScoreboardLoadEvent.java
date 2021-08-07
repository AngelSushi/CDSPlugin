package com.angelsushi.cds.scoreboard.events;

import com.angelsushi.cds.scoreboard.CDSScoreboard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSScoreboardLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private CDSScoreboard scoreboard;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
