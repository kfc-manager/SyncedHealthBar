package com.kalle.syncedhealthbar.commands;

import com.kalle.syncedhealthbar.Config;
import com.kalle.syncedhealthbar.Exceptions.CorruptedConfigException;
import com.kalle.syncedhealthbar.HealthBar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.naming.NameAlreadyBoundException;

/**
 * The class of the command "createHB" that implements its command execution.
 */
public class CreateHealthBar implements CommandExecutor {

    private Config config; //config because we need to make changes to the config.yml

    /**
     * A class constructor so we can pass the config to the object.
     * @param config config object so we can make changes to the config
     */
    public CreateHealthBar(Config config) {
        this.config = config;
    }

    /**
     * The method that implements "createHB" execution procedure.
     * @param sender sender who is executing the command
     * @param command command that is getting executed
     * @param label
     * @param args arguments of the command that is getting executed
     * @return true if it is the passed command, false if not
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("createHB")) { //check if string matches with command
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
                HealthBar hb = new HealthBar(config,args[0]); //create the new health bar
                sender.sendMessage(ChatColor.GREEN + "The health bar: '" + args[0] + "' has been created!");
            } catch (NameAlreadyBoundException e) { //name is already used by another health bar
                sender.sendMessage(ChatColor.RED + "The name: '" + args[0] + "' is already taken!");
            } catch (CorruptedConfigException e) { //config.yml is corrupted and can not be worked with
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        return false;
    }

}
