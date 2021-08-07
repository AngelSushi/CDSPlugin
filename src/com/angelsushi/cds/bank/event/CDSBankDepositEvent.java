package com.angelsushi.cds.bank.event;

import com.angelsushi.cds.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSBankDepositEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private int amount;
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
