package com.angelsushi.cds.team;

import com.angelsushi.cds.manager.CDSColors;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.manager.CDSPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
@Setter
public class Team implements Comparable<Team> {

    private String name;
    private CDSColors color;
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<String> requirePlayers = new ArrayList<>();
    private ClassementType classementType;
    private boolean accountSet;
    private Integer account;

    public Team(String name, CDSColors color, Player creator) {
        this.name = name;
        this.color = color;
        players.add(creator.getUniqueId().toString());
        classementType = ClassementType.SOLO;
    }

    public Team(String name,CDSColors color,ArrayList<String> players,ClassementType type,boolean accountSet, Integer account) {
        this.name = name;
        this.color = color;
        this.players = players;
        this.classementType = type;
        this.accountSet = accountSet;
        this.account = account;
    }

    @Override
    public int compareTo(Team o) {
        if(o != null)
            return getAccount().compareTo(o.getAccount());
        else
            return 0;
    }
}
