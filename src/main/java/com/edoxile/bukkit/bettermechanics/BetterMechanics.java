package com.edoxile.bukkit.bettermechanics;

import com.edoxile.bukkit.bettermechanics.exceptions.ConfigWriteException;
import com.edoxile.bukkit.bettermechanics.listeners.MechanicsBlockListener;
import com.edoxile.bukkit.bettermechanics.listeners.MechanicsPlayerListener;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class BetterMechanics extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	private MechanicsPlayerListener playerListener;
	private MechanicsBlockListener blockListener;
	private MechanicsConfig configManager;
	private File configFile;

	public void onDisable() {
		log.info("[BetterMechanics] disabled.");
	}

	public void onEnable() {
		try {
			configFile = this.getFile();
			configManager = new MechanicsConfig(this);
			blockListener = new MechanicsBlockListener(configManager);
			playerListener = new MechanicsPlayerListener(configManager);
			registerEvents();
			log.info("[BetterMechanics] Loading completed.");
		} catch (ConfigWriteException ex) {
			log.severe("[BetterMechanics] Couldn't create config file.");
			this.setEnabled(false);
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	public String getName() {
		return getDescription().getName();
	}

	public void registerEvents() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Event.Priority.Normal, this);
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("bettermechanics")) {
			if (sender instanceof Player
					&& !configManager.permissionConfig.checkPermissions(
							(Player) sender, "bettermechanics.reloadconfig"))
				return true;
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "I need to know what to do!");
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (args.length == 1) {
					try {
						configManager = new MechanicsConfig(this);
						playerListener.setConfig(configManager);
						blockListener.setConfig(configManager);
						if (sender instanceof Player)
							sender.sendMessage(ChatColor.GOLD
									+ "Reloaded config.");
						log.info("[BetterMechanics] Reloaded config.");
					} catch (ConfigWriteException e) {
						log.severe("[BetterMechanics] Couldn't create config file.");
						this.setEnabled(false);
					}
				} else if (args[1].equalsIgnoreCase("cauldron")) {
					configManager.reloadCauldronConfig();
					playerListener.setConfig(configManager);
					blockListener.setConfig(configManager);
					if (sender instanceof Player)
						sender.sendMessage(ChatColor.GOLD
								+ "Reloaded cauldron recipes.");
					log.info("[BetterMechanics] Reloaded cauldron recipes.");
				} else {
					sender.sendMessage(ChatColor.RED
							+ "Wrong usage. Usage: /bm reload <cauldron>");
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "Wrong usage. Usage: /bm reload <cauldron>");
			}
			return true;
		} else {
			return false;
		}
	}
}
