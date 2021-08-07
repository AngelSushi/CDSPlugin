package com.angelsushi.cds.save;

import com.angelsushi.cds.CDS;
import com.angelsushi.cds.leaderboard.ClassementType;
import com.angelsushi.cds.manager.CDSColors;
import com.angelsushi.cds.manager.CDSFunctions;
import com.angelsushi.cds.manager.CDSPlayer;
import com.angelsushi.cds.manager.VillagerType;
import com.angelsushi.cds.save.events.CDSPostLoadEvent;
import com.angelsushi.cds.save.events.CDSPostWriteEvent;
import com.angelsushi.cds.save.events.CDSPreLoadEvent;
import com.angelsushi.cds.save.events.CDSPreWriteEvent;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.event.runnable.CDSStartEventRunnable;
import com.angelsushi.cds.event.runnable.EventRunnable;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.Cuboid;
import com.angelsushi.cds.inventory.CDSMerchantInventory;
import com.angelsushi.cds.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileSaver {

    private static YamlConfiguration villagersConfig,playersConfig,merchantConfig,houseConfig,bankConfig,eventConfig,scoreboardConfig,teamConfig;
    private static File villagerFile,playerFile,merchantFile,houseFile,bankFile,eventFile,scoreboardFile,teamFile;

    public static void initFiles(CDS instance) {

        CDSPreLoadEvent preLoadEvent = new CDSPreLoadEvent(CDS.getInstance(),null);
        Bukkit.getServer().getPluginManager().callEvent(preLoadEvent);

        villagerFile = new File(instance.getDataFolder() + "/villagers.yml");
        playerFile = new File(instance.getDataFolder() + "/players.yml");
        merchantFile = new File(instance.getDataFolder() + "/merchants.yml");
        houseFile = new File(instance.getDataFolder() + "/houses.yml");
        bankFile = new File(instance.getDataFolder() + "/banks.yml");
        eventFile = new File(instance.getDataFolder() + "/event.yml");
        teamFile = new File(instance.getDataFolder() + "/teams.yml");

        villagersConfig = YamlConfiguration.loadConfiguration(villagerFile);
        playersConfig = YamlConfiguration.loadConfiguration(playerFile);
        merchantConfig = YamlConfiguration.loadConfiguration(merchantFile);
        houseConfig = YamlConfiguration.loadConfiguration(houseFile);
        bankConfig = YamlConfiguration.loadConfiguration(bankFile);
        eventConfig = YamlConfiguration.loadConfiguration(eventFile);
        teamConfig = YamlConfiguration.loadConfiguration(teamFile);

        CDS.getInstance().setCdsVillagers(readVillagers(villagersConfig));
        CDS.getInstance().setTeams(readTeams(teamConfig));
        CDS.getInstance().setCdsPlayers(readPlayers(playersConfig));
        CDS.getInstance().setMerchantInventories(readInventories(merchantConfig));
        CDS.getInstance().setCdsHouses(readHouses(houseConfig));
        CDS.getInstance().setBankSigns(readBankSigns(bankConfig));
        readEventData(eventConfig);


        CDSPostLoadEvent postLoadEvent = new CDSPostLoadEvent(CDS.getInstance(),null);
        Bukkit.getServer().getPluginManager().callEvent(postLoadEvent);
    }

    public static void initScoreboard(CDS instance) {
        scoreboardFile = new File(instance.getDataFolder() + "/scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        CDS.getInstance().setScoreboard(FileSaver.readScoreboard(scoreboardConfig));
    }

    public static void writeData() {

        CDSPreWriteEvent preWriteEvent = new CDSPreWriteEvent(CDS.getInstance(),null);
        Bukkit.getServer().getPluginManager().callEvent(preWriteEvent);

        writeVillagers(villagersConfig,villagerFile);
        writePlayers(playersConfig,playerFile);
        writeInventories(merchantConfig,merchantFile);
        writeHouses(houseConfig,houseFile);
        writeBankSigns(bankConfig,bankFile);
        writeEventData(eventConfig,eventFile);
        writTeam(teamConfig,teamFile);

        CDSPostWriteEvent postWriteEvent = new CDSPostWriteEvent(CDS.getInstance(),null);
        Bukkit.getServer().getPluginManager().callEvent(postWriteEvent);
    }

    public static ArrayList<CDSHouse> readHouses(YamlConfiguration config) {
        ArrayList<CDSHouse> cdsHouses = new ArrayList<>();
        if(config.getConfigurationSection("cdsHouses") != null) {
            for(String id : config.getConfigurationSection("cdsHouses").getKeys(false)) {
                String key = "cdsHouses." + id;

                String name = config.getString(key + ".name");
                String worldName = config.getString(key + ".worldName");

                int lowerX = config.getInt(key + ".lowerX");
                int lowerY = config.getInt(key + ".lowerY");
                int lowerZ = config.getInt(key + ".lowerZ");
                int upperX = config.getInt(key + ".upperX");
                int upperY = config.getInt(key + ".upperY");
                int upperZ = config.getInt(key + ".upperZ");
                boolean buy = config.getBoolean(key + ".buy");
                ArrayList<String> owners = (ArrayList<String>) config.getList(key + ".owners");

                Cuboid cuboid = new Cuboid(new Location(Bukkit.getWorld(worldName),lowerX,lowerY,lowerZ), new Location(Bukkit.getWorld(worldName),upperX,upperY,upperZ));

                Block sign = Bukkit.getWorld(worldName).getBlockAt(config.getInt(key + ".sign.x"),config.getInt(key + ".sign.y"),config.getInt(key + ".sign.z"));
                CDS.getInstance().getSignHouses().add(sign);

                CDSHouse house = new CDSHouse(name,cuboid,buy,owners);
                cdsHouses.add(house);
            }
        }

        return cdsHouses;
    }

    public static ArrayList<CDSPlayer> readPlayers(YamlConfiguration config) {
        ArrayList<CDSPlayer> cdsPlayers = new ArrayList<>();

        if(config.getConfigurationSection("cdsPlayers") != null) {
            for(String id : config.getConfigurationSection("cdsPlayers").getKeys(false)) {
                UUID uuid = UUID.fromString(config.getString("cdsPlayers." + id + ".id"));
                String name = config.getString("cdsPlayers." + id + ".name");
                boolean houseOwner = config.getBoolean("cdsPlayers." + id + ".houseOwner");
                Team team = CDSFunctions.getTeam(config.getString("cdsPlayers." + id + ".teamName"));
                CDSPlayer player = new CDSPlayer(name,uuid,houseOwner,team);
                cdsPlayers.add(player);
            }
        }

        return cdsPlayers;
    }

    public static HashMap<UUID, VillagerType> readVillagers(YamlConfiguration config) {
        HashMap<UUID,VillagerType> cdsPlayers = new HashMap<>();

        if(config.getConfigurationSection("cdsVillagers") != null) {
            for(String id : config.getConfigurationSection("cdsVillagers").getKeys(false))
                cdsPlayers.put(UUID.fromString(config.getString("cdsVillagers." + id + ".id")), VillagerType.valueOf(config.getString("cdsVillagers." + id + ".type")));
        }

        return cdsPlayers;
    }

    public static ArrayList<CDSMerchantInventory> readInventories(YamlConfiguration config) {
        ArrayList<CDSMerchantInventory> cdsInventories = new ArrayList<>();

        if(config.getConfigurationSection("cdsInventories") != null) {
            HashMap<Integer,ItemStack> items = new HashMap<>();
            for(String id : config.getConfigurationSection("cdsInventories").getKeys(false)) {
                UUID uuid = UUID.fromString(config.getString("cdsInventories." + id + ".id"));
                String name = config.getString("cdsInventories." + id + ".name");
                if(config.getConfigurationSection("cdsInventories." + id + ".items") != null) {
                    for(String itemId : config.getConfigurationSection("cdsInventories." + id + ".items").getKeys(false)) {
                        int slot = config.getInt("cdsInventories." + id + ".items" + itemId + ".slot");
                        ItemStack item = config.getItemStack("cdsInventories." + id + ".items" + itemId + ".item");
                        items.put(slot,item);
                    }
                }

                CDSMerchantInventory inv = new CDSMerchantInventory(uuid,name,items);
                cdsInventories.add(inv);
            }
        }

        return cdsInventories;
    }

    public static ArrayList<Sign> readBankSigns(YamlConfiguration configuration) {
        ArrayList<Sign> bankSigns = new ArrayList<>();

        if(configuration.getConfigurationSection("banks") != null) {
            for(String id : configuration.getConfigurationSection("banks").getKeys(false)) {
                String worldName = configuration.getString("banks." + id + ".worldName");
                int x = configuration.getInt("banks." + id + ".x");
                int y = configuration.getInt("banks." + id + ".y");
                int z = configuration.getInt("banks." + id + ".z");

                bankSigns.add((Sign)Bukkit.getWorld(worldName).getBlockAt(x,y,z).getState());
            }
        }

        return bankSigns;
    }

    public static void readEventData(YamlConfiguration config) {

        if(config.getConfigurationSection("event.date") != null) {
            int day = config.getInt("event.date.day");
            int month = config.getInt("event.date.month");
            int year = config.getInt("event.date.year");
            int hours = config.getInt("event.date.hours");
            int minutes = config.getInt("event.date.minutes");

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
                CDS.getInstance().setEventDuration(config.getInt("event.duration"));
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

    public static CDSScoreboard readScoreboard(YamlConfiguration config) {
        CDSScoreboard scoreboard = null;
        if(config.getConfigurationSection("scoreboard") != null) {
            scoreboard = new CDSScoreboard(config.getString("scoreboard.title"));

            for(String line : config.getConfigurationSection("scoreboard").getKeys(false)) {
                if(!line.equalsIgnoreCase("title") && line.startsWith("line"))
                    scoreboard.addLine(config.getString("scoreboard." + line));
            }
        }

        return scoreboard;
    }

    private static ArrayList<Team> readTeams(YamlConfiguration config) {
        ArrayList<Team> teams = new ArrayList<>();

        if(config.getConfigurationSection("teams") != null) {
            for(String id : config.getConfigurationSection("teams").getKeys(false)) {
                String key = "teams." + id;
                String teamName = config.getString(key + ".name");
                CDSColors color = CDSColors.valueOf(config.getString(key + ".color"));
                ArrayList<String> players = (ArrayList<String>) config.getList(key + ".players");
                ClassementType classementType = ClassementType.valueOf(config.getString(key + ".classementType"));
                boolean accountSet = config.getBoolean(key + ".accountSet");
                Integer account = config.getInt(key + ".account");

                Team team = new Team(teamName,color,players,classementType,accountSet,account);
                teams.add(team);
            }
        }

        return teams;
    }

    public static void writeBankSigns(YamlConfiguration config,File file) {
        createFile(file);
        int index = 0;

        for(Sign sign : CDS.getInstance().getBankSigns()) {
            config.set("banks." + index + ".worldName",sign.getWorld().getName());
            config.set("banks." + index + ".x",sign.getX());
            config.set("banks." + index + ".y",sign.getY());
            config.set("banks." + index + ".z",sign.getZ());
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeVillagers(YamlConfiguration config, File file) {
        createFile(file);
        int index = 0;

        for(UUID uuid : CDS.getInstance().getCdsVillagers().keySet()) {
            config.set("cdsVillagers." + index + ".id",uuid.toString());
            config.set("cdsVillagers." + index + ".type",CDS.getInstance().getCdsVillagers().get(uuid).toString());
            index++;
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writePlayers(YamlConfiguration config, File file) {
        createFile(file);
        int index = 0;

        for(CDSPlayer player : CDS.getInstance().getCdsPlayers()) {
            config.set("cdsPlayers." + index + ".name",player.getName());
            config.set("cdsPlayers." + index + ".id",player.getId().toString());
            config.set("cdsPlayers." + index + ".houseOwner",player.isHouseOwner());
            if(player.hasTeam())
                config.set("cdsPlayers." + index + ".teamName",player.getTeam().getName());
            index++;
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeInventories(YamlConfiguration config, File file) {
        createFile(file);
        int index = 0;

        for(CDSMerchantInventory inv : CDS.getInstance().getMerchantInventories()) {
            config.set("cdsInventories." + index + ".id",inv.getEntityId().toString());
            config.set("cdsInventories." + index + ".name",inv.getName());
            for(int i = 0;i<inv.getItems().size();i++) {
                config.set("cdsInventories." + index + ".items." + i + ".slot",inv.getItems().keySet().toArray()[i]);
                int result = (int)inv.getItems().keySet().toArray()[i];
                config.set("cdsInventories." + index + ".items." + i + ".item",inv.getItems().get(result));
            }
            index++;
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeHouses(YamlConfiguration config, File file) {
        createFile(file);
        int index = 0;

        for(CDSHouse house : CDS.getInstance().getCdsHouses()) {
            config.set("cdsHouses." + index + ".name",house.getName());
            config.set("cdsHouses." + index + ".worldName",house.getPlace().getWorld().getName());
            config.set("cdsHouses." + index + ".lowerX",house.getPlace().getLowerX());
            config.set("cdsHouses." + index + ".lowerY",house.getPlace().getLowerY());
            config.set("cdsHouses." + index + ".lowerZ",house.getPlace().getLowerZ());
            config.set("cdsHouses." + index + ".upperX",house.getPlace().getUpperX());
            config.set("cdsHouses." + index + ".upperY",house.getPlace().getUpperY());
            config.set("cdsHouses." + index + ".upperZ",house.getPlace().getUpperZ());
            config.set("cdsHouses." + index + ".buy",house.isBuy());
            config.set("cdsHouses." + index + ".owners",house.getOwners());
            Block sign = CDSFunctions.getSignByHouseName(house.getName());
            if(sign != null) {
                config.set("cdsHouses." + index + ".sign.x",sign.getLocation().getBlockX());
                config.set("cdsHouses." + index + ".sign.y",sign.getLocation().getBlockY());
                config.set("cdsHouses." + index + ".sign.z",sign.getLocation().getBlockZ());
            }
            index++;
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeEventData(YamlConfiguration config, File file) {
        createFile(file);

        if(CDS.getInstance().getBeginDate() != null) {
            LocalDateTime date = CDS.getInstance().getBeginDate();
            config.set("event.date.day",date.getDayOfMonth());
            config.set("event.date.month",date.getMonthValue());
            config.set("event.date.year",date.getYear());
            config.set("event.date.hours",date.getHour());
            config.set("event.date.minutes",date.getMinute());
            config.set("event.duration",CDS.getInstance().getEventDuration());
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writTeam(YamlConfiguration config, File file) {
        createFile(file);
        int index = 0;

        for(Team team : CDS.getInstance().getTeams()) {
            config.set("teams." + index + ".name",team.getName());
            config.set("teams." + index + ".color",team.getColor().name());
            config.set("teams." + index + ".players",team.getPlayers());
            config.set("teams." + index + ".classementType",team.getClassementType());
            config.set("teams." + index + ".accountSet",team.isAccountSet());
            config.set("teams." + index + ".account",team.getAccount());

            index++;
        }

        try {
            config.save(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void createFile(File file) {
        try {
            file.delete();
            file.createNewFile();
            file.getParentFile().mkdirs();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
