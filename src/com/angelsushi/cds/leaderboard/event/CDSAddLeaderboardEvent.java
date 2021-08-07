package com.angelsushi.cds.leaderboard.event;

import com.angelsushi.cds.leaderboard.CDSLeaderboard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CDSAddLeaderboardEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private CDSLeaderboard leaderboard;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
