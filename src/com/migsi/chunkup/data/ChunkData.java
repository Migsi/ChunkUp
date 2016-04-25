package com.migsi.chunkup.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.migsi.chunkup.ChunkUp;

public class ChunkData {

	private static HashMap<ChunkUpPlayer, Integer> ownermap = null;

	private static long NextID = 0;
	private static long NextRoute = 0;

	private final long ID;
	private String description = null;
	private List<ChunkUpPlayer> owners = null;
	private String world = null;
	private int x, z;

	public ChunkData(Chunk chunk) {
		ID = -1;
		world = chunk.getWorld().getName();
		x = chunk.getX();
		z = chunk.getZ();
	}

	public ChunkData(CommandSender sender, boolean remove) {
		if (!remove) {
			ID = NextID;
			description = new String("Route " + Long.toString(NextRoute));
			NextID++;
			NextRoute++;
		} else {
			ID = -1;
		}

		owners = new ArrayList<>();
		owners.add(new ChunkUpPlayer((Player) sender));

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(CommandSender sender, String description, long route) {
		ID = NextID;
		NextID++;
		if (description != null) {
			description = description.replace(';', ':');
			this.description = description;
		} else {
			if (route > -1) {
				this.description = new String("Route " + route);
			} else {
				this.description = new String("Route " + Long.toString(NextRoute));
				NextRoute++;
			}
		}

		owners = new ArrayList<>();
		owners.add(new ChunkUpPlayer((Player) sender));

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(String[] data) throws NumberFormatException, ArrayIndexOutOfBoundsException, NullPointerException {
		ID = Integer.parseInt(data[0]);
		description = data[1];

		// TODO check if player even exists?
		owners = new ArrayList<>();
		if (data[2].indexOf('°') > -1) {
			String[] ownerlist = data[2].split("°");
			for (String owner : ownerlist) {
				owners.add(new ChunkUpPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)).getPlayer()));
			}
		} else {
			owners.add(new ChunkUpPlayer(Bukkit.getOfflinePlayer(UUID.fromString(data[2])).getPlayer()));
		}

		world = data[3];
		x = Integer.parseInt(data[4]);
		z = Integer.parseInt(data[5]);
	}

	public static long getNextID() {
		return NextID;
	}

	public static void setNextID(long NextID) {
		ChunkData.NextID = NextID;
	}

	public long getID() {
		return ID;
	}

	public static long getNextRouteNoInc() {
		return NextRoute;
	}

	public static long getNextRoute() {
		long ret = NextRoute;
		NextRoute++;
		return ret;
	}

	public static void setNextRoute(long NextRoute) {
		ChunkData.NextRoute = NextRoute;
	}

	public static void incCounters() {
		NextRoute++;
	}

	public static HashMap<ChunkUpPlayer, Integer> getOwnerMap() {
		if (ownermap == null) {
			ownermap = new HashMap<>();
		}
		return ownermap;
	}

	public static void clearOwnerMap() {
		ownermap.clear();
	}

	public static void addToMap(final ChunkUpPlayer owner) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (ownermap == null) {
					ownermap = new HashMap<>();
					ChunkUp.verbose(Level.WARNING, "Ownermap was null");
				}
				if (ownermap.containsKey(owner)) {
					ownermap.put(owner, ownermap.get(owner) + 1);
					ChunkUp.verbose(Level.WARNING, "Incremented players value to " + ownermap.get(owner));
				} else {
					ownermap.put(owner, 1);
					ChunkUp.verbose(Level.WARNING, "Added player to map");
				}
			}
		}.runTaskAsynchronously(ChunkUp.instance);
	}

	public static void removeFromMap(final ChunkUpPlayer owner) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (ownermap != null) {
					Integer value = ownermap.get(owner);
					if (value == null) {
						ChunkUp.verbose(Level.WARNING, "Player had no value");
					} else {
						ChunkUp.verbose(Level.WARNING, "Player value was " + value);
					}
					
					if (value == null || value <= 1) {
						ownermap.remove(owner);
						ChunkUp.verbose(Level.WARNING, "Removed player from map");
					} else {
						ownermap.put(owner, value - 1);
						ChunkUp.verbose(Level.WARNING, "Decremented players value to " + (value - 1));
					}
				}
			}
		}.runTaskAsynchronously(ChunkUp.instance);
	}
	
	public static void showOwners() {
		ChunkUp.verbose(Level.WARNING, ownermap.toString());
	}

	public boolean addOwner(ChunkUpPlayer owner) {
		return owners.add(owner);
	}

	public boolean removeOwner(ChunkUpPlayer owner) {
		return owners.remove(owner);
	}

	public List<ChunkUpPlayer> getOwners() {
		return owners;
	}

	public ChunkUpPlayer getMainOwner() {
		if (owners != null && !owners.isEmpty()) {
			return owners.get(0);
		}
		return null;
	}

	public int ownerCount() {
		return owners.size();
	}

	public boolean isOwner(ChunkUpPlayer owner) {
		return owners.contains(owner);
	}

	public boolean isOnlyOwner(ChunkUpPlayer owner) {
		return (owners.size() == 1 && isOwner(owner));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object chdata) {
		if (chdata instanceof ChunkData) {
			if (world.equals(((ChunkData) chdata).getWorld())) {
				if (x == ((ChunkData) chdata).getX()) {
					if (z == ((ChunkData) chdata).getZ()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String toConfString() {
		String owner = new String();
		if (owners != null) {
			for (int i = 0; i < owners.size(); i++) {
				owner += owners.get(i).getUUID().toString();
				if (i < owners.size() - 1) {
					owner += "°";
				}
			}
		} else {
			owner = "";
		}
		return ID + ";" + description + ";" + owner + ";" + world + ";" + x + ";" + z;
	}

	public String toString() {
		String owner = new String();
		if (owners != null) {
			for (int i = 0; i < owners.size(); i++) {
				owner += owners.get(i).getName();
				if (i < owners.size() - 1) {
					owner += ", ";
				}
			}
		} else {
			owner = "Nobody?";
		}

		return ChatColor.BOLD + "Owner: " + ChatColor.RESET + ChatColor.GRAY + owner + ChatColor.RESET + ChatColor.BOLD
				+ "\nDescription: " + ChatColor.RESET + ChatColor.GRAY + description + ChatColor.RESET + ChatColor.BOLD
				+ "\nWorld: " + ChatColor.RESET + ChatColor.GRAY + world + ChatColor.RESET + ChatColor.BOLD + " X: "
				+ ChatColor.RESET + ChatColor.GRAY + x + ChatColor.RESET + ChatColor.BOLD + " Z: " + ChatColor.RESET
				+ ChatColor.GRAY + z + ChatColor.RESET;
	}
}
