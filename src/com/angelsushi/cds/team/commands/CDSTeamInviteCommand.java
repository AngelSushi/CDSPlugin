package com.angelsushi.cds.team.commands;

import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import com.angelsushi.cds.team.events.CDSTeamInviteEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CDSTeamInviteCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player)sender;
            CDSPlayer player = CDSFunctions.getCDSPlayerById(p.getUniqueId());

            if(player != null && player.hasTeam()) {
                if(player.getTeam().getPlayers().get(0).equals(p.getUniqueId().toString())) {
                    if(args.length == 1) {
                        Player targetPlayer = Bukkit.getPlayer(args[0]);

                        if(targetPlayer != null) {
                            Team team = player.getTeam();
                            team.getRequirePlayers().add(targetPlayer.getUniqueId().toString());

                            //Event
                            CDSTeamInviteEvent inviteEvent = new CDSTeamInviteEvent(targetPlayer,team, Message.INVITE_TEAM);
                            Bukkit.getServer().getPluginManager().callEvent(inviteEvent);

                            targetPlayer.sendMessage(inviteEvent.getMessage());
                            p.sendMessage(inviteEvent.getMessage());
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
                    }
                    else
                        ErrorFunctions.error(p, ErrorType.ARGS_UNAVAILABLE);
                }
                else
                    ErrorFunctions.error(p,ErrorType.NO_TEAM_CHIEF);
            }
            else
                ErrorFunctions.error(p,ErrorType.NO_PLAYER_TEAM);

            return true;
        }

        return false;
    }
}
