package com.kalle.syncedhealthbar;

import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.ItemNotFoundException;
import com.kalle.syncedhealthbar.Exceptions.PlayerAlreadyInListException;
import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import javax.naming.NameAlreadyBoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A class to handle all operations on the config.yml file of the plugin.
 */
public class Config {

    private Main plugin;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm | dd.MM.yyyy"); //The format how dates are stored

    //paths of the config.yml entries to make statements more readable
    String hbPath = "Health Bar ";
    String pPath = ".Player ";
    String hbCountPath = "Health Bar Count";
    String pCountPath = ".Player Count";

    /**
     * Class constructor.
     * @param plugin main class must be passed in order to be able
     *               to make changes to the config.yml
     */
    public Config(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * A method to load on every server start and restart all health bars
     * from the config.yml.
     * @throws CorruptedConfigException manually changes were made to the config.yml
     *                                  so that the file is corrupted and can not
     *                                  be worked with
     * @throws PlayerAlreadyInListException when a player gets added to the health bar he
     *                                      already has been added to the exception is
     *                                      thrown (config.yml must be corrupted since
     *                                      player is duplicated in file)
     */
    public void loadConfig() throws CorruptedConfigException, PlayerAlreadyInListException {
        plugin.getConfig().options().copyDefaults(true);
        if (plugin.getConfig().get(hbCountPath) == null) { //if the config.yml does not exist yet
            plugin.getConfig().set(hbCountPath,"0");
        }
        plugin.saveConfig();
        int hbCount = getHBCount(); //count of health bars
        for (int i = 0 ; i < hbCount ; i++) { //loops through all health bars
            String hbName = getHBName(i); //get the name of the health bar
            double health = getHBHealth(i); //get the health value of the health bar
            try {
                HealthBar hb = new HealthBar(this, hbName, health); //create the health bar
            } catch (NameAlreadyBoundException e) { //config.yml must have been manually changed so the names are duplicated
                throw new CorruptedConfigException("CONFIG ERROR: health bar name duplicate found!");
            }
        }
        for (Player i : Bukkit.getOnlinePlayers()) { //loop through online players to add them all to their health bar in case of a server restart
            try {
                int[] index = getPIndex(i); //the index array contains the health bar index and player index to tell us which health bar he needs to be added to
                String hbName = getHBName(index[0]); //name of the health bar works as unique identifier
                HealthBar hb = HealthBar.getHealthBar(hbName); //get the health bar
                hb.loadPlayer(i); //add player to array list of the health bar which contains the players
            } catch (PlayerAlreadyInListException | PlayerNotInListException e) { //these exceptions should not occur but do not need to be handled because the plugin can function normally
                continue;
            } catch (ItemNotFoundException e) { //the config.yml must be gotten corrupted in the process of loading the data in this method
                throw new CorruptedConfigException("CONFIG ERROR: health bar name corrupted!");
            }
        }
    }

    /**
     * A method to add a new health bar to the config.yml.
     * @param hb health bar gets passed to get all necessary values
     *           that need to be stored
     * @throws CorruptedConfigException the count of health bars is either deleted from the config.yml
     *                                  or is not an integer anymore (due to manual changes)
     */
    public void addHealthBar(HealthBar hb) throws CorruptedConfigException {
        String name = hb.getName();
        String health = hb.getHealth() + ""; //health value (double) gets converted to string
        int hbCount;
        try {
            hbCount = getHBCount();
        } catch (NullPointerException e) { //entry was deleted (count = null)
            throw new CorruptedConfigException("CONFIG ERROR: health bar count not found!");
        } catch (NumberFormatException e) { //entry was changed and is not convertable to integer anymore
            throw new CorruptedConfigException("CONFIG ERROR: health bar count corrupted!");
        }
        plugin.getConfig().set(hbPath + hbCount + ".Name", name); //store name of the health bar
        plugin.getConfig().set(hbPath + hbCount + ".Health", health); //store health value of the health bar
        plugin.getConfig().set(hbPath + hbCount + pCountPath, 0); //create player count and set it to 0 since there has been no players added yet
        plugin.getConfig().set(hbCountPath, (hbCount + 1) + ""); //increment health bar count since we added a new health bar
        plugin.saveConfig(); //save all changes made
    }

    /**
     * A method to delete a health bar from the config.yml.
     * @param hbName name of the health bar that needs to be deleted
     *               since the name is used as unique identifier
     * @throws CorruptedConfigException the count of health bars is either deleted from the config.yml
     *                                  or is not an integer anymore (due to manual changes)
     */
    public void deleteHealthBar(String hbName) throws CorruptedConfigException {
        int hbCount;
        try {
            hbCount = getHBCount();
        } catch (NullPointerException e) { //entry was deleted (count = null)
            throw new CorruptedConfigException("CONFIG ERROR: health bar count not found!");
        } catch (NumberFormatException e) { //entry was changed and is not convertable to integer anymore
            throw new CorruptedConfigException("CONFIG ERROR: health bar count corrupted!");
        }
        for (int j = getHBIndex(hbName) ; j < hbCount - 1 ; j++) { //loop through all the health bars behind the one that needs to be deleted
            //put every health bar one position further front, so no empty entries getting created
            Object next = plugin.getConfig().get(hbPath + (j+1));
            plugin.getConfig().set(hbPath + j,next);
        }
        plugin.getConfig().set(hbPath + (hbCount-1), null); //delete the last health bar since copy is saved one count lower
        plugin.getConfig().set(hbCountPath, (hbCount-1) + ""); //set the count of health bars to -1 since we deleted a health bar
        plugin.saveConfig(); //save all changes made
    }

    /**
     * A method to add a player to a health bar in the config.yml.
     * @param hbName name of the health bar of which a player needs
     *               to be added to since the name is used as
     *               unique identifier
     * @param player player that needs to be added in order to get
     *               the necessary data that needs to be stored
     *               (UUID and name of the player)
     * @throws CorruptedConfigException manually changes were made to the config.yml
     *                                  so that the file is corrupted and can not
     *                                  be worked with (name of health bar is deleted)
     * @throws PlayerAlreadyInListException getPIndex(player) returns an index which means that a
     *                                      player is already added to a health bar and
     *                                      the exception is thrown
     */
    public void addPlayer(String hbName, Player player) throws CorruptedConfigException, PlayerAlreadyInListException {
        Calendar calendar = new GregorianCalendar();
        try {
            getPIndex(player); //check if the player is already added to a health bar and throws PlayerNotInListException if not
            throw new PlayerAlreadyInListException("Player: '" + player.getName() + "' is already added to the health bar: '" + hbName + "'!");
        } catch (PlayerNotInListException e) { //player is not added to any health bar, and we can presume
            plugin.getConfig().set(hbPath + getHBIndex(hbName) + pPath + getPCount(hbName) + ".Name", player.getName()); //set player name in config.yml
            plugin.getConfig().set(hbPath + getHBIndex(hbName) + pPath + getPCount(hbName) + ".UUID", player.getUniqueId().toString()); //set player UUID in config.yml
            Date date = calendar.getTime(); //get the time of when the player is added to health bar
            plugin.getConfig().set(hbPath + getHBIndex(hbName) + pPath + getPCount(hbName) + ".Last Login", sdf.format(date)); //set player last login to time he was added to the health bar
            plugin.getConfig().set(hbPath + getHBIndex(hbName) + pCountPath, (getPCount(hbName)+1) + ""); //increase the player count of the health bar by 1
            plugin.saveConfig(); //save all changes made
        } catch (NullPointerException e) { //entry was deleted (name = null)
            throw new CorruptedConfigException("CONFIG ERROR: health bar name not found!");
        }
    }

    /**
     * A method to delete a player from his health bar in the config.yml (player must be online).
     * @param hb health bar that he gets removed from
     * @param player player that gets removed in order to get
     *               his UUID, so he can be identified in the
     *               config.yml
     * @throws PlayerNotInListException the player was not found in the config.yml
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public void removePlayer(HealthBar hb, Player player) throws PlayerNotInListException, CorruptedConfigException {
        try {
            int[] index = getPIndex(player); //get index of the health bar the player is assigned to and the index of the player
            int pcount = getPCount(hb.getName()); //get the player count of the health bar
            deletePlayerEntry(index, pcount); //delete the entry in the config.yml
        } catch (NumberFormatException e) {
            throw new CorruptedConfigException("CONFIG ERROR: player count corrupted!");
        }
    }

    /**
     * A method to remove a player from his health bar in the config.yml by his name (if he is not online).
     * Players can have the same name in the config. If player1 changes his name to another name and
     * player2 changes his name to the old name of player1 and player1 has not joined the server since
     * the name change. We need to remove the player with the most recent "last login".
     * @param name name of the player to find him in the config.yml
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     * @throws PlayerNotInListException the player was not found in the config.yml
     */
    public void removePlayer(String name) throws CorruptedConfigException, PlayerNotInListException {
        ArrayList<int[]> index = getPIndex(name); //get all the index of every player with the name
        if (index.isEmpty()) { //if the array list is empty there is no player with that name
            throw new PlayerNotInListException("Player: '" + name + "' has no health bar assigned!");
        }
        int[] remove = index.get(0); //initialize with the first player in the array list
        String sDate;
        try {
            sDate = plugin.getConfig().get(hbPath + index.get(0)[0] + pPath + index.get(0)[1] + ".Last Login").toString(); //get the last login of the first in array list
            Date dateAkt = sdf.parse(sDate); //parse to the date
            for (int i = 1 ; i < index.size() ; i++) { //loop through the array list
                sDate = plugin.getConfig().get(hbPath + index.get(i)[0] + pPath + index.get(i)[1] + ".Last Login").toString(); //get the date of the player we look at in this iteration
                if (dateAkt.before(sdf.parse(sDate))) {  //check if he logged in after the one with the latest login
                    dateAkt = sdf.parse(sDate);
                    remove = index.get(i); //change the one that needs be removed
                }
            }
        } catch (NullPointerException e) { //entry was deleted (last login = null)
            throw new CorruptedConfigException("CONFIG ERROR: players last login not found!");
        } catch (ParseException e) { //entry was changed and can not be parsed to a date anymore
            throw new CorruptedConfigException("CONFIG ERROR: players last login corrupted!");
        }
        try {
            String hbName = plugin.getConfig().get(hbPath + remove[0] + ".Name").toString();
            int pCount = getPCount(hbName); //get the player count of the health bar with the player that gets removed
            deletePlayerEntry(remove,pCount); //delete the entry with the player that gets removed
        } catch (NullPointerException e) { //entry was deleted (name = null)
            throw new CorruptedConfigException("CONFIG ERROR: health bar name not found!");
        } catch (NumberFormatException e) { //entry was changed and could not be parsed into integer
            throw new CorruptedConfigException("CONFIG ERROR: player count corrupted!");
        }
    }

    /**
     * A method to delete the entry of a player from the config.yml.
     * @param index an array with the index of the health bar the player is in
     *              and the index of the player to find the path of the entry
     *              that needs to be deleted
     * @param pcount the count of players in the health bar for bounds of the loop
     */
    public void deletePlayerEntry(int[] index, int pcount) {
        for (int i = index[1] ; i < pcount - 1 ; i++) { //loop through all the players behind the one that needs to be deleted
            //put every player one position further front, so no empty entries getting created
            Object next = plugin.getConfig().get(hbPath + index[0] + pPath + (i + 1));
            plugin.getConfig().set(hbPath + index[0] + pPath + i,next);
        }
        plugin.getConfig().set(hbPath + index[0] + pPath + (pcount-1), null); //delete the last player since copy is saved one count lower
        plugin.getConfig().set(hbPath + index[0] + pCountPath, (pcount-1) + ""); //set the count of players to -1 since we deleted a player
        plugin.saveConfig(); //save all changes made
    }

    /**
     * A method to get the health bar count in the config.yml.
     * @return the health bar count (how many health bars are stored in the config.yml)
     * @throws NullPointerException entry was deleted (count = null)
     * @throws NumberFormatException entry was changed and is not convertable to integer anymore
     */
    public int getHBCount() throws NullPointerException,NumberFormatException {
        return Integer.parseInt(plugin.getConfig().get(hbCountPath).toString()); //parse entry to integer
    }

    /**
     * A method to get the health bar health value in the config.yml.
     * @param index the index of the health bar of which the health value is needed
     * @return the double of the value that is stored in the config.yml of the health bar
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public double getHBHealth(int index) throws CorruptedConfigException {
        double health;
        try {
            health = Double.parseDouble(plugin.getConfig().get(hbPath + index + ".Health").toString()); //parsing the entry into a double
        } catch (NullPointerException e) {
            throw new CorruptedConfigException("CONFIG ERROR: health bar health value not found!");
        } catch (NumberFormatException e) {
            throw new CorruptedConfigException("CONFIG ERROR: health bar health value corrupted!");
        }
        if (health > 20 || health < 0) { //health needs to be between 0 and 20
            throw new CorruptedConfigException("CONFIG ERROR: health bar health value corrupted!");
        }
        return health;
    }

    /**
     * A method to get the player count of the health bar in the config.yml.
     * @param hbName the name of the health bar to identify the health bar of which
     *               the player count is needed
     * @return the player count of the health bar
     * @throws NullPointerException entry was deleted (count = null)
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     * @throws NumberFormatException the count could not be parsed to integer and exception is thrown
     */
    public int getPCount(String hbName) throws NullPointerException, CorruptedConfigException, NumberFormatException {
        return Integer.parseInt(plugin.getConfig().get(hbPath + getHBIndex(hbName) + pCountPath).toString()); //parse entry to integer
    }

    /**
     * A method to get the index of the entry of a health bar.
     * @param hbName the name of the health bar to identify it in the config.yml
     * @return the index of the entry
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public int getHBIndex(String hbName) throws CorruptedConfigException {
        int hbCount;
        try {
            hbCount = getHBCount(); //get the count for the bound of the for-loop
        } catch (NullPointerException e) {
            throw new CorruptedConfigException("CONFIG ERROR: health bar count not found!");
        } catch (NumberFormatException e) {
            throw new CorruptedConfigException("CONFIG ERROR: health bar count corrupted!");
        }
        for (int i = 0 ; i < hbCount ; i++) { //loop through all health bars
            String tmpName;
            try {
                tmpName = plugin.getConfig().get(hbPath + i + ".Name").toString(); //put name of the health bar we look at in this iteration in temporary variable
            } catch (NullPointerException e) {
                throw new CorruptedConfigException("CONFIG ERROR: health bar name not found!");
            }
            if (tmpName.equals(hbName)) { //compare names, if equal we found the needed health bar
                return i;
            }
        }
        throw new CorruptedConfigException("CONFIG ERROR: health bar name not found!");
    }

    /**
     * A method to get the index of the health bar a player is added to and the index
     * of the player in that health bar.
     * @param player the player of which the index is needed
     * @return the index of the entry of the health bar and the index of the entry of
     *         the player in the config.yml
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     * @throws PlayerNotInListException the player has no entry in the config.yml and
     *                                  no index can be returned
     */
    public int[] getPIndex(Player player) throws CorruptedConfigException, PlayerNotInListException {
        String uuid = player.getUniqueId().toString();
        for (int i = 0 ; i < getHBCount() ; i++) { //loop through health bars
            for (int j = 0 ; j < getPCount(getHBName(i)) ; j++) { //loop through players
                String tmpUUID;
                try {
                    tmpUUID = plugin.getConfig().get(hbPath + i + pPath + j + ".UUID").toString(); //put uuid of the player we look at in this iteration in temporary variable
                } catch (NullPointerException e) {
                    throw new CorruptedConfigException("CONFIG ERROR: player uuid not found!");
                }
                if (uuid.equals(tmpUUID)) { //compare UUIDs, if equal we found the needed player
                    int[] index = new int[2];
                    index[0] = i; //put index of the health bar in array
                    index[1] = j; //put index of the player in array
                    return index; //return array
                }
            }
        }
        throw new PlayerNotInListException("Player: '" + player.getName() + "' is not assigned to any health bar!"); //player was no where found
    }

    /**
     * A method to get the index of the health bar a player is added to and the index
     * of the player in that health bar for all players with the passed name.
     * @param name name of the players of which the indices are needed
     * @return array list with all the indices of players with given name
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public ArrayList<int[]> getPIndex(String name) throws CorruptedConfigException {
        ArrayList<int[]> index = new ArrayList<int[]>();
        for (int i = 0 ; i < getHBCount() ; i++) { //loop through health bars
            for (int j = 0 ; j < getPCount(getHBName(i)) ; j++) { //loop through players
                String tmpName;
                try {
                    tmpName = plugin.getConfig().get(hbPath + i + pPath + j + ".Name").toString(); //put name of the player we look at in this iteration in temporary variable
                } catch (NullPointerException e) {
                    throw new CorruptedConfigException("CONFIG ERROR: player name not found!");
                }
                if (name.equals(tmpName)) { //compare names, if equal we found one of the needed players
                    int[] akt = new int[2];
                    akt[0] = i; //put index of the health bar in array
                    akt[1] = j; //put index of the player in array
                    index.add(akt); //put array in array list
                }
            }
        }
        return index; //return array list
    }

    /**
     * A method to get the name of a health bar by the index of its
     * entry in the config.yml.
     * @param i index of the entry of the health bar
     * @return name of the health bar
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public String getHBName(int i) throws CorruptedConfigException {
        String hbName;
        try {
            hbName = plugin.getConfig().get(hbPath + i + ".Name").toString(); //get the name from the entry in config.yml
        } catch (NullPointerException e) {
            throw new CorruptedConfigException("CONFIG ERROR: health bar name not found!");
        }
        return hbName;
    }

    /**
     * A method to get a list of the names of the players in the passed
     * health bar.
     * @param hb health bar of which the player names are needed
     * @return array list with the names of the players
     * @throws CorruptedConfigException config.yml was corrupted and exception is thrown
     */
    public ArrayList<String> getPlayerList(HealthBar hb) throws CorruptedConfigException {
        int hbIndex = getHBIndex(hb.getName());
        int pcount = getPCount(hb.getName()); //get the player count of the health bar as bound for the for-loop
        ArrayList<String> players = new ArrayList<String>();
        for (int i = 0; i < pcount; i++) { //loop through the players
            try {
                players.add(plugin.getConfig().get(hbPath + hbIndex + pPath + i + ".Name").toString()); //add the name of the player we look at in this iteration to the array list
            } catch (NullPointerException e) {
                throw new CorruptedConfigException("CONFIG ERROR: player name not found!");
            }
        }
        return players; //return array list
    }

    /**
     * A method to update the name and last login of a player in the config.yml.
     * @param player player which needs to be updated
     */
    public void updatePlayer(Player player) {
        try {
            int[] index = getPIndex(player); //get the index to find the entry that needs to be updated
            Calendar calendar = new GregorianCalendar();
            Date date = calendar.getTime(); //get current time
            plugin.getConfig().set(hbPath + index[0] + pPath + index[1] + ".Name", player.getName()); //update name
            plugin.getConfig().set(hbPath + index[0] + pPath + index[1] + ".Last Login", sdf.format(date)); //update last login
            plugin.saveConfig(); //save all changes made
        } catch (CorruptedConfigException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
        } catch (PlayerNotInListException e) {
            //player is not assigned to any health bar and nothing needs to happen
            return;
        }
    }

    /**
     * A method to get the health bar of a player by the data stored in the config.yml.
     * @param player player of which the health bar is needed
     * @return health bar of the player
     */
    public HealthBar getHealthBar(Player player) {
        try {
            int[] index = getPIndex(player); //get the index to find the entry that holds the needed information
            String hbName = getHBName(index[0]);
            HealthBar hb = HealthBar.getHealthBar(hbName); //get the health bar object
            return hb;
        } catch (CorruptedConfigException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
        } catch (ItemNotFoundException e) {
            plugin.getServer().getConsoleSender().sendMessage((ChatColor.RED + e.getMessage()));
        } catch (PlayerNotInListException e) {
            return null;
        }
        return null;
    }

    /**
     * A method to set the health value of a health bar.
     * @param hb health bar of which the health value needs to be set
     * @param health health value which needs to be set to
     */
    public void setHealth(HealthBar hb, double health) {
        try {
            int index = getHBIndex(hb.getName()); //get the index to find the entry
            plugin.getConfig().set(hbPath + index + ".Health", health); //set health value to passed double
            plugin.saveConfig();
        } catch (CorruptedConfigException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

}
