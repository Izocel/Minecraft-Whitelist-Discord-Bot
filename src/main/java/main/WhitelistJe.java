package main;

import java.net.http.WebSocket.Listener;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import bukkit.BukkitManager;
import configs.ConfigManager;
import dao.DaoManager;
import discord.DiscordManager;
import functions.GuildManager;
import helpers.Helper;
import models.User;



public final class WhitelistJe extends JavaPlugin implements Listener {

    private Logger logger;
    public WhitelistJe instance;
    private BukkitManager bukkitManager;
    private GuildManager guildManager;
    private ConfigManager configManager;
    private DiscordManager discordManager;
    private DaoManager daoManager;

    private JSONArray players = new JSONArray();
    private JSONArray playersAllowed = new JSONArray();

    public String figlet ="""
        
         __       __  __        __    __                __  __              __               _____           
        /  |  _  /  |/  |      /  |  /  |              /  |/  |            /  |             /     |          
        $$ | / \\ $$ |$$ |____  $$/  _$$ |_     ______  $$ |$$/   _______  _$$ |_            $$$$$ |  ______  
        $$ |/$  \\$$ |$$      \\ /  |/ $$   |   /      \\ $$ |/  | /       |/ $$   |  ______      $$ | /      \\ 
        $$ /$$$  $$ |$$$$$$$  |$$ |$$$$$$/   /$$$$$$  |$$ |$$ |/$$$$$$$/ $$$$$$/  /      |__   $$ |/$$$$$$  |
        $$ $$/$$ $$ |$$ |  $$ |$$ |  $$ | __ $$    $$ |$$ |$$ |$$      \\   $$ | __$$$$$$//  |  $$ |$$    $$ |
        $$$$/  $$$$ |$$ |  $$ |$$ |  $$ |/  |$$$$$$$$/ $$ |$$ | $$$$$$  |  $$ |/  |      $$ \\__$$ |$$$$$$$$/ 
        $$$/    $$$ |$$ |  $$ |$$ |  $$  $$/ $$       |$$ |$$ |/     $$/   $$  $$/       $$    $$/ $$       |
        $$/      $$/ $$/   $$/ $$/    $$$$/   $$$$$$$/ $$/ $$/ $$$$$$$/     $$$$/         $$$$$$/   $$$$$$$/ 
                 ______     __   __   _____        ______   ______     ______       __     ______     ______     ______   ______   
                /\\  == \\   /\\ \\ / /  /\\  __-.     /\\  == \\ /\\  == \\   /\\  __ \\     /\\ \\   /\\  ___\\   /\\  ___\\   /\\__  _\\ /\\  ___\\  
                \\ \\  __<   \\ \\ \\'/   \\ \\ \\/\\ \\    \\ \\  _-/ \\ \\  __<   \\ \\ \\/\\ \\   _\\_\\ \\  \\ \\  __\\   \\ \\ \\____  \\/_/\\ \\/ \\ \\___  \\ 
                 \\ \\_\\ \\_\\  \\ \\__|    \\ \\____-     \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_____\\ /\\_____\\  \\ \\_____\\  \\ \\_____\\    \\ \\_\\  \\/\\_____\\
                  \\/_/ /_/   \\/_/      \\/____/      \\/_/     \\/_/ /_/   \\/_____/ \\/_____/   \\/_____/   \\/_____/     \\/_/   \\/_____/

    """;


    public WhitelistJe() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getName());
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        daoManager = new DaoManager(DataSourceFactory.getMySQLDataSource(configManager));

        discordManager = new DiscordManager(this);
        guildManager = new GuildManager(discordManager.getGuild());

        bukkitManager = new BukkitManager(this);

        updateAllPlayers();
        updateAllowedPlayers();

        Logger.getLogger("WhiteList-Je").info(this.figlet);

        Timestamp start = Helper.getTimestamp();
        for (int i = 0; i < 10; i++) {
            User newUser = daoManager.getUsersDao().findUser(1);
            newUser.setMcName("pseudo");
            newUser.setDiscordTag("discordTag#" + i);
            newUser.executeOrder66(null);
            Integer id = newUser.save(daoManager.getUsersDao());
            this.logger.info(id == null ? "null" : id.toString() + " for Q# : " + i);
        }
        Timestamp stop = Helper.getTimestamp();

        long diff = stop.getTime()-start.getTime();
        this.logger.info("" + diff);


        this.logger.info(this.players.toString());
    }

    @Override
    public void onDisable() {
       this.discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public DaoManager getDaoManager() {
        return this.daoManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public GuildManager getGuildManager() {
        return this.guildManager;
    }

    public BukkitManager gtBukkitManager() {
        return this.bukkitManager;
    }

    public JSONArray updateAllPlayers() {
        this.players = daoManager.getUsersDao().findAll();
        return this.players;
    }

    public JSONArray updateAllowedPlayers() {
        this.playersAllowed = daoManager.getUsersDao().findAllowed();
        return this.playersAllowed;
    }

    public void updatePlayerUUID(Integer id, UUID mc_uuid) {
        daoManager.getUsersDao().setPlayerUUID(id, mc_uuid);
    }

    public Integer playerIsAllowed(UUID mc_uuid) {
        this.updateAllowedPlayers();
        Integer allowedUserId = -1;

        for (Object object : this.playersAllowed) {
            final JSONObject player = (JSONObject) object;

            final String uuidReccord = player.getString("mc_uuid");
            if(uuidReccord.equals(mc_uuid.toString())) {
                allowedUserId = player.getInt("id");
                break;
            }
        }

        return allowedUserId;
    }
}
