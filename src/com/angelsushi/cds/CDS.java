package com.angelsushi.cds;

import com.angelsushi.cds.bank.BankListener;
import com.angelsushi.cds.bank.command.CDSBankCommand;
import com.angelsushi.cds.event.commands.CDSEventCommand;
import com.angelsushi.cds.event.commands.CDSManageCommand;
import com.angelsushi.cds.house.CDSHouse;
import com.angelsushi.cds.house.CDSOffer;
import com.angelsushi.cds.house.HouseListener;
import com.angelsushi.cds.house.commands.CDSHouseCommand;
import com.angelsushi.cds.merchants.commands.CDSShopCommand;
import com.angelsushi.cds.inventory.CDSMerchantInventory;
import com.angelsushi.cds.inventory.InventoryListener;
import com.angelsushi.cds.leaderboard.CDSLeaderboard;
import com.angelsushi.cds.leaderboard.LeaderboardListener;
import com.angelsushi.cds.leaderboard.command.CDSLeaderboardCommand;
import com.angelsushi.cds.manager.*;
import com.angelsushi.cds.save.DBSaver;
import com.angelsushi.cds.save.FileSaver;
import com.angelsushi.cds.scoreboard.CDSScoreboard;
import com.angelsushi.cds.scoreboard.ScoreboardListener;
import com.angelsushi.cds.team.Team;
import com.angelsushi.cds.team.TeamListener;
import com.angelsushi.cds.team.commands.CDSTeamCommand;
import com.angelsushi.cds.team.commands.CDSTeamCreateCommand;
import fr.watch54.display.managers.HologramManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CDS extends JavaPlugin {

    @Getter
    private static CDS instance;
    @Getter @Setter
    private HashMap<UUID, VillagerType> cdsVillagers = new HashMap<>();
    @Getter @Setter
    private ArrayList<CDSPlayer> cdsPlayers = new ArrayList<>();
    @Getter @Setter
    private ArrayList<CDSMerchantInventory> merchantInventories = new ArrayList<>();
    @Getter @Setter
    private ArrayList<CDSHouse> cdsHouses = new ArrayList<>();
    @Getter
    private ArrayList<Block> signHouses = new ArrayList<>();
    @Getter
    private ArrayList<CDSOffer> cdsOffers = new ArrayList<>();
    @Getter @Setter
    private ArrayList<Team> teams = new ArrayList<Team>();
    @Getter
    private ArrayList<CDSLeaderboard> leaderboards = new ArrayList<>();
    @Getter
    private HologramManager hologramManager;
    @Getter @Setter
    private LocalDateTime beginDate;
    @Getter @Setter
    private ArrayList<Sign> bankSigns = new ArrayList<Sign>();
    @Getter @Setter
    private int eventDuration;
    @Setter @Getter
    private boolean saveFile = false,start,end;

    @Getter @Setter
    private CDSScoreboard scoreboard;
    @Getter @Setter
    private Location houseFirst,houseSecond;

    private Connection conn;

    @Override
    public void onEnable() {
        instance = this;
        this.hologramManager = new HologramManager(this);

        InitCommands();

        Bukkit.getServer().getPluginManager().registerEvents(new CDSListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new BankListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new TeamListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new HouseListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(),this);

        if(saveFile)
            FileSaver.initFiles(instance);
        else {
            conn = DBSaver.connect();
            DBSaver.readDatas(conn);
        }

        FileSaver.initScoreboard(instance);

    }

    @Override
    public void onDisable() {
        hologramManager.clear();

        if(saveFile)
            FileSaver.writeData();
       else  // Connexion bdd
            DBSaver.writeDatas(conn);
    }

    private void InitCommands() {
        CDSCommand cdsCommand = new CDSCommand() {
            @Override
            public boolean runCommand(CommandSender sender, Command rootCommand, String label, String[] args) { return false; }};

        cdsCommand.registerSubCommand("bank",new CDSBankCommand());
        cdsCommand.registerSubCommand("shop",new CDSShopCommand());
        cdsCommand.registerSubCommand("house",new CDSHouseCommand());

        cdsCommand.registerSubCommand("leaderboard",new CDSLeaderboardCommand());

        CDSTeamCommand teamCommand = new CDSTeamCommand();
        teamCommand.registerSubCommand("create",new CDSTeamCreateCommand());
        cdsCommand.registerSubCommand("team",teamCommand);

        CDSEventCommand eventCommand = new CDSEventCommand();
        eventCommand.registerSubCommand("date",new CDSManageCommand());

        cdsCommand.registerSubCommand("event",eventCommand);

        getCommand("cds").setExecutor(cdsCommand);
    }


}
