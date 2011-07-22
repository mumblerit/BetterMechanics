package com.edoxile.bukkit.bettermechanics;

import com.edoxile.bukkit.bettermechanics.exceptions.ConfigWriteException;
import com.edoxile.bukkit.bettermechanics.listeners.MechanicsBlockListener;
import com.edoxile.bukkit.bettermechanics.listeners.MechanicsPlayerListener;
import com.edoxile.bukkit.bettermechanics.mechanics.Pen;
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
 * Created by IntelliJ IDEA.
 * User: Edoxile
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
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pen")) {
            if (configManager.getPenConfig().enabled) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.DARK_RED + "Incorrect usage. Usage: /pen <set|clear|setline|help>");
                    } else {
                        Pen pen = new Pen();
                        if (args[0].equalsIgnoreCase("set")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                pen.setLines(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("clear")) {
                            pen.clear(player);
                            player.sendMessage(ChatColor.GOLD + "Pen data cleared.");
                        } else if (args[0].equalsIgnoreCase("dump")) {
                            pen.dump(player);
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 3) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                pen.setLine(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                pen.clearLine(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("help")) {
                            player.sendMessage("Pen help. The char '^' is a linebreak. Commands:");
                            player.sendMessage("/pen set <text> | set the sign text");
                            player.sendMessage("/pen setline <line> <text> | set one line of the text");
                            player.sendMessage("/pen clearline <line> | clears the specified line");
                            player.sendMessage("/pen clear | clears the current text");
                            player.sendMessage("/pen dump | dumps the current text");
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "Incorrect usage. Usage: /pen <set|clear>|setline|help>");
                        }
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Consoles aren't allowed in this pen party club!");
                }
            }
            sender.sendMessage(ChatColor.DARK_RED + "The pen is not enabled.");
            return true;
        } else if (command.getName().equalsIgnoreCase("bettermechanics")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "I need to know what to do!");
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (args.length == 1) {
                    try {
                        configManager = new MechanicsConfig(this);
                        playerListener.setConfig(configManager);
                        blockListener.setConfig(configManager);
                        if (sender instanceof Player)
                            sender.sendMessage(ChatColor.GOLD + "Reloaded config.");
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
                        sender.sendMessage(ChatColor.GOLD + "Reloaded cauldron recipes.");
                    log.info("[BetterMechanics] Reloaded cauldron recipes.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong usage. Usage: /bm reload <cauldron>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Wrong usage. Usage: /bm reload <cauldron>");
            }
            return true;
        } else {
            return false;
        }
    }
}
