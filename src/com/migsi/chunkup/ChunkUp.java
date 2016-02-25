package com.migsi.chunkup;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkUp extends JavaPlugin {

	// Is verbose active
	private static boolean verbose = false;
	// Determine loading algorithm
	private static boolean useAlternativeChunkLoader = true;
	// MovementListener for continuous marking
	private MovementListener movementListener = null;
	// ChunkLoader keeps chunks up
	private ChunkLoader loader = null;
	// Config
	private Config config = null;

	public void onEnable() {
		getLogger().info("Initializing ChunkUp...");
		
		// Setting up config config
		config = new Config(this);
		// Setting up loader
		loader = new ChunkLoader(config.readConfig());
		config.resetTemp();
		if (useAlternativeChunkLoader) {
			getServer().getPluginManager().registerEvents(loader, this);
		}
		// Setting up MovementListener
		movementListener = new MovementListener(this);
		getServer().getPluginManager().registerEvents(movementListener, this);

		getLogger().info("ChunkUp was succesfully enabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean ret = true;
		if (cmd.getName().equalsIgnoreCase("chunkup")) {

			// Check for needed arguments
			if (args.length > 0) {
				if (sender instanceof Player) {

					// Check arguments
					switch (args[0].toLowerCase()) {
					case "help":
						ret = !checkPermission(sender, Permissions.Help);
						break;
					case "info":
						if (checkPermission(sender, Permissions.Info)) {
							info(sender);
						}
						break;
					case "mark":
						if (checkPermission(sender, Permissions.Mark)) {
							mark(sender, args);
						}
						break;
					case "follow":
						if (checkPermission(sender, Permissions.Follow)) {
							followMark(sender, args);
						}
						break;
					case "escape":
						if (checkPermission(sender, Permissions.Escape)) {
							escape(sender);
						}
						break;
					case "unmark":
						if (checkPermission(sender, Permissions.Unmark)) {
							unmark(sender);
						}
						break;
					case "unmarkall":
						if (checkPermission(sender, Permissions.Unmarkall_Own)) {
							unmarkall(sender, args);
						}
						break;
					case "list":
						if (checkPermission(sender, Permissions.List)) {
							list(sender);
						}
						break;
					case "set":
						if (checkPermission(sender, Permissions.Set)) {
							changeConfig(sender, args);
						}
						break;
					case "get":
						if (checkPermission(sender, Permissions.Get)) {
							changeConfig(sender, args);
						}
						break;
					default:
						sender.sendMessage(ChatColor.DARK_RED + "Sorry, I don't know that command!" + ChatColor.RESET);
						ret = false;
					}
				} else {

					// If the user is on a console
					switch (args[0].toLowerCase()) {
					case "help":
						ret = false;
						break;
					case "unmarkall":
						unmarkall(sender, args);
						break;
					case "list":
						list(sender);
						break;
					case "set":
						changeConfig(sender, args);
						break;
					case "get":
						changeConfig(sender, args);
						break;
					default:
						sender.sendMessage(ChatColor.DARK_RED
								+ "Sorry, I think this command doesn't exist or you can't run it from a console!"
								+ ChatColor.RESET);
						ret = false;
					}
				}

				// If no arguments were given
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You need to tell me some arguments!" + ChatColor.RESET);
				ret = false;
			}
		}
		return ret;
	}

	public void onDisable() {
		if (!useAlternativeChunkLoader) {
			loader.cancel();
		}
		HandlerList.unregisterAll(movementListener);
		HandlerList.unregisterAll(loader);
		config.writeConfig(loader.getChunkDataVector());
	}

	// Command-Routines

	/**
	 * Gives the player some information about the chunk he's into.
	 * 
	 * @param sender
	 */
	public void info(CommandSender sender) {
		ChunkData chdata = loader.getChunkDataVector().get(new ChunkData(((Player) sender).getLocation().getChunk()));
		if (chdata != null) {
			sender.sendMessage(chdata.toString());
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Not marked!" + ChatColor.RESET + ChatColor.BOLD + " World: "
					+ ChatColor.RESET + ChatColor.GRAY + ((Player) sender).getLocation().getChunk().getWorld().getName()
					+ ChatColor.RESET + ChatColor.BOLD + " X: " + ChatColor.RESET + ChatColor.GRAY
					+ ((Player) sender).getLocation().getChunk().getX() + ChatColor.RESET + ChatColor.BOLD + " Z: "
					+ ChatColor.RESET + ChatColor.GRAY + ((Player) sender).getLocation().getChunk().getZ()
					+ ChatColor.RESET);
		}
	}

	/**
	 * Marks the players chunk to stay loaded. Optional route selection
	 * 
	 * @param sender
	 * @param args
	 */
	public void mark(CommandSender sender, String[] args) {
		if (loader.add(new ChunkData(sender, convertToInt(sender, args, 2)))) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "The chunk was marked" + ChatColor.RESET);
		} else {
			sender.sendMessage(ChatColor.DARK_PURPLE + "This chunk was already marked" + ChatColor.RESET);
		}
	}

	/**
	 * Marks automatically every chunk the player travels into. This Method is
	 * only used by Class MovementListener (PlayerMoveEvent Handler)
	 * 
	 * @param player
	 * @return true if action performed correctly
	 */
	public boolean automark(ChunkUpPlayer player) {
		if (player.isMarking()) {
			return loader.add(new ChunkData(player.getPlayer(), player.getRoute()));
		}
		return loader.remove(new ChunkData(player.getPlayer(), true));
	}

	/**
	 * Marks all chunks the player travels into until he tells the plugin to
	 * stop
	 * 
	 * @param sender
	 */
	public void followMark(CommandSender sender, String[] args) {
		if (args.length >= 2) {
			int route = convertToInt(sender, args, 3);
			switch (args[1].toLowerCase()) {
			case "mark":
				if (movementListener.add((Player) sender, true, route)) {
					sender.sendMessage(ChatColor.DARK_PURPLE + "I'm following you now" + ChatColor.RESET);
				} else {
					sender.sendMessage(ChatColor.DARK_PURPLE + "I'm already following you!" + ChatColor.RESET);
				}
				break;
			case "unmark":
				if (movementListener.add((Player) sender, false, route)) {
					sender.sendMessage(ChatColor.DARK_PURPLE + "I'm following you now" + ChatColor.RESET);
				} else {
					sender.sendMessage(ChatColor.DARK_PURPLE + "I'm already following you!" + ChatColor.RESET);
				}
				break;
			default:
				sender.sendMessage(
						ChatColor.DARK_RED + "Do you want to " + ChatColor.RESET + "mark" + ChatColor.DARK_RED + " or "
								+ ChatColor.RESET + "unmark" + ChatColor.DARK_RED + " chunks?" + ChatColor.RESET);
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Do you want to " + ChatColor.RESET + "mark" + ChatColor.DARK_RED
					+ " or " + ChatColor.RESET + "unmark" + ChatColor.DARK_RED + " chunks?" + ChatColor.RESET);
		}

	}

	/**
	 * Stops plugin to follow player
	 * 
	 * @param sender
	 */
	public void escape(CommandSender sender) {
		if (movementListener.remove((Player) sender)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "I've lost your trace" + ChatColor.RESET);
		} else {
			sender.sendMessage(ChatColor.DARK_PURPLE + "I wasn't following you!" + ChatColor.RESET);
		}
	}

	/**
	 * Unmarks the players chunk to stay loaded
	 * 
	 * @param sender
	 */
	public void unmark(CommandSender sender) {
		// TODO überprüfen ob spieler alle entfernen darf?
		if (loader.remove(new ChunkData(sender, true))) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "The chunk was unmarked" + ChatColor.RESET);
		} else {
			sender.sendMessage(ChatColor.DARK_PURPLE + "This chunk is currently not marked" + ChatColor.RESET);
		}
	}

	/**
	 * Unmarks all chunks
	 * 
	 * @param sender
	 */
	public void unmarkall(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				if (loader.clear(sender.getName())) {
					if (ChunkDataVector.isUsingOwners()) {
						sender.sendMessage(ChatColor.DARK_PURPLE + "All your chunks have been unmarked" + ChatColor.RESET);
					} else {
						sender.sendMessage(ChatColor.DARK_PURPLE + "All chunks have been unmarked" + ChatColor.RESET);
					}
				} else {
					sender.sendMessage(ChatColor.DARK_PURPLE + "You had no chunks marked!" + ChatColor.RESET);
				}
			} else if (args.length == 2
					&& (sender.getName().equals(args[1]) || checkPermission(sender, Permissions.Unmarkall))) {
				if (loader.clear(args[1])) {
					sender.sendMessage(
							ChatColor.DARK_PURPLE + "Deleted chunks of player: " + ChatColor.RESET + args[1]);
				} else {
					sender.sendMessage(ChatColor.DARK_PURPLE + args[1] + " had no chunks marked!" + ChatColor.RESET);
				}
			}
		} else {
			if (args.length == 2 && loader.clear(args[1])) {
				sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted chunks of player: " + ChatColor.RESET + args[1]);
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You need to tell me a player!" + ChatColor.RESET);
			}
		}
	}

	/**
	 * Gives a list of all marked chunks
	 * 
	 * @param sender
	 */
	public void list(CommandSender sender) {
		String list = loader.getChunkDataVector().list();
		if (list != null) {
			sender.sendMessage(list);
		} else {
			sender.sendMessage(ChatColor.DARK_PURPLE + "No chunks are currently marked" + ChatColor.RESET);
		}
	}

	/**
	 * Changes the given configuration to the given value is used with "set"
	 * command, otherwise tells values of the current configuration with
	 * "get" command.
	 * 
	 * @param sender
	 * @param args
	 */
	public void changeConfig(CommandSender sender, String[] args) {
		if (args.length > 1) {
			// Determine subcommand
			switch (args[0].toLowerCase()) {
			case "set":
				// Determine options
				switch (args[1].toLowerCase()) {
				case "ignoreinterval":
					ChunkUpPlayer.setIgnoreCount(convertToInt(sender, args, 2));
					sender.sendMessage(ChatColor.DARK_PURPLE + "Ignore interval was set to: " + ChatColor.RESET
							+ ChunkUpPlayer.getIgnoreCount());
					break;
				case "refreshtime":
					ChunkLoader.setRefreshTime(convertToInt(sender, args, 2));
					sender.sendMessage(ChatColor.DARK_PURPLE + "Refresh time was set to: " + ChatColor.RESET
							+ ChunkLoader.getRefreshTime());
					break;
				case "altchunkloader":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Using alternative ChunkLoader: " + ChatColor.RESET
							+ toggleAlternativeChunkLoader(convertToBoolean(sender, args, 2)));
					break;
				case "owners":
					ChunkDataVector.setUseOwners(convertToBoolean(sender, args, 2));
					sender.sendMessage(ChatColor.DARK_PURPLE + "Chunk owners enabled: " + ChatColor.RESET
							+ ChunkDataVector.isUsingOwners());
					break;
				default:
					sender.sendMessage(ChatColor.DARK_RED + "I don't know that setting!" + ChatColor.RESET);
				}
				break;
			case "get":
				// Determine options
				switch (args[1].toLowerCase()) {
				case "ignoreinterval":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Ignore interval is set to: " + ChatColor.RESET
							+ ChunkUpPlayer.getIgnoreCount());
					break;
				case "refreshtime":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Refresh time is set to: " + ChatColor.RESET
							+ ChunkLoader.getRefreshTime());
					break;
				case "altchunkloader":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Using alternative ChunkLoader: " + ChatColor.RESET
							+ isUseAlternativeChunkLoader());
					break;
				case "owners":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Chunk owners enabled: " + ChatColor.RESET
							+ ChunkDataVector.isUsingOwners());
					break;
				case "info":
					sender.sendMessage(ChatColor.DARK_PURPLE + "Ignore interval is set to: " + ChatColor.RESET
							+ ChunkUpPlayer.getIgnoreCount());
					sender.sendMessage(ChatColor.DARK_PURPLE + "Refresh time is set to: " + ChatColor.RESET
							+ ChunkLoader.getRefreshTime());
					sender.sendMessage(ChatColor.DARK_PURPLE + "Using alternative ChunkLoader: " + ChatColor.RESET
							+ isUseAlternativeChunkLoader());
					sender.sendMessage(ChatColor.DARK_PURPLE + "Chunk owners enabled: " + ChatColor.RESET
							+ ChunkDataVector.isUsingOwners());
					break;
				default:
					sender.sendMessage(ChatColor.DARK_RED + "I don't know that setting!" + ChatColor.RESET);
				}
				break;
			default:
				sender.sendMessage(ChatColor.DARK_RED + "I don't know what you want to do!" + ChatColor.RESET);
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "You need to tell me some more arguments!" + ChatColor.RESET);
		}
	}

	// Other methods

	/**
	 * Toggles the alternative chunk loading algorithm
	 * 
	 * @param value
	 *            true/false
	 * @return true if enabled
	 */
	public boolean toggleAlternativeChunkLoader(boolean value) {
		if (useAlternativeChunkLoader != value) {
			useAlternativeChunkLoader = value;
			if (useAlternativeChunkLoader) {
				loader.cancel();
				getServer().getPluginManager().registerEvents(loader, this);
				verbose(Level.CONFIG, "Toggled to use event based chunk loading");
			} else {
				HandlerList.unregisterAll(loader);
				config.writeConfig(loader.getChunkDataVector());
				loader = new ChunkLoader(config.loadConfig());
				verbose(Level.CONFIG, "Toggled to use time based chunk loading");
			}
		}
		return useAlternativeChunkLoader;
	}

	/**
	 * Converts an argument to integer
	 * 
	 * @param sender
	 * @param args
	 * @param position
	 * @return value. -1 if no route was given.
	 */
	private int convertToInt(CommandSender sender, String[] args, int position) {
		int ret = -1;
		if (args.length == position + 1) {
			try {
				ret = Integer.parseInt(args[position]);
			} catch (NumberFormatException e) {
				sender.sendMessage(
						ChatColor.DARK_RED + "You need to tell me a value in number format!" + ChatColor.RESET);
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.DARK_RED + "You need to tell me a value!" + ChatColor.RESET);
			}
		}
		return ret;
	}

	/**
	 * Converts an argument to boolean
	 * 
	 * @param sender
	 * @param args
	 * @param position
	 * @return true if argument equals "1/yes/true/on"
	 */
	private boolean convertToBoolean(CommandSender sender, String args[], int position) {
		boolean ret = false;
		try {
			if ("1".equalsIgnoreCase(args[position]) || "yes".equalsIgnoreCase(args[position])
					|| "true".equalsIgnoreCase(args[position]) || "on".equalsIgnoreCase(args[position])) {
				ret = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.DARK_RED + "You need to tell me a value!" + ChatColor.RESET);
		}
		return ret;
	}

	/**
	 * Checks if sender has the given permission
	 * 
	 * @param sender
	 * @return true if player has permission
	 */
	public boolean checkPermission(CommandSender sender, String permission) {
		if (!sender.hasPermission(permission) && Config.Permissions || !sender.isOp() && Config.Op) {
			sender.sendMessage(
					ChatColor.DARK_RED + "Sorry, you don't have the permission to use this command!" + ChatColor.RESET);
			return false;
		}
		return true;
	}

	/**
	 * Broadcasts a message
	 * 
	 * @param msg
	 *            Message
	 */
	public static void message(String msg) {
		Bukkit.broadcastMessage(msg);
	}

	/**
	 * Used for further information output
	 * 
	 * @param msg
	 *            Message
	 */
	public static void verbose(Level level, String msg) {
		if (verbose) {
			getPlugin(ChunkUp.class).getLogger().log(level, msg);
		}
	}

	public static boolean isUseAlternativeChunkLoader() {
		return useAlternativeChunkLoader;
	}

	public static void setUseAlternativeChunkLoader(boolean useAlternativeChunkLoader) {
		ChunkUp.useAlternativeChunkLoader = useAlternativeChunkLoader;
	}

	public static boolean isVerbose() {
		return verbose;
	}

	public static void setVerbose(boolean verbose) {
		ChunkUp.verbose = verbose;
	}

}
