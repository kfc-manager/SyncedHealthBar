# SyncedHealthBar

## What is SyncedHealthBar?

SyncedHealthBar is a Minecraft plugin for your spigot server. As the name already suggests it implements synchronized health bars in the game, which means that you can group players to one health bar so that they take damage, heal and die together! You can even create multiple health bars for a larger server, where people should not share all the same health bar.

## Features

You can create and delete a health bar, add and remove players to/from a health bar and list all players assigned to a health bar. Various commands are a part of the plugin (explained below). If one player takes damage, all players assigned to the same health bar take the damage aswell (same goes for healing). As of now the health regain by a high enough food level is solved the following way: the mean of the food level is calculated by all players that are online and assigned to the same health bar. If the mean exceeds a food level of 18 (the amount where a player usually regains health), all players of the health bar regenerate their health.

## Commands

**/createHB**
- the command requires one argument, which is the name of the health bar (must be unique)
- creates the health bar (with full health starting)

**/deleteHB**
- the command requires one argument, which is the name of the health bar
- deletes the health bar (removes players aswell, so they can be added to another health bar)

**/addHB**
- the command requires two arguments, which are the name of the player (must be online) and the name of the health bar
- adds the player to the health bar and sets his health to the health of the health bar
- note that every player can only be assigned to one health bar

**/removeHB**
- the command requires one argument, which is the name of a player (is allowed to be offline)
- removes the player from his health bar

**/listHB**
- the command requires one argument, which is the name of the health bar
- lists all the players that share this health bar (are allowed to be offline)

## Installation

In the project folder "SyncedHealthBar/target/" is a compiled .jar file, which you can simply drag in your plugins folder of your server. Reload the server and the console should say: "SyncedHealthBar has been enabled!". Also in your plugins folder should be a "SyncedHealthBar" folder generated which holds the config.yml. It is recommended that you dont make any changes to that file.


## Problems

As of now there are no known bugs. However when a player does not respawn instantly, other players assigned to his health bar take damage and then he respawns it takes a small delay until his health is synchronized. This is due to how the respawn event works in Bukkit/Spigot. The event sets at the end the health of the player to full health which means setHealth() always gets overwritten. In order to fix that problem a Thread is started which tracks the location of the player and sets his health when he actually respawns on the map.
