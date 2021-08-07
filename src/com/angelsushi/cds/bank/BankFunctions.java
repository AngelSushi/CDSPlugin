package com.angelsushi.cds.bank;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.bank.event.CDSBankDepositEvent;
import com.angelsushi.cds.bank.event.CDSBankWithdrawEvent;
import com.angelsushi.cds.error.ErrorFunctions;
import com.angelsushi.cds.error.ErrorType;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.Message;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class BankFunctions {

    public static void depositEmeralds(Player p,CDSPlayer player, Team team,ItemStack item) {
        if(player.hasTeam()) {
            int amplifier = 1;

            if(item.getType().getId() == 133)
                amplifier = 9;

            if(player.hasEnoughInInventory(item,item.getAmount(),p.getInventory())) {

                if(team.isAccountSet())
                    team.setAccount(team.getAccount() + item.getAmount() * amplifier);
                else {
                    team.setAccountSet(true);
                    team.setAccount(item.getAmount() * amplifier);
                }

                p.getInventory().removeItem(new ItemStack(item.getType(),item.getAmount()));

                CDSBankDepositEvent depositEvent = new CDSBankDepositEvent((Player)p,item.getAmount() * amplifier,player.getTeam(),Message.ADD_MONEY_BANK);
                Bukkit.getServer().getPluginManager().callEvent(depositEvent);

                p.sendMessage(depositEvent.getMessage());
            }
            else
                ErrorFunctions.error(p,ErrorType.NOT_ENOUGH_EMERALDS);
        }
        else
            ErrorFunctions.error(p, ErrorType.NO_PLAYER_TEAM);
    }

    public static void withdrawEmeralds(Player p,CDSPlayer player,Team team,ItemStack item) {
        if(player.hasTeam()) {
            int amplifier = 1;

            if(item.getType().getId() == 133)
                amplifier = 9;

            if(team.isAccountSet() && team.getAccount() >= item.getAmount() * amplifier) {
                p.getInventory().addItem(new ItemStack(item.getType(),item.getAmount()));
                team.setAccount(team.getAccount() - item.getAmount() * amplifier);

                CDSBankWithdrawEvent withdrawEvent = new CDSBankWithdrawEvent(p,item.getAmount() * amplifier,player.getTeam(),Message.WITHDRAW_MONEY);
                Bukkit.getServer().getPluginManager().callEvent(withdrawEvent);

                p.sendMessage(withdrawEvent.getMessage());
            }
            else // Pas assez d'émeraudes dans sa banque
                ErrorFunctions.error(p,ErrorType.NOT_ENOUGH_EMERALDS);
        }
        else
            ErrorFunctions.error(p,ErrorType.NO_PLAYER_TEAM);
    }

    public static void changeBankSign(SignChangeEvent e) {
        if(!e.getLine(1).equalsIgnoreCase("deposit") && !e.getLine(1).equalsIgnoreCase("withdraw") &&
                !e.getLine(3).equalsIgnoreCase("emerald") && !e.getLine(3).equalsIgnoreCase("emerald_block")) {
            e.setCancelled(true);
            return;
        }

        int emerald = -1;

        try { emerald = Integer.parseInt(e.getLine(2)); }
        catch(Exception e1) {
            e1.printStackTrace();
            ErrorFunctions.error(e.getPlayer(),ErrorType.CONVERT_INTEGER);
        }

        if(emerald > 0) {
            e.setLine(0,"§cBanque");
            e.setLine(2,"§a" + e.getLine(2));
            e.setLine(3,e.getLine(3).toUpperCase());
            CDS.getInstance().getBankSigns().add((Sign)e.getBlock().getState());
        }
        else
            ErrorFunctions.error(e.getPlayer(),ErrorType.CONVERT_INTEGER);
    }
}
