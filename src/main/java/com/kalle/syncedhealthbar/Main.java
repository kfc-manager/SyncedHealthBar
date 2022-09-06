package com.kalle.syncedhealthbar;

import com.kalle.syncedhealthbar.Events.*;
import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.PlayerAlreadyInListException;
import com.kalle.syncedhealthbar.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Main class that holds the startup and shutdown logic of the plugin.
 */
public final class Main extends JavaPlugin {

    private Config config = new Config(this); //variable to create a config.yml for the plugin

    //Commands:
    private CreateHealthBar create = new CreateHealthBar(config);
    private DeleteHealthBar delete = new DeleteHealthBar();
    private AddPlayer add = new AddPlayer();
    private RemovePlayer remove = new RemovePlayer(config);
    private ListHealthBar list = new ListHealthBar(config);

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            config.loadConfig();
            //register commands
            getCommand("createHB").setExecutor(create);
            getCommand("deleteHB").setExecutor(delete);
            getCommand("addHB").setExecutor(add);
            getCommand("removeHB").setExecutor(remove);
            getCommand("listHB").setExecutor(list);
            //register events
            getServer().getPluginManager().registerEvents(new PlayerJoin(config), this);
            getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
            getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
            getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
            getServer().getPluginManager().registerEvents(new PlayerHeal(), this);

            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SyncedHealthBar has been enabled!");
        } catch (CorruptedConfigException e) { //config.yml is corrupted and plugin should get reloaded
            getServer().getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: SyncedHealthBar could not be loaded. Please repair the plugins/SyncedHealthBar/config.yml file and restart the server!");
        } catch (PlayerAlreadyInListException e) { //config.yml is corrupted and plugin should get reloaded
            getServer().getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: SyncedHealthBar could not be loaded. Please repair the plugins/SyncedHealthBar/config.yml file and restart the server!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "SyncedHealthBar has been disabled!");
    }
}
