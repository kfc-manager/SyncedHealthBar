package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * A class which implements the listener that detects the event when a player leaves the server.
 */
public class PlayerQuit implements Listener {

    /**
     * The method that implements what happens when the event gets triggered.
     * @param event event that gets triggered
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer(); //get the player that triggered the event
        try {
            HealthBar hb = HealthBar.getHealthBar(player); //get the player's health bar
            hb.quitPlayer(player); //remove the player from the health bar's list of players that are online
        } catch (PlayerNotInListException e) { //player has no health bar assigned
            //case can occur and nothing needs to be done
            return;
        }
    }

}
