package com.angelsushi.cds.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CDSMerchantInventory {

    private UUID entityId;
    private String name;
    private HashMap<Integer,ItemStack> items;

    public CDSMerchantInventory(UUID entityId,String name) {
        this.entityId = entityId;
        this.name = name;
        this.items = new HashMap<>();
    }
}
