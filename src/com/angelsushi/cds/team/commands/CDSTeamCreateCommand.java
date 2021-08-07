package com.angelsushi.cds.team.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.CDSColors;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import com.angelsushi.cds.team.events.CDSTeamCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CDSTeamCreateCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player)sender;
            CDSPlayer player = CDSFunctions.getCDSPlayerById(p.getUniqueId());

            if(player != null) {
                if(args.length == 2) {
                    String teamName = args[0];
                    String color = args[1];

                    if(CDSColors.getColor(color) != null) {
                        if(!player.hasTeam()) {
                            if(!CDSFunctions.existTeam(teamName)) {
                                CDSColors teamColor = CDSColors.getColor(color);
                                Team team = new Team(teamName,teamColor,p);
                                player.setTeam(team);
                                team.setClassementType(ClassementType.SOLO);
                                CDS.getInstance().getTeams().add(team);

                                CDSTeamCreateEvent teamCreateEvent = new CDSTeamCreateEvent(player,p,team, Message.CREATE_TEAM);
                                Bukkit.getServer().getPluginManager().callEvent(teamCreateEvent);

                                Bukkit.broadcastMessage(teamCreateEvent.getMessage());
                            }
                            else
                                ErrorFunctions.error(p,ErrorType.ALREADY_EXIST_TEAM);
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.HAS_ALREADY_TEAM);
                    }
                    else
                        ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
                }
                else
                    ErrorFunctions.error(p, ErrorType.ARGS_UNAVAILABLE);
            }

            return true;
        }

        return false;
    }
}