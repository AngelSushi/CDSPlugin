package com.angelsushi.cds.leaderboard.command;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.leaderboard.CDSLeaderboard;
import com.angelsushi.cds.leaderboard.event.CDSAddLeaderboardEvent;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.team.Team;
import fr.watch54.display.holograms.HologramServer;
import fr.watch54.display.interfaces.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CDSLeaderboardCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(p.isOp()) {
                if(args.length == 2) {
                    ClassementType type = ClassementType.valueOf(args[0]);
                    int max = -1;

                    try { max = Integer.parseInt(args[1]); }
                    catch(Exception e) {
                        e.printStackTrace();
                        ErrorFunctions.error(p,ErrorType.CONVERT_INTEGER);
                    }

                    if(type != null) {
                        if(max > 0) {
                            if(CDSFunctions.getLeaderboardByType(type) == null) {
                                ArrayList<Team> teamRank = CDS.getInstance().getTeams();
                                teamRank.sort(Comparator.comparingInt(obj -> obj.getAccount()));

                                List<Text> listText = new ArrayList<>();
                                listText.add(() -> "§aClassement §e" +type.name());
                                Collections.reverse(teamRank);
                                int index = 0;
                                for(Team team : teamRank) {
                                    if(team.getClassementType() == type && index < max) {
                                        listText.add(() -> team.getName() + ": §a" + team.getAccount());
                                        index++;
                                    }
                                }

                                HologramServer leaderboardServer = CDS.getInstance().getHologramManager().createServer(listText, p.getLocation(), true);
                                CDSLeaderboard leaderboard = new CDSLeaderboard(leaderboardServer,type,max);
                                CDS.getInstance().getLeaderboards().add(leaderboard);

                                CDSAddLeaderboardEvent addLeaderboardEvent = new CDSAddLeaderboardEvent(p,leaderboard);
                                Bukkit.getServer().getPluginManager().callEvent(addLeaderboardEvent);
                            }
                            else
                                ErrorFunctions.error(p,ErrorType.ALREADY_LEADERBOARD_EXIST);
                        }
                        else
                            ErrorFunctions.error(p,ErrorType.CONVERT_INTEGER);
                    }
                    else
                        ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
                }
                else
                    ErrorFunctions.error(p,ErrorType.ARGS_UNAVAILABLE);
            }
            else
                ErrorFunctions.error(p, ErrorType.NOT_OP);

            return true;
        }

        return true;
    }
}
