package com.kalle.syncedhealthbar.Events;

import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * A class which implements the listener that detects the event when a player takes damage.
 */
public class PlayerDamage implements Listener {

    /**
     * The method that implements what happens when the event gets triggered.
     * @param event event that gets triggered
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) { //check if the entity is a player
            Player player = (Player) event.getEntity(); //cast entity to player
            try {
                HealthBar hb = HealthBar.getHealthBar(player); //get the player's health bar
                double damage = event.getDamage(); //get the amount of damage the event has caused
                hb.dealDamage(player,damage); //deal damage to all players in the same health bar
            } catch (PlayerNotInListException e) { //player has no health bar assigned
                //case can occur and nothing needs to be done
                return;
            }
        }
    }

}
