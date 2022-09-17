package main;

import java.net.http.WebSocket.Listener;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import bukkit.BukkitManager;
import configs.ConfigManager;
import dao.UsersDao;
import discord.DiscordManager;
import functions.GuildManager;



public final class WhitelistJe extends JavaPlugin implements Listener {

    private Logger logger;
    public WhitelistJe instance;
    private BukkitManager bukkitManager;
    private GuildManager guildManager;
    private ConfigManager configManager;
    private DiscordManager discordManager;
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
        this.instance = this;
        this.discordManager = new DiscordManager(this);
        this.bukkitManager = new BukkitManager(this);
        this.configManager = new ConfigManager();
        this.guildManager = new GuildManager(this.discordManager.getGuild());

        this.updateAllPlayers();
        this.updateAllowedPlayers();

        Logger.getLogger("WhiteList-Je").info(this.figlet);
    }

    @Override
    public void onDisable() {
       this.discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
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
        this.players = new UsersDao().findAll();
        return this.players;
    }

    public JSONArray updateAllowedPlayers() {
        this.playersAllowed = new UsersDao().findAllowed();
        return this.playersAllowed;
    }

    public void updatePlayerUUID(Integer id, UUID mc_uuid) {
        new UsersDao().setPlayerUUID(id, mc_uuid);
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
