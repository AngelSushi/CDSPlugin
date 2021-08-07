package com.angelsushi.cds.house;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class CDSHouse {

    private String name;
    private Cuboid place;
    private boolean buy;
    private ArrayList<String> owners = new ArrayList<>();

    public CDSHouse(String name,Cuboid place) {
        this.name = name;
        this.place = place;
    }
}
