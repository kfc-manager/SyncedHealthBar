package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.Config;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A class which implements the listener that detects the event when a player joins the server.
 */
public class PlayerJoin implements Listener {

    private Config config; //config because we need to make changes to the config.yml

    /**
     * A class constructor so we can pass the config to the object.
     * @param config config object so we can make changes to the config
     */
    public PlayerJoin(Config config) {
        this.config = config;
    }

    /**
     * The method that implements what happens when the event gets triggered.
     * @param event event that gets triggered
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer(); //get the player that triggered the event
        config.updatePlayer(player); //update name and last login of the player in the config.yml
        HealthBar hb = config.getHealthBar(player); //get the health bar of the player (by entry stored in the config.yml)
        try {
            hb.joinPlayer(player); //load player in the health bar
            player.setHealth(hb.getHealth()); //set players health to the health of the health bar
        } catch (NullPointerException e) { //player has no health bar assigned
            //case can occur and nothing needs to be done
            return;
        }
    }

}
