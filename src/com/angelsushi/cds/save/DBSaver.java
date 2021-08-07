package com.angelsushi.cds.save;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.event.runnable.CDSStartEventRunnable;
import com.angelsushi.cds.event.runnable.EventRunnable;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.Cuboid;
import com.angelsushi.cds.inventory.CDSMerchantInventory;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.manager.CDSColors;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.VillagerType;
import com.angelsushi.cds.save.events.CDSPostLoadEvent;
import com.angelsushi.cds.save.events.CDSPostWriteEvent;
import com.angelsushi.cds.save.events.CDSPreLoadEvent;
import com.angelsushi.cds.save.events.CDSPreWriteEvent;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DBSaver {

    public static Connection connect() {
        String url = "jdbc:mysql://localhost/cds";
        String user = "root";
        String pwd = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver OK");
        }catch(Exception e) {
            e.printStackTrace();
        }

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url,user,pwd);
        }catch(Exception e) {
            e.printStackTrace();
        }

        if(conn != null)
            System.out.println("connexion réussie");

        return conn;
    }

    public static void writeDatas(Connection conn){
        CDSPreWriteEvent preWriteEvent = new CDSPreWriteEvent(CDS.getInstance(),conn);
        Bukkit.getServer().getPluginManager().callEvent(preWriteEvent);

        clearTables(conn);

        for(CDSHouse house : CDS.getInstance().getCdsHouses())
            DBSaver.writeHouse(conn,house);
        for(CDSPlayer player : CDS.getInstance().getCdsPlayers())
            DBSaver.writePlayer(conn,player);
        for(CDSMerchantInventory inventory : CDS.getInstance().getMerchantInventories())
            DBSaver.writeInventory(conn,inventory);
        for(Sign sign : CDS.getInstance().getBankSigns())
            DBSaver.writeBankSigns(conn,sign);
        for(UUID entity_id : CDS.getInstance().getCdsVillagers().keySet())
            DBSaver.writeVillager(conn,entity_id,CDS.getInstance().getCdsVillagers().get(entity_id));
        for(Team team : CDS.getInstance().getTeams())
            DBSaver.writeTeam(conn,team);

        DBSaver.writeEventData(conn);

        CDSPostWriteEvent postWriteEvent = new CDSPostWriteEvent(CDS.getInstance(),conn);
        Bukkit.getServer().getPluginManager().callEvent(postWriteEvent);
    }

    public static void readDatas(Connection conn) {
        CDSPreLoadEvent preLoadEvent = new CDSPreLoadEvent(CDS.getInstance(),conn);
        Bukkit.getServer().getPluginManager().callEvent(preLoadEvent);

        CDS.getInstance().setBankSigns(readBankSigns(conn));
        CDS.getInstance().setCdsVillagers(readCdsVillagers(conn));
        CDS.getInstance().setMerchantInventories(readCDSInventories(conn));
        CDS.getInstance().setCdsHouses(readCDSHouses(conn));
        CDS.getInstance().setTeams(readCDSTeams(conn));
        CDS.getInstance().setCdsPlayers(readCDSPlayers(conn));

        readEventData(conn);

        CDSPostLoadEvent postLoadEvent = new CDSPostLoadEvent(CDS.getInstance(),conn);
        Bukkit.getServer().getPluginManager().callEvent(postLoadEvent);

    }

    private static void clearTables(Connection conn) {
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Houses");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Houses_Owners");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Players");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Inventories");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Inventories_Contents");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_BankSigns");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Villagers");
        DBSaver.executeQuery(conn,"DROP TABLE CDS_Teams");
        DBSaver.executeQuery(conn,"DROP TABLE CDS");

        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Houses (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, house_name VARCHAR(30),world_name VARCHAR(30), lower_x SMALLINT UNSIGNED,lower_y SMALLINT UNSIGNED,lower_z SMALLINT UNSIGNED,upper_x SMALLINT UNSIGNED,upper_y SMALLINT UNSIGNED,upper_z SMALLINT UNSIGNED,buy bool,sign_x SMALLINT UNSIGNED,sign_y SMALLINT UNSIGNED,sign_z SMALLINT UNSIGNED,PRIMARY KEY (id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Houses_Owners (id SMALLINT UNSIGNED NOT NULL,player_id VARCHAR(36),PRIMARY KEY (id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Players (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,player_name VARCHAR(25),player_id VARCHAR(36), house_owner bool,team_name VARCHAR(20), PRIMARY KEY (id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Inventories (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,entity_id VARCHAR(36),inv_name VARCHAR(20), PRIMARY KEY(id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Inventories_Contents (id SMALLINT UNSIGNED NOT NULL,slot TINYINT UNSIGNED,item_data VARCHAR(100), PRIMARY KEY (id));"); // A modifier le 100 si nécessaire
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_BankSigns (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,world_name VARCHAR(30),x SMALLINT UNSIGNED,y SMALLINT UNSIGNED,z SMALLINT UNSIGNED, PRIMARY KEY(id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Villagers (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,entity_id VARCHAR(36),villager_type VARCHAR(8), PRIMARY KEY (id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS_Teams (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,team_name VARCHAR(20),color VARCHAR(9), classement_type VARCHAR(8),account_set bool, account SMALLINT UNSIGNED, PRIMARY KEY(id));");
        DBSaver.createTable(conn,"CREATE TABLE IF NOT EXISTS CDS (day TINYINT UNSIGNED,month TINYINT UNSIGNED,year SMALLINT UNSIGNED,hours TINYINT UNSIGNED,minutes TINYINT UNSIGNED,duration TINYINT UNSIGNED);");

    }

    public static void createTable(Connection conn,String query) {
       try {
           Statement statement = conn.createStatement();
           statement.execute(query);
       }catch(Exception e) {
           e.printStackTrace();
       }
    }

    public static void writeHouse(Connection conn, CDSHouse house) {

        try {
            int id = CDSFunctions.getLastId(conn,"SELECT * FROM CDS_Houses");
            String insert = "INSERT INTO CDS_Houses (house_name,world_name,lower_x,lower_y,lower_z,upper_x,upper_y,upper_z,buy,sign_x,sign_y,sign_z)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

            PreparedStatement preparedStatement = conn.prepareStatement(insert);
            preparedStatement.setString(1, house.getName());
            preparedStatement.setString(2,house.getPlace().getWorldName());
            preparedStatement.setInt(3,house.getPlace().getLowerX());
            preparedStatement.setInt(4,house.getPlace().getLowerY());
            preparedStatement.setInt(5,house.getPlace().getLowerZ());
            preparedStatement.setInt(6,house.getPlace().getUpperX());
            preparedStatement.setInt(7,house.getPlace().getUpperY());
            preparedStatement.setInt(8,house.getPlace().getUpperZ());
            preparedStatement.setBoolean(9,house.isBuy());
            preparedStatement.setInt(10,CDSFunctions.getSignByHouseName(house.getName()).getX());
            preparedStatement.setInt(11,CDSFunctions.getSignByHouseName(house.getName()).getY());
            preparedStatement.setInt(12,CDSFunctions.getSignByHouseName(house.getName()).getZ());

            for(String owner : house.getOwners()) { // A MODIFIER CAR l'id va pas etre bonne en autoincrement
                String ownerQuery = "INSERT INTO CDS_Houses_Owners (id,player_id) VALUES (?,?);";
                PreparedStatement ownerStatement = conn.prepareStatement(ownerQuery);
                ownerStatement.setInt(1,id+1);
                ownerStatement.setString(2,owner);
                ownerStatement.execute();
            }

            preparedStatement.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void writePlayer(Connection conn, CDSPlayer player) {
        try {
            String query = "INSERT INTO CDS_Players (player_name,player_id,house_owner,team_name VALUES (?,?,?,?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,player.getName());
            statement.setString(2,player.getId().toString());
            statement.setBoolean(3,player.isHouseOwner());

            if(player.hasTeam())
                statement.setString(4,player.getTeam().getName());

            statement.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeInventory(Connection conn, CDSMerchantInventory inventory) {
        try {
            int id = CDSFunctions.getLastId(conn,"SELECT * FROM CDS_Inventories");

            String query = "INSERT INTO CDS_Inventories (entity_id,inv_name) VALUES (?,?)";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1,inventory.getEntityId().toString());
            statement.setString(2,inventory.getName());

            for(int slot : inventory.getItems().keySet()) {
                ItemStack item = inventory.getItems().get(slot);

                String itemQuery = "INSERT INTO CDS_Inventories_Contents (id,slot,item_data) VALUES (?,?);";
                PreparedStatement itemStatement = conn.prepareStatement(itemQuery);

                itemStatement.setInt(1,id+1);
                itemStatement.setInt(2,slot);
                itemStatement.setString(3,CDSFunctions.toBase64(item));

                itemStatement.execute();
            }

            statement.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBankSigns(Connection conn, Sign sign) {
        try {
             String query = "INSERT INTO CDS_BankSigns (world_name,x,y,z) VALUES (?,?,?,?);";
             PreparedStatement statement = conn.prepareStatement(query);

             statement.setString(1,sign.getWorld().getName());
             statement.setInt(2,sign.getX());
             statement.setInt(3,sign.getY());
             statement.setInt(4,sign.getZ());

             statement.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeVillager(Connection conn, UUID entity_id, VillagerType type) {
        try {
            String query = "INSERT INTO CDS_Villagers (entity_id,villager_type) VALUES (?,?);";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1,entity_id.toString());
            statement.setString(2,type.name());

            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeEventData(Connection conn) {
        try {
            LocalDateTime date = CDS.getInstance().getBeginDate();
           if(date != null) {
               String query = "INSERT INTO CDS (day,month,year,hours,minutes,duration) VALUES (?,?,?,?,?,?);";
               PreparedStatement statement = conn.prepareStatement(query);

               statement.setInt(1,date.getDayOfMonth());
               statement.setInt(2,date.getMonthValue());
               statement.setInt(3,date.getYear());
               statement.setInt(4,date.getHour());
               statement.setInt(5,date.getMinute());
               statement.setInt(6,CDS.getInstance().getEventDuration());

               statement.execute();
           }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTeam(Connection conn, Team team) {
        try {
            String query = "INSERT INTO CDS_Teams (team_name,color,classement_type,account_set,account) VALUES (?,?,?,?,?);";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1,team.getName());
            statement.setString(2,team.getColor().name());
            statement.setString(3,team.getClassementType().name());
            statement.setBoolean(4,team.isAccountSet());
            statement.setInt(5,team.getAccount());

            statement.execute();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Sign> readBankSigns(Connection conn) {
        ArrayList<Sign> bankSigns = new ArrayList<>();

        try {
            String query = "SELECT * FROM CDS_BankSigns";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);

            if(result.next()) {
                while(result.next()) {
                    String worldName = result.getString("world_name");
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    int z = result.getInt("z");

                    bankSigns.add((Sign)Bukkit.getWorld(worldName).getBlockAt(x,y,z).getState());
                }
            }


        }catch(Exception e) {
            e.printStackTrace();
        }

        return bankSigns;
    }

    public static HashMap<UUID,VillagerType> readCdsVillagers(Connection conn) {
        HashMap<UUID,VillagerType> cdsVillagers = new HashMap<>();

        try {
            String query = "SELECT * FROM CDS_Villagers";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                UUID id = UUID.fromString(result.getString("entity_id"));
                VillagerType type = VillagerType.valueOf(result.getString("villager_type"));

                cdsVillagers.put(id,type);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return cdsVillagers;
    }

    public static ArrayList<CDSMerchantInventory> readCDSInventories(Connection conn) {
        ArrayList<CDSMerchantInventory> inventories = new ArrayList<>();

        try {
            String query = "SELECT * FROM CDS_Inventories";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                UUID entity_id = UUID.fromString(resultSet.getString("entity_id"));
                String inv_name = resultSet.getString("inv_name");
                HashMap<Integer,ItemStack> items = new HashMap<Integer,ItemStack>();

                String itemQuery = "SELECT * FROM CDS_Inventories_Contents WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(itemQuery);
                preparedStatement.setInt(1,resultSet.getInt("id"));

                ResultSet result = preparedStatement.executeQuery();

                while(result.next()) {
                    items.put(result.getInt("slot"),CDSFunctions.fromBase64(result.getString("item_data")));
                }

                CDSMerchantInventory merchantInventory = new CDSMerchantInventory(entity_id,inv_name,items);
                inventories.add(merchantInventory);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return inventories;
    }

    public static ArrayList<CDSHouse> readCDSHouses(Connection conn) {
        ArrayList<CDSHouse> cdsHouses = new ArrayList<>();

        try {
            String request = "SELECT * FROM CDS_Houses";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(request);

            while(result.next()) {
                String house_name = result.getString("house_name");
                String world_name = result.getString("world_name");
                int lower_x = result.getInt("lower_x");
                int lower_y = result.getInt("lower_y");
                int lower_z = result.getInt("lower_z");
                int upper_x = result.getInt("upper_x");
                int upper_y = result.getInt("upper_y");
                int upper_z = result.getInt("upper_z");
                boolean buy = result.getBoolean("buy");
                int sign_x = result.getInt("sign_x");
                int sign_y = result.getInt("sign_y");
                int sign_z = result.getInt("sign_z");
                ArrayList<String> owners = new ArrayList<>();

                String ownerRequest = "SELECT * FROM CDS_Houses_Owners WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(ownerRequest);

                preparedStatement.setInt(1,result.getInt("id"));
                ResultSet ownerResult = preparedStatement.executeQuery();

                while(ownerResult.next()) {
                    owners.add(ownerResult.getString("player_id"));
                }

                Cuboid place = new Cuboid(Bukkit.getWorld(world_name),lower_x,lower_y,lower_z,upper_x,upper_y,upper_z);
                CDSHouse house = new CDSHouse(house_name,place,buy,owners);
                Block sign = Bukkit.getWorld(house_name).getBlockAt(sign_x,sign_y,sign_z);

                CDS.getInstance().getSignHouses().add(sign);

                cdsHouses.add(house);
            }


        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return cdsHouses;
    }

    public static ArrayList<Team> readCDSTeams(Connection conn) {
        ArrayList<Team> cdsTeams = new ArrayList<>();

        try {
            String request = "SELECT * FROM CDS_Teams";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(request);

            while(result.next()) {
                String team_name = result.getString("team_name");
                CDSColors colors = CDSColors.valueOf(result.getString("color"));
                ClassementType type = ClassementType.valueOf(result.getString("classement_type"));
                boolean account_set = result.getBoolean("account_set");
                Integer account = result.getInt("account");

                Team team = new Team(team_name,colors,new ArrayList<>(),type,account_set,account);
                cdsTeams.add(team);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return cdsTeams;
    }

    public static ArrayList<CDSPlayer> readCDSPlayers(Connection conn) {
        ArrayList<CDSPlayer> cdsPlayers = new ArrayList<>();

        try {
            String request = "SELECT * FROM CDS_Players";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(request);

            while(result.next()) {
                String player_name = result.getString("player_name");
                UUID player_id = UUID.fromString(result.getString("player_id"));
                boolean house_owner = result.getBoolean("house_owner");
                Team team =  CDSFunctions.getTeam(result.getString("team_name"));

                CDSPlayer player = new CDSPlayer(player_name,player_id,house_owner,team);
                cdsPlayers.add(player);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return cdsPlayers;
    }

    public static void readEventData(Connection conn) {
        try {

            String request = "SELECT * FROM CDS";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(request);

            if(result.next()) {
                int day = result.getInt("day");
                int month = result.getInt("month");
                int year = result.getInt("year");
                int hours = result.getInt("hours");
                int minutes = result.getInt("minutes");
                int duration = result.getInt("duration");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime date = null;
                // a tester quand heure est sur minuit 00
                if(month < 10 && day < 10)
                    date = LocalDateTime.parse("" + year + "-0" + month + "-0" + day + " " + hours + ":" + minutes, formatter);
                else if(month < 10 && day >= 10)
                    date = LocalDateTime.parse("" + year + "-0" + month + "-" + day + " " + hours + ":" + minutes, formatter);
                else if(month >= 10 && day < 10)
                    date = LocalDateTime.parse("" + year + "-" + month + "-0" + day + " " + hours + ":" + minutes, formatter);
                else
                    date = LocalDateTime.parse("" + year + "-" + month + "-" + day + " " + hours + ":" + minutes, formatter);

                if(date != null) {
                    CDS.getInstance().setBeginDate(date);
                    CDS.getInstance().setEventDuration(duration);
                    CDS.getInstance().setStart(CDSFunctions.checkIfStarted(date));
                    CDS.getInstance().setEnd(CDSFunctions.checkIfEnded(date));

                    if(CDS.getInstance().isStart() && !CDS.getInstance().isEnd()) {
                        EventRunnable eventRunnable = new EventRunnable();
                        eventRunnable.runTaskTimer(CDS.getInstance(),0,20);
                    }
                    else if(!CDS.getInstance().isStart() && !CDS.getInstance().isEnd()) {
                        CDSStartEventRunnable startEventRunnable = new CDSStartEventRunnable();
                        startEventRunnable.runTaskTimer(CDS.getInstance(),0,20);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(Connection conn,String query) {

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.execute();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}
