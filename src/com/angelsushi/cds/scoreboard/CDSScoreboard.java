package com.angelsushi.cds.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class CDSScoreboard {

    public static final int MAX_LINES = 16;

    private Scoreboard scoreboard = null;
    @Getter
    private Objective objective = null;
    @Getter
    private List<String> modifies = new ArrayList<>();

    public CDSScoreboard(String title) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(title, "dummy");
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }


    private String getLineCoded(String line) {
        String result = line;
        while (modifies.contains(result))
            result += ChatColor.RESET;
        return result.substring(0, Math.min(40, result.length()));
    }

    public void addLine(String line) {
        // if (modifies.size() > MAX_LINES)
        //   throw new IndexOutOfBoundsException("You cannot add more than 16 lines.");
        String modified = getLineCoded(line);
        modifies.add(modified);
        objective.getScore(modified).setScore(-(modifies.size()));
    }

    public void addBlankSpace() {
        addLine(" ");
    }

    public void clear() {
        for(int i = 0;i<MAX_LINES;i++) {
            if(i < modifies.size()) {
                String oldModified = modifies.get(i);
                scoreboard.resetScores(oldModified);
            }
        }
    }

    public void setLine(int index, String line) {
        if (index < 0 || index >= MAX_LINES)
            throw new IndexOutOfBoundsException("The index cannot be negative or higher than 15.");

        if(index < modifies.size()) {
            String oldModified = modifies.get(index);
            scoreboard.resetScores(oldModified);
        }

        String modified = getLineCoded(line);
        modifies.set(index, modified);
        objective.getScore(modified).setScore(-(index + 1));
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public String toString() {
        String out = "";
        int i = 0;
        for (String string : modifies)
            out += -(i + 1) + ")-> " + string + ";\n";
        return out;
    }
}
