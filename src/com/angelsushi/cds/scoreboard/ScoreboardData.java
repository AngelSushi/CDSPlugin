package com.angelsushi.cds.scoreboard;

public enum     ScoreboardData {

    PLAYER_NAME("%n"), 
    TEAM_NAME("%t"), 
    CLASSEMENT_TYPE("%tc"), 
    TEAM_RANK("%r"), 
    EMERALDS("%e"),
    TEAM_PLAYERS("%tp"), 
    TEAM_NUMBER("%nt"), 
    CONNECTED_PLAYERS("%np"), 
    HOUSE_NUMBER("%nh"), 
    TOTAL_HOUSE("%tnh"), 
    END_DATE("%de"), 
    ACTUAL_DAY("%ad"), 
    REMAINING_DAYS("%rd"); 

    String identifier;

    ScoreboardData(String identifier) {
        this.identifier = identifier;
    }
}
