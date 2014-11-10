package net.SimpleSurvival;

import net.SimpleSurvival.settings.WorldSettings;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.World;

import java.util.HashMap;

/**
 * Created by maldridge on 11/8/14.
 */
public class SimpleSurvival extends JavaPlugin {
	CommandParser commandParser = new CommandParser();
	SpawnManager spawnManager = new SpawnManager();
	HashMap<String, WorldSettings> worldSettings = new HashMap<String, WorldSettings>();

	public SimpleSurvival() {
		for(World world : getServer().getWorlds()) {
			worldSettings.put(world.getName(), new WorldSettings());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return commandParser.parseCommand(sender, cmd, label, args);
	}

	private class CommandParser {
		public boolean parseCommand(CommandSender sender, Command cmd, String label, String[] args) {
			String cmdName = cmd.getName();
			if(cmdName.equalsIgnoreCase("addSpawn")) {
				return addSpawn((Player)sender);
			} else if(cmdName.equalsIgnoreCase("changeSpawn")) {
				if (args.length != 1) return false;
				return setSpawn((Player)sender, args[0]);
			} else if(cmdName.equalsIgnoreCase("getSpawns")) {
				return getSpawns((Player)sender);
			}
			return false;
		}

		private boolean addSpawn(Player player) {
			String worldName = player.getLocation().getWorld().getName();
			boolean returnValue;
			returnValue = spawnManager.addSpawn(worldSettings.get(worldName), player.getLocation());
			if(returnValue == true) {
				player.sendMessage("Successfully added spawn.");
			}
			return returnValue;
		}

		private boolean setSpawn(Player player, String arg) {
			String worldName = player.getLocation().getWorld().getName();
			int spawnNum;
			try {
				spawnNum = Integer.valueOf(arg);
				--spawnNum;
			} catch(NumberFormatException e) {
				return false;
			}

			if(worldSettings.containsKey(worldName)) {
				WorldSettings settings = worldSettings.get(worldName);
				if(spawnNum < settings.spawns.size()) {
					boolean returnValue;
					returnValue = spawnManager.setSpawn(settings, spawnNum, player.getLocation());
					if(returnValue == true) {
						player.sendMessage("Spawn " + (spawnNum + 1) + " successfully changed.");
					}
					return returnValue;
				} else {
					player.sendMessage("Not enough spawn points already exist.");
					return true;
				}
			} else {
				player.sendMessage("World settings do not exist.");
				return true;
			}
		}

		private boolean getSpawns(Player player) {
			String worldName = player.getLocation().getWorld().getName();
			WorldSettings settings = worldSettings.get(worldName);
			int i = 1;
			for(Integer[] spawn : settings.spawns) {
				player.sendMessage("Spawn " + i++ + ":");
				player.sendMessage("   x: " + spawn[0]);
				player.sendMessage("   y: " + spawn[1]);
				player.sendMessage("   z: " + spawn[2]);
			}
			return true;
		}
	}
}
