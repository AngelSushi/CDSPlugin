package com.angelsushi.cds.event.commands;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.CDSCommand;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.event.events.CDSDateEvent;
import com.angelsushi.cds.event.runnable.CDSStartEventRunnable;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.events.CDSScoreboardLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CDSManageCommand extends CDSCommand {

    @Override
    public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player)sender;

            if(p.isOp()) {
                if(args.length == 3) {
                    int day = -1;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime date = LocalDateTime.parse(args[0] + " " + args[1], formatter);

                    try {
                        day = Integer.parseInt(args[2]);
                    }catch(Exception e) {
                        e.printStackTrace();
                        ErrorFunctions.error(p,ErrorType.CONVERT_INTEGER);
                    }

                    if(date != null) {
                        if(day > 0) {
                            LocalDateTime now = LocalDateTime.now();
                            int year = 1900 + date.getYear();
                            int month = date.getMonthValue() + 1;
                            // Vérifier le date

                            if(year > now.getYear() && month > now.getMonthValue() && date.getDayOfMonth() > now.getDayOfMonth())
                                setDate(date,day,p);
                            else if(year == now.getYear() && month >= now.getMonthValue() && date.getDayOfMonth() >= now.getDayOfMonth()) {
                                if(month == now.getMonthValue() && date.getDayOfMonth() == now.getDayOfMonth()) {
                                    if(date.getHour() >= now.getHour() && date.getMinute() >= now.getMinute())
                                        setDate(date,day,p);
                                }
                                else
                                    setDate(date,day,p);
                            }
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

        return false;
    }

    private void setDate(LocalDateTime date, int day,Player p) {
        CDS.getInstance().setBeginDate(date);
        CDS.getInstance().setEventDuration(day);
        CDSStartEventRunnable startEventRunnable = new CDSStartEventRunnable();
        startEventRunnable.runTaskTimer(CDS.getInstance(),0,20);

        CDSDateEvent dateEvent = new CDSDateEvent(date,day,Message.ADD_BEGINDATE);
        Bukkit.getServer().getPluginManager().callEvent(dateEvent);

        p.sendMessage(dateEvent.getMessage());

        for(Player player : Bukkit.getOnlinePlayers()) {
            CDSScoreboardLoadEvent scoreboardChangeEvent = new CDSScoreboardLoadEvent(player,new CDSScoreboard(CDS.getInstance().getScoreboard().getObjective().getDisplayName()));
            Bukkit.getServer().getPluginManager().callEvent(scoreboardChangeEvent);
        }

    }
}
