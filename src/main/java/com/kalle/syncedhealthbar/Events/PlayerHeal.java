package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;

/**
 * A class which implements the listener that detects the event when a player heals.
 */
public class PlayerHeal implements Listener {

    /**
     * The method that implements what happens when the event gets triggered.
     * @param event event that gets triggered
     */
    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) { //check if the entity is a player
            Player player = (Player) event.getEntity(); //cast entity to player
            try {
                HealthBar hb = HealthBar.getHealthBar(player); //get the player's health bar
                if (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) { //check if healing is caused because a player has eaten enough
                    ArrayList<Player> players = hb.getPlayers(); //get all online players assigned to the same health bar
                    double foodLevel = 0; //create variable to calculate the mean of the food levels of the players
                    for (int i = 0; i < players.size(); i++) { //loop through players
                        foodLevel += players.get(i).getFoodLevel(); //add all food levels
                    }
                    foodLevel = foodLevel / players.size(); //divide by player size in order to get the mean
                    if (foodLevel < 18) { event.setCancelled(true); return; } //if the mean exceeds 18 the players regain health regularly
                }
                hb.healHealth(player,event.getAmount()); //heal all players by the same amount of healing done
            } catch (PlayerNotInListException e) { //player has no health bar assigned
                //case can occur and nothing needs to be done
                return;
            }
        }
    }

}
