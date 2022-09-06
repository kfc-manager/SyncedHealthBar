package com.kalle.syncedhealthbar.commands;

import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.Exceptions.ItemNotFoundException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The class of the command "deleteHB" that implements its command execution.
 */
public class DeleteHealthBar implements CommandExecutor {

    /**
     * The method that implements "deleteHB" execution procedure.
     * @param sender sender who is executing the command
     * @param command command that is getting executed
     * @param label
     * @param args arguments of the command that is getting executed
     * @return true if it is the passed command, false if not
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("deleteHB")) { //check if string matches with command
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
                HealthBar hb = HealthBar.getHealthBar(args[0]); //get the health bar
                hb.deleteHealthBar(); //delete the health bar
                sender.sendMessage(ChatColor.GREEN + "The health bar: '" + args[0] + "' has been deleted!");
            } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (ItemNotFoundException e) { //health bar that is supposed to be deleted does not exist
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            return true;
        }
        return false;
    }

}
