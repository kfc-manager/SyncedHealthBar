package com.kalle.syncedhealthbar;

import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.ItemNotFoundException;
import com.kalle.syncedhealthbar.Exceptions.PlayerAlreadyInListException;
import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.naming.NameAlreadyBoundException;
import java.util.ArrayList;

/**
 * A class to represent a health bar in the game.
 */
public class HealthBar implements Listener {

    private Config config; //config because we need to make changes to the config.yml

    private ArrayList<Player> players = new ArrayList<Player>(); //list which holds all players added to the health bar
    private static ArrayList<HealthBar> healthBars = new ArrayList<HealthBar>(); //list to get health bar objects
    private double health; //health value which determines how much health the players currently have
    private String uName; //unique name by which the health bars can be identified

    /**
     * A class constructor which is used to create a new health bar.
     * @param config config variable to make changes to the config.yml inside the object
     * @param uName name by which the health bar can be called
     * @throws NameAlreadyBoundException when the name is already in use for another
     *                                   health bar exception gets thrown
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public HealthBar(Config config, String uName) throws NameAlreadyBoundException, CorruptedConfigException {
        this.config = config;
        for (HealthBar i : healthBars) { //loop through already existing health bars
            if (i.getName().equals(uName)) { //check if names equal
                throw new NameAlreadyBoundException("The name is already taken."); //name is already taken
            }
        }
        this.uName = uName; //set name
        this.health = 20; //set health value to full health since health bar is new
        config.addHealthBar(this); //save health bar in the config.yml
        healthBars.add(this); //add health bar to list
    }

    /**
     * A class constructor which is used to load a health bar when the
     * server is getting started or restarted.
     * @param config config variable to make changes to the config.yml inside the object
     * @param uName name by which the health bar can be called
     * @param health health value how much health the players have
     * @throws NameAlreadyBoundException name is duplicated in the config.yml, and it is probably corrupted
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public HealthBar(Config config, String uName, double health) throws NameAlreadyBoundException, CorruptedConfigException {
        this.config = config;
        for (HealthBar i : healthBars) { //loop through already existing health bars
            if (i.getName().equals(uName)) { //check if names equal
                throw new NameAlreadyBoundException("The name is already taken."); //name is already taken
            }
        }
        this.uName = uName; //set name
        this.health = health; //set health value to passed health
        healthBars.add(this); //add health bar to list
    }

    /**
     * A method to get the name of the health bar.
     * @return name of the health bar
     */
    public String getName() {
        return uName;
    }

    /**
     * A method to get the health value of the health bar.
     * @return health value of the health bar
     */
    public double getHealth() {
        return health;
    }

    /**
     * A method to get a health bar by its unique name.
     * @param uName name of the health bar that is needed
     * @return health bar that has the passed name
     * @throws ItemNotFoundException no health bar has the passed name and exception is thrown
     */
    public static HealthBar getHealthBar(String uName) throws ItemNotFoundException {
        for (HealthBar i : healthBars) { //loop through all health bars
            if (i.getName().equals(uName)) { //compare names
                return i; //names match and health bar in this iteration gets returned
            }
        }
        throw new ItemNotFoundException("The health bar: '" + uName + "' does not exist!"); //no health bar has the name
    }

    /**
     * A method to get a health bar by a player that is assigned to that health bar.
     * @param player player of which the health bar is needed
     * @return health bar of passed player
     * @throws PlayerNotInListException player is not assigned to any health bar and exception is thrown
     */
    public static HealthBar getHealthBar(Player player) throws PlayerNotInListException {
        for (HealthBar i : healthBars) { //loop through health bars
            if (i.players.contains(player)) { //check if player is in players list of health bar we look at in this iteration
                return i; //return the health bar if player is a part of the health bar
            }
        }
        throw new PlayerNotInListException("Player: '" + player.getName() + "' has no health bar assigned!"); //player is not assigned to any health bar
    }

    /**
     * A method to add a player to the health bar.
     * @param player player that needs to be added to the health bar
     * @throws PlayerAlreadyInListException player is already added to a health bar and exception is thrown
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public void addPlayer(Player player) throws PlayerAlreadyInListException, CorruptedConfigException {
        config.addPlayer(getName(), player); //make changes to the config.yml also checks if player is already assigned to a health bar
        players.add(player); //add the player to the players list of the health bar
        player.setHealth(health); //set the health of the player to the health value of the health bar since he is now a part of the health bar
    }

    /**
     * A method to load a player to the health bar when the server gets restarted.
     * @param player player that needs to be loaded
     * @throws PlayerAlreadyInListException player is already in the players list and exception is thrown
     */
    public void loadPlayer(Player player) throws PlayerAlreadyInListException {
        if (players.contains(player)) { //check if player is already in the list
            throw new PlayerAlreadyInListException("Player: '" + player.getName() + "' is already assigned to this health bar!");
        }
        players.add(player); //add player to the list
    }

    /**
     * A method to load a player to the health bar when he joins the server.
     * @param player player that needs to be loaded
     */
    public void joinPlayer(Player player) {
        players.add(player); //add player to the list
    }

    /**
     * A method to remove the player from the health bars player list when he goes offline.
     * @param player player that needs to be removed
     */
    public void quitPlayer(Player player) {
        players.remove(player); //remove player from the list
    }

    /**
     * A method to remove a player permanently from his assigned health bar.
     * @param player player that needs to be removed
     * @throws PlayerNotInListException player is not assigned to any health bar and can not be removed from one
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public void removePlayer(Player player) throws PlayerNotInListException, CorruptedConfigException {
        config.removePlayer(this,player); //remove player from his health bar in the config
        players.remove(player); //remove player from players list of the health bar
    }

    /**
     * A method to delete a health bar.
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public void deleteHealthBar() throws CorruptedConfigException {
        config.deleteHealthBar(getName()); //delete the entry of the health bar from the config.yml
        players = null; //delete the array list of the health bar
        healthBars.remove(this); //remove the health bar from the health bar list so it can not be called anymore
    }

    /**
     * A method to deal damage to all online players that are assigned to the health bar.
     * @param player player that initiated the damage and does not need to be damaged again
     * @param damage amount of damage to deal to the players
     */
    public void dealDamage(Player player, double damage) {
        health -= damage; //calculate new health of the health bar
        if (health < 0) { //check if health is still in bounds
            health = 0; //set health to 0 (kill the players)
        }
        for (Player i : players) { //apply damage to all players in health bar players list
            if (player == i) continue; //player is the player that does not need to be damaged
            if (i.isDead()) continue; //player is not respawned yet and can not take more damage (otherwise visual bug in the game gets created)
            i.setHealth(health); //set health of the player to new value
        }
        if (health == 0) { //check if players died
            health = 20; //set health back to full health (respawn)
        }
        config.setHealth(this,health); //save changes to config.yml
    }

    /**
     * A method to heal the online players that are assigned to the health bar.
     * @param player player that initiated the healing and does not need to be healed again
     * @param healing amount of healing to heal the players
     */
    public void healHealth(Player player, double healing) {
        health += healing; //calculate new health of the health bar
        if (health >= 20) health = 20; //check if health is still in bounds
        for (Player i : players) { //apply healing to all players in the health bar players list
            if (player == i) continue; //player is the player that does not need to be healed
            if (i.isDead()) continue; //player is not respawned yet and can not be healed
            i.setHealth(health); //set health of the player to new value
        }
        config.setHealth(this,health); //save changes to config.yml
    }

    /**
     * A method to get the players assigned to the health bar that are currently online.
     * @return list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

}
