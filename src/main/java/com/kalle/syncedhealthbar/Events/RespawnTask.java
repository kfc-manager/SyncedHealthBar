package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;

/**
 * A class that extends Thread in order to track when a player actually respawns on the map
 * to be able to change his health to the health value of his assigned health bar.
 */
public class RespawnTask extends Thread {

    private Player player; //player who respawns
    private HealthBar hb; //the player's health bar

    /**
     * A class constructor to be able to pass the player and his health bar
     * to the thread.
     * @param player player which health needs to be changed
     * @param hb health bar of the player
     */
    public RespawnTask(Player player, HealthBar hb) {
        super();
        this.player = player;
        this.hb = hb;
    }

    /**
     * The method executed when the thread gets started.
     */
    public void run() {
        try {
            String location = player.getLocation().toString(); //get initial location of the player
            String tmpLocation = location; //create temporary variable to compare locations
            while (location.equals(tmpLocation)) { //when location changes the player is respawned on the map
                tmpLocation = player.getLocation().toString();
            }
            player.setHealth(hb.getHealth()); //set health of the player to the health of his health bar
        } catch (NullPointerException e) { //something went wrong with parsing the player's location to a string
            return;
        }
    }

}
