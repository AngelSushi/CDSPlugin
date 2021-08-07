package com.angelsushi.cds.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class InventoryManager {

    public static void OpenBankInventory(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 54,"Banque");
        int[] numbers = {1,10,16,32,64};
        int[] id = {388,133};

        ArrayList<ItemStack> items = new ArrayList<>();

        for(int k = 0;k<2;k++) {
            for(int i = 0;i<2;i++) {
                for(int j = 0;j<numbers.length;j++) {
                    boolean withdraw = k == 0;
                    ItemStack item = new ItemStack(id[i],numbers[j]);
                    ItemMeta meta = item.getItemMeta();
                    int emerald = (numbers[j] * (i*9));
                    if(i == 0) emerald += numbers[j];

                    if(!withdraw) meta.setDisplayName("§cDéposer " + emerald + " émeraudes");
                    else meta.setDisplayName("§aRetirer " + emerald + " émeraudes");
                    item.setItemMeta(meta);
                    items.add(item);
                }
            }
        }

        for(int i = 0;i<2;i++) {
            for(int j = 0;j<5;j++) {
                inventory.setItem((11 + i*9) + j,items.get(10 + (j + i*5)));
            }
        }

        for(int i = 0;i<2;i++) {
            for(int j = 0;j<5;j++) {
                inventory.setItem((38 + i*9) + j,items.get(j + i*5));
            }
        }

        for(int i = 0;i<5;i++) {
            ItemStack item = new ItemStack(160,1,(short)14);
            inventory.setItem(29+i,item);
        }

        for(int i = 0;i<2;i++) {
            String name = "";

            if(i == 0) name = "§cTout déposer";
            else name = "§aTout retirer";

            ItemStack item = new ItemStack(399);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);

            inventory.setItem(28 + 6 *i,item);
        }

        p.openInventory(inventory);
    }
}
