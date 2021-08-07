package com.angelsushi.cds.error;

import com.angelsushi.cds.manager.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ErrorFunctions {

    public static void error(Player p,ErrorType type) {
        CDSErrorEvent errorEvent = new CDSErrorEvent(type, Message.ERROR_MESSAGE);
        Bukkit.getServer().getPluginManager().callEvent(errorEvent);

        p.sendMessage(errorEvent.getMessage());
    }
}
