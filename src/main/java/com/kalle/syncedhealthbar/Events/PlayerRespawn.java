package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * A class which implements the listener that detects the event when a player respawns.
 */
public class PlayerRespawn implements Listener {

    /**
     * The method that implements what happens when the event gets triggered.
     * @param event event that gets triggered
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer(); //get the player that triggered the event
        try {
            HealthBar hb = HealthBar.getHealthBar(player); //get the player's health bar
            RespawnTask task = new RespawnTask(player,hb); //create thread to track when the player actually spawns on map (otherwise health can not be set cause event overwrites it to full health at the end)
            task.start(); //start the thread
        } catch (PlayerNotInListException e) { //player has no health bar assigned
            //case can occur and nothing needs to be done
            return;
        }
    }

}
