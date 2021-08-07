package com.angelsushi.cds.manager;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.CDSOffer;
import com.angelsushi.cds.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CDSPlayer {

    private String name;
    private UUID id;
    private boolean houseOwner;
    private Team team;

    public CDSPlayer(String name,UUID id) {
        this.name = name;
        this.id = id;
    }

    public boolean hasTeam() {
        return team != null;
    }

    public boolean hasEnoughInInventory(ItemStack i, int amount, Inventory inv) {
        for(ItemStack item : inv.getContents()) {
            if(item != null && item.getType() == i.getType() && item.getAmount() >= amount) {
                return true;
            }
        }

        return false;
    }

    public boolean canBuyWithBank(int price) {
        return team.getAccount() >= price;
    }

    public boolean canBuyWithInventory(int price,Inventory inv) {
        int amount = 0;

        for(ItemStack item : inv.getContents()) {
            if(item != null && item.getType() == Material.EMERALD)
                amount += item.getAmount();
            else if(item != null && item.getType() == Material.EMERALD_BLOCK)
                amount += item.getAmount() * 9;
        }

        return amount >= price;
    }

    public ArrayList<CDSHouse> getHouses() {
        ArrayList<CDSHouse> houses = new ArrayList<>();

        for(CDSHouse house : CDS.getInstance().getCdsHouses()) {
            if(house.getOwners().contains(id.toString()))
                houses.add(house);
        }

        return houses;
    }

    public boolean hasSendOffer(String houseName) {
        for(CDSOffer offer : CDS.getInstance().getCdsOffers()) {
            if(offer.getSender().getUniqueId().toString().equals(id.toString()) && offer.getHouse().getName().equals(houseName))
                return true;
        }

        return false;
    }


}
