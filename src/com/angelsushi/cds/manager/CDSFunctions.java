package com.angelsushi.cds.manager;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.CDSOffer;
import com.angelsushi.cds.inventory.CDSMerchantInventory;
import com.angelsushi.cds.leaderboard.CDSLeaderboard;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.team.Team;
import io.netty.handler.codec.base64.Base64Encoder;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class CDSFunctions {

    public static boolean checkIfStarted(LocalDateTime beginDate) {
        LocalDateTime now = LocalDateTime.now();

        if(now.getMonthValue() == beginDate.getMonthValue()) {
            if(now.getDayOfMonth() > beginDate.getDayOfMonth())
                return true;
            else if(beginDate.getDayOfMonth() == now.getDayOfMonth()) {
                if(now.getHour() > beginDate.getHour())
                    return true;
                else if(now.getHour() == beginDate.getHour()) {
                    return now.getMinute() >= beginDate.getMinute();
                }
            }
        }
        else if(now.getMonthValue() > beginDate.getMonthValue())
            return true;
        else {
            if(now.getYear() > beginDate.getYear())
                return true;
        }

        return false;
    }

    public static boolean checkIfEnded(LocalDateTime beginDate) {
        LocalDateTime endDate = beginDate.plusDays(CDS.getInstance().getEventDuration());
        LocalDateTime now = LocalDateTime.now();

        if(now.getYear() == endDate.getYear()) {
            if(now.getMonthValue() == endDate.getMonthValue()) {
                if(now.getDayOfMonth() == endDate.getDayOfMonth()) {
                    if(now.getHour() == endDate.getHour())
                        return now.getMinute() >= endDate.getMinute();
                    else if(now.getHour() > endDate.getHour())
                        return true;
                }
                else if(now.getDayOfMonth() > endDate.getDayOfMonth())
                    return true;
            }
            else if(now.getMonthValue() > endDate.getMonthValue())
                return true;
        }
        else if(now.getYear() > endDate.getYear())
            return true;

        return false;
    }


    public static CDSPlayer getCDSPlayerById(UUID id) {
        for(CDSPlayer player : CDS.getInstance().getCdsPlayers()) {
            if(player.getId().toString().equals(id.toString()))
                return player;
        }

        return null;
    }

    public static CDSLeaderboard getLeaderboardByType(ClassementType type) {
        for(CDSLeaderboard leaderboard : CDS.getInstance().getLeaderboards()) {
            if(leaderboard.getType() == type)
                return leaderboard;
        }

        return null;
    }

    public static CDSHouse isInHouse(Location loc) {
        for(CDSHouse house : CDS.getInstance().getCdsHouses()) {
            if(house.getPlace().contains(loc))
                return house;
        }

        return null;
    }

    public static boolean isBankSign(Sign sign) {
        for(Sign s : CDS.getInstance().getBankSigns()) {
            if(s.getWorld().getName().equalsIgnoreCase(sign.getWorld().getName())) {
                if(s.getX() == sign.getX() && s.getY() == sign.getY() && s.getZ() == sign.getZ())
                    return true;
            }
        }

        return false;
    }

    public static boolean existTeam(String name) {
        for(Team team : CDS.getInstance().getTeams()) {
            if(team.getName().equals(name))
                return true;
        }

        return false;
    }

    public static Team getTeam(String name) {
        for(Team team : CDS.getInstance().getTeams()) {
            if(team.getName().equals(name))
                return team;
        }

        return null;
    }

    public static CDSMerchantInventory getMerchantInventory(UUID id) {
        for(CDSMerchantInventory inv : CDS.getInstance().getMerchantInventories()) {
            if(inv.getEntityId().toString().equals(id.toString()))
                return inv;
        }

        return null;
    }

    public static CDSHouse getHouseByName(String name) {
        for(CDSHouse house : CDS.getInstance().getCdsHouses()) {
            if(house.getName().equals(name))
                return house;
        }
        return null;
    }

    public static CDSOffer getOffer(String houseName, CDSPlayer sender) {
        for(CDSOffer offer : CDS.getInstance().getCdsOffers()) {
            if(offer.getHouse().getName().equals(houseName) && offer.getSender().getUniqueId().toString().equals(sender.getId().toString()))
                return offer;
        }

        return null;
    }

    public static Block getSignByHouseName(String name) {
        for(Block signBlock : CDS.getInstance().getSignHouses()) {
            Sign sign = (Sign) signBlock.getState();
            if(sign.getLine(0).substring(2).equals(name))
                return signBlock;
        }

        return null;
    }

    public static boolean isHouseSign(Block signBlock) {
        for(org.bukkit.block.Block signHouse : CDS.getInstance().getSignHouses()) {
            Location signLoc = signBlock.getLocation();
            Location signHouseLoc = signHouse.getLocation();

            if(signLoc.getWorld().getName().equals(signHouseLoc.getWorld().getName()) && signLoc.getBlockX() == signHouseLoc.getBlockX() && signLoc.getBlockY() == signHouseLoc.getBlockY() && signLoc.getBlockZ() == signHouseLoc.getBlockZ())
                return true;

        }

        return false;
    }

    public static CDSMerchantInventory getMerchantInventory(String name) {
        for(CDSMerchantInventory inv : CDS.getInstance().getMerchantInventories()) {
            if(inv.getName().equals(name))
                return inv;
        }

        return null;
    }

    public static Inventory InitMerchantInventory(Inventory inv) {
        for(int i = 0;i<9;i++)  {
            inv.setItem(i,new ItemStack(160));
            inv.setItem(45 + i,new ItemStack(160));
            inv.setItem(27 + i,new ItemStack(160));
        }

        for(int i = 1;i<5;i++)
            inv.setItem(i * 9,new ItemStack(160));
        for(int i = 0;i<4;i++)
            inv.setItem(i * 9 + 17,new ItemStack(160));

        return inv;
    }

    public static Inventory LoadMerchantInventory(CDSMerchantInventory inv, Player p) {
        Inventory inventory = Bukkit.createInventory(null,54, inv.getName());
        InitMerchantInventory(inventory);

        for(int i : inv.getItems().keySet())
            inventory.setItem(i,inv.getItems().get(i));

        return inventory;
    }

    public static void OpenMerchantInventory(CDSMerchantInventory inv,Player p,Villager villager) {
        MerchantRecipeList recipes = new MerchantRecipeList();

        for(int i = 0;i<7;i++) {
            ItemStack sellItemOne = inv.getItems().get(10 + i);
            ItemStack sellItemTwo = inv.getItems().get(19 + i);
            ItemStack buyItem = inv.getItems().get(37 + i);

            MerchantRecipe recipe = null;

            if(sellItemOne != null && buyItem != null && sellItemOne == null)
                recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(sellItemOne),CraftItemStack.asNMSCopy(buyItem));
            else if(sellItemOne != null && buyItem != null && sellItemOne != null)
                recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(sellItemOne),CraftItemStack.asNMSCopy(sellItemTwo),CraftItemStack.asNMSCopy(buyItem));
            if(recipe != null)
                recipes.add(recipe);

        }

        ((CraftPlayer) p).getHandle().openTrade(new IMerchant() {

            @Override
            public EntityHuman v_() {
                return ((CraftPlayer) p).getHandle();
            }

            @Override
            public MerchantRecipeList getOffers(EntityHuman entityHuman) {
                return recipes;
            }

            @Override
            public IChatBaseComponent getScoreboardDisplayName() {
                return IChatBaseComponent.ChatSerializer.a(villager.getCustomName());
            }

            @Override
            public void a(MerchantRecipe merchantRecipe) {}

            @Override
            public void a_(net.minecraft.server.v1_8_R3.ItemStack itemStack) {}

            @Override
            public void a_(EntityHuman entityHuman) {}

        });

    }

    public static boolean isMerchantInventory(String name) {
        for(CDSMerchantInventory inventory : CDS.getInstance().getMerchantInventories()) {
            if(inventory.getName().equals(name))
                return true;
        }
        return false;
    }

    public static String toBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();

            return Base64.encodeBase64String(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack)dataInput.readObject();
            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static int getLastId(Connection conn, String query) {
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            int id = -1;

            if(result.next()) {
                while(result.next())
                    id = result.getInt("id");
            }

            return id;

        }catch(Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
