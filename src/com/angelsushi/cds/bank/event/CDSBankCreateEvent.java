package com.angelsushi.cds.bank.event;

import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CDSBankCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Villager villager;
    private Block sign;
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