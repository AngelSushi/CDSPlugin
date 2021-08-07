package com.angelsushi.cds.team.commands;

import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import com.angelsushi.cds.team.events.CDSTeamJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CDSTeamJoinCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player)sender;
            CDSPlayer player = CDSFunctions.getCDSPlayerById(p.getUniqueId());

            if(player != null && !player.hasTeam()) {
                if(args.length == 1) {
                    if(CDSFunctions.existTeam(args[0])) {
                        Team team = CDSFunctions.getTeam(args[0]);

                        if(team.getRequirePlayers().contains(p.getUniqueId().toString())) {
                            team.getRequirePlayers().remove(p.getUniqueId().toString());
                            team.getPlayers().add(p.getUniqueId().toString());

                            // EVENT
                            CDSTeamJoinEvent joinEvent = new CDSTeamJoinEvent(p,player,team, Message.JOIN_TEAM);
                            Bukkit.getServer().getPluginManager().callEvent(joinEvent);

                            p.sendMessage(joinEvent.getMessage());

                            for(String id : team.getPlayers()) {
                                if(Bukkit.getPlayer(UUID.fromString(id)) != null)
                                    Bukkit.getPlayer(UUID.fromString(id)).sendMessage(joinEvent.getMessage());
                            }
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.NOT_INVITE);
                    }
                    else
                        ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
                }
                else
                    ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p, ErrorType.HAS_ALREADY_TEAM);


        }

        return false;
    }
}
