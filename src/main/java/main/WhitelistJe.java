package main;

import java.net.http.WebSocket.Listener;
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
import helpers.DbPoolFactory;
import helpers.PooledDatasource;

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

    public String getfiglet() {
        String figlet = """

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

        figlet += "\n" + this.getPluginInfos(true);

        return figlet;
    }

    public WhitelistJe() {
        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
    }

    public String getPluginInfos(boolean toConsole) {

        final String devName = toConsole ? "@xXx-RaFuX#1345" : "<@272924120142970892>";

        return "Name: `" + this.getName() + "`\n" +
        "Version: `" + this.getVersion() + "`\n" +
        "Developped by: " + devName + "\n\n";
    }

    public String getVersion() {
        return this.configManager.get("pluginVersion", "2022.2");
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        daoManager = setDaoManager();
        discordManager = new DiscordManager(this);
        guildManager = new GuildManager(discordManager.getGuild());
        bukkitManager = new BukkitManager(this);

        updateAllPlayers();
        updateAllowedPlayers();

        Logger.getLogger("WhiteList-Je").info(this.getfiglet());
        guildManager.getAdminChannel().sendMessage("**Le plugin `" + this.getName() + "` est loader**\n\n" + getPluginInfos(false)).queue();
    }

    private DaoManager setDaoManager() {
        PooledDatasource pds;
        try {
            pds = DbPoolFactory.getMysqlPool(this.configManager);
            return new DaoManager(pds);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDisable() {
        guildManager.getAdminChannel().sendMessage("**Le plugin `" + this.getName() + "` est unloader**\n\n" + getPluginInfos(false)).queue();
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

    public BukkitManager getBukkitManager() {
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

    public void updatePlayerUUID(Integer id, UUID mc_uuid, boolean tempConfirmed) {
        daoManager.getUsersDao().setPlayerUUID(id, mc_uuid, tempConfirmed);
    }

    public Integer playerIsAllowed(UUID uuid) {
        Integer allowedUserId = -1;
        this.updateAllowedPlayers();

        try {
            for (Object object : this.playersAllowed) {
                final JSONObject player = (JSONObject) object;
                final String uuidReccord = player.optString("mc_uuid");
    
                if (uuidReccord.equals(uuid.toString())) {
                    allowedUserId = player.getInt("id");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allowedUserId;
    }
}
