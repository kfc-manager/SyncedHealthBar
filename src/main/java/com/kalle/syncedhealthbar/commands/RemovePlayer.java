package com.kalle.syncedhealthbar.commands;

import com.kalle.syncedhealthbar.Config;
import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.PlayerNotInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The class of the command "removeHB" that implements its command execution.
 */
public class RemovePlayer implements CommandExecutor {

    private Config config; //config because we need to make changes to the config.yml

    /**
     * A class constructor so we can pass the config to the object.
     * @param config config object so we can make changes to the config
     */
    public RemovePlayer(Config config) {
        this.config = config;
    }

    /**
     * The method that implements "removeHB" execution procedure.
     * @param sender sender who is executing the command
     * @param command command that is getting executed
     * @param label
     * @param args arguments of the command that is getting executed
     * @return true if it is the passed command, false if not
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("removeHB")) { //check if string matches with command
            //check if command has the right amount of arguments
            if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Too many arguments!");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "The command requires an argument!");
                return true;
            }
            for (Player i : Bukkit.getOnlinePlayers()) { //loop through online players
                if (i.getName().equals(args[0])) { //check if name equals to the player we look at in this iteration
                    try {
                        HealthBar hb = HealthBar.getHealthBar(i); //get his health bar
                        hb.removePlayer(i); //remove the player from the health bar
                        sender.sendMessage(ChatColor.GREEN + "The player: '" + args[0] + "' has been removed from his health bar!");
                    } catch (PlayerNotInListException e) { //player is not assigned to any health bar
                        sender.sendMessage(ChatColor.RED + e.getMessage());
                    } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                        sender.sendMessage((ChatColor.RED + e.getMessage()));
                    }
                    return true;
                }
            }
            try {
                config.removePlayer(args[0]); //remove the player from the config.yml (only needs to be removed from the config.yml since he is not online)
                sender.sendMessage(ChatColor.GREEN + "The player: '" + args[0] + "' has been removed from his health bar!");
            } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (PlayerNotInListException e) { //player is not assigned to any health bar
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            return true;
        }
        return false;
    }

}
