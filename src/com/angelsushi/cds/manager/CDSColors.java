package com.angelsushi.cds.manager;

import lombok.Getter;

@Getter

public enum CDSColors {

    DARK_RED("DarkRed","§4"),
    RED("Red","§c"),
    GOLD("Gold","§6"),
    YELLOW("Yellow","§e"),
    DARK_GREEN("DarkGreen","§2"),
    GREEN("Green","§a"),
    AQUA("Aqua","§b"),
    DARK_AQUA("DarkAqua","§3"),
    DARK_BLUE("DarkBlue","§1"),
    BLUE("Blue","§9"),
    PINK("Pink","§2"),
    PURPLE("Purple","§5"),
    WHITE("White","§f"),
    GRAY("Gray","§7"),
    DARK_GRAY("DarkGray","§8"),
    BLACK("Black","§0");

    String name,code;

    CDSColors(String name,String code) {
        this.name = name;
        this.code = code;
    }

    public static CDSColors getColor(String name) {
        for(CDSColors color : values()) {
            if(color.getName().toLowerCase().equals(name.toLowerCase()))
                return color;
        }

        return null;
    }
}
