package com.angelsushi.cds.inventory;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.bank.BankFunctions;
import com.angelsushi.cds.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryListener implements Listener {
    private Villager villager;

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        if(!CDS.getInstance().isEnd() && e.getRightClicked().getType() == EntityType.VILLAGER && CDS.getInstance().getCdsVillagers().containsKey(e.getRightClicked().getUniqueId()) && CDS.getInstance().getCdsVillagers().get(e.getRightClicked().getUniqueId()) == VillagerType.BANK) {
            villager =(Villager)e.getRightClicked();
            InventoryManager.OpenBankInventory((Player)e.getPlayer());
        }

        if(!CDS.getInstance().isEnd() && e.getRightClicked().getType() == EntityType.VILLAGER && CDS.getInstance().getCdsVillagers().containsKey(e.getRightClicked().getUniqueId()) && CDS.getInstance().getCdsVillagers().get(e.getRightClicked().getUniqueId()) == VillagerType.MERCHANT) {
            villager =(Villager)e.getRightClicked();
            CDSMerchantInventory inv = CDSFunctions.getMerchantInventory(e.getRightClicked().getUniqueId());
            if(e.getPlayer().isSneaking()) {
                if(inv != null)
                    e.getPlayer().openInventory(CDSFunctions.LoadMerchantInventory(inv,e.getPlayer()));
                else {
                    Inventory inventory = Bukkit.createInventory(null,54,e.getRightClicked().getCustomName());
                    e.getPlayer().openInventory(CDSFunctions.InitMerchantInventory(inventory));
                    inv = new CDSMerchantInventory(e.getRightClicked().getUniqueId(),e.getRightClicked().getCustomName());
                    CDS.getInstance().getMerchantInventories().add(inv);
                }
            }
            else {
                if(inv != null)
                    CDSFunctions.OpenMerchantInventory(inv,e.getPlayer(),villager);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player p = (Player)e.getPlayer();
        if(villager != null && CDS.getInstance().getCdsVillagers().containsKey(villager.getUniqueId()) && CDS.getInstance().getCdsVillagers().get(villager.getUniqueId()) == VillagerType.BANK) {
            if(e.getInventory().getType() == InventoryType.MERCHANT)
                e.setCancelled(true);
        }
        if(villager != null && CDS.getInstance().getCdsVillagers().containsKey(villager.getUniqueId()) && CDS.getInstance().getCdsVillagers().get(villager.getUniqueId()) == VillagerType.MERCHANT) {
            if(p.isSneaking() && e.getInventory().getType() == InventoryType.MERCHANT)
                e.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // Meme pb que pr l'ancien le 1er marche pas mais les suivants marchent

        if(CDSFunctions.getMerchantInventory(e.getInventory().getName()) != null) {
            HashMap<Integer, ItemStack> merchantItems = new HashMap<>();

            for(int i = 0;i<e.getInventory().getSize();i++) {
                ItemStack item = e.getInventory().getItem(i);
                if(item != null) {
                    if((i >= 10 && i <= 16) || (i >= 19 && i <= 25) || (i >= 37 && i<= 43))
                        merchantItems.put(i,item);
                }
            }

            CDSMerchantInventory inv = CDSFunctions.getMerchantInventory(e.getInventory().getName());
            inv.setItems(merchantItems);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getName().equals("Banque")) {
            e.setCancelled(true);
            CDSPlayer player = CDSFunctions.getCDSPlayerById(e.getWhoClicked().getUniqueId());
            ItemStack clickedItem = e.getCurrentItem();

            if(player != null && clickedItem != null) {
                if(clickedItem.getItemMeta().getDisplayName().contains("Déposer"))
                    BankFunctions.depositEmeralds((Player)e.getWhoClicked(),player,player.getTeam(),clickedItem);
                else if(clickedItem.getItemMeta().getDisplayName().contains("Retirer"))
                    BankFunctions.withdrawEmeralds((Player)e.getWhoClicked(),player,player.getTeam(),clickedItem);
            }
        }
        else {
            if(e.getClickedInventory() != null && CDSFunctions.isMerchantInventory(e.getClickedInventory().getName()) && e.getClickedInventory().getType() == InventoryType.CHEST) {
                if(!(e.getSlot() >= 10 && e.getSlot() <= 16) && !(e.getSlot() >= 19 && e.getSlot() <= 25) && !(e.getSlot() >= 37 && e.getSlot()<= 43))
                    e.setCancelled(true);
            }
        }
    }
}
