package com.kalle.syncedhealthbar.commands;

import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.ItemNotFoundException;
import com.kalle.syncedhealthbar.Exceptions.PlayerAlreadyInListException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The class of the command "addHB" that implements its command execution.
 */
public class AddPlayer implements CommandExecutor {

    /**
     * The method that implements "addHB" execution procedure.
     * @param sender sender who is executing the command
     * @param command command that is getting executed
     * @param label
     * @param args arguments of the command that is getting executed
     * @return true if it is the passed command, false if not
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("addHB")) { //check if string matches with command
            //check if command has the right amount of arguments
            if (args.length > 2) {
                sender.sendMessage(ChatColor.RED + "Too many arguments!");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "The command requires a health bar and a player as argument!");
                return true;
            }
            try {
                HealthBar hb = HealthBar.getHealthBar(args[1]); //get health bar that the player is supposed to be added to
                for (Player i : Bukkit.getOnlinePlayers()) { //loop through online players
                    if (i.getName().equals(args[0])) { //check if player is meant ot be added (if name is equal)
                        hb.addPlayer(i); //add player to the health bar
                        sender.sendMessage(ChatColor.GREEN + "PLayer: '" + args[0] + "' has been added to the health bar: '" + args[1] + "'!");
                        return true;
                    }
                }
                //no online player found with that name
                sender.sendMessage(ChatColor.RED + "Player: '" + args[0] + "' is currently not online!");
            } catch (ItemNotFoundException e) { //health bar does not exist
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (PlayerAlreadyInListException e) { //player is already assigned to a health bar
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            return true;
        }
        return false;
    }

}
