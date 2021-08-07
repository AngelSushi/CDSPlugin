package com.angelsushi.cds.team.events;
;
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
public class CDSTeamJoinEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private CDSPlayer cdsPlayer;
    private Team team;
    @Setter
    private String message;

    public CDSTeamJoinEvent(Player player,CDSPlayer p,Team team) {
        this.player = player;
        this.cdsPlayer = p;
        this.team = team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
