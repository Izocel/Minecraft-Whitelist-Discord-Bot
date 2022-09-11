package main;

import java.net.http.WebSocket.Listener;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import dao.UsersDao;
import discord.DiscordManager;



public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private Logger logger;
    private DiscordManager discordManager;
    private JSONArray playersAllowed = new JSONArray();
    private BukkitManager bukkitManager;

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

    public void updateAllowedPlayers() {
        this.playersAllowed = new UsersDao().findAllowed();
    }

    public void updatePlayerUUID(Integer id, UUID UUID) {
        new UsersDao().setPlayerUUID(id, UUID);
    }

    public Integer playerIsAllowed(String playerName) {
        this.updateAllowedPlayers();
        Integer allowedUserId = -1;

        for (Object object : this.playersAllowed) {
            final JSONObject player = (JSONObject) object;

            final String nameReccord = player.getString("name");
            if(nameReccord.equals(playerName)) {
                allowedUserId = player.getInt("id");
                break;
            }
        }

        return allowedUserId;
    }
}
