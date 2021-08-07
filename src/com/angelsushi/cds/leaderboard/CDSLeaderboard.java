package com.angelsushi.cds.leaderboard;

import fr.watch54.display.holograms.HologramServer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CDSLeaderboard {

    private HologramServer leaderboard;
    private ClassementType type;
    private int max;
}
