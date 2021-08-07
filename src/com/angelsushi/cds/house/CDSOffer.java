package com.angelsushi.cds.house;

import com.angelsushi.cds.house.CDSHouse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class CDSOffer {

    private Player sender;
    private Player receiver;
    private int offerPrice;
    private CDSHouse house;
}
