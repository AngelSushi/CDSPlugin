package com.angelsushi.cds.team.events;

import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSTeamInviteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Team team;
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
