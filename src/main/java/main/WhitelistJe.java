package main;


import java.net.http.WebSocket.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import discord.DiscordManager;


public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);
    }

    @Override
    public void onDisable() {
       this.discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }
}
