package com.kalle.syncedhealthbar.commands;

import com.kalle.syncedhealthbar.Config;
import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.ItemNotFoundException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * The class of the command "listHB" that implements its command execution.
 */
public class ListHealthBar implements CommandExecutor {

    private Config config; //config because we need to make changes to the config.yml

    /**
     * A class constructor so we can pass the config to the object.
     * @param config config object so we can make changes to the config
     */
    public ListHealthBar(Config config) {
        this.config = config;
    }

    /**
     * The method that implements "listHB" execution procedure.
     * @param sender sender who is executing the command
     * @param command command that is getting executed
     * @param label
     * @param args arguments of the command that is getting executed
     * @return true if it is the passed command, false if not
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("listHB")) { //check if string matches with command
            //check if command has the right amount of arguments
            if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Too many arguments!");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "The command requires an argument!");
                return true;
            }
            try {
                HealthBar hb = HealthBar.getHealthBar(args[0]); //get health bar
                ArrayList<String> players = config.getPlayerList(hb); //get the list of names for each player
                if (players.size() == 0) { //check if players are assigned to the health bar
                    sender.sendMessage(ChatColor.GREEN + "No player is added to the health bar: '" + args[0] + "'!");
                    return true;
                }
                String result = "'" + players.get(0) + "'"; //string that we build to return
                for (int i = 1; i < players.size(); i++) { //loop through the players
                    result += ", '" + players.get(i) + "'"; //add player to the result string
                }
                sender.sendMessage(ChatColor.GREEN + "The players: " + result + " are assigned to the health bar: '" + args[0] + "'!"); //print the result to the sender
            } catch (ItemNotFoundException e) { //health bar does not exist
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            return true;
        }
        return false;
    }

}
