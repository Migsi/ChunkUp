package com.migsi.chunkup.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.migsi.chunkup.ChunkUp;

public class ChunkData {

	private static HashMap<String, Integer> ownermap = null;

	private static long NextID = 0;
	private static long NextRoute = 0;

	private final long ID;
	private String description = null;
	private List<String> owners = null;
	private String world = null;
	private int x, z;

	public ChunkData(Chunk chunk) {
		ID = -1;
		world = chunk.getWorld().getName();
		x = chunk.getX();
		z = chunk.getZ();
	}

	public ChunkData(CommandSender sender, boolean remove) {
		if (ownermap == null) {
			ownermap = new HashMap<>();
		}
		if (!remove) {
			ID = NextID;
			description = new String("Route " + Long.toString(NextRoute));
			NextID++;
			NextRoute++;
		} else {
			ID = -1;
		}
		
		owners = new ArrayList<>();
		owners.add(sender.getName());

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(CommandSender sender, String description, long route) {
		if (ownermap == null) {
			ownermap = new HashMap<>();
		}
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
		owners.add(sender.getName());

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(String[] data) throws NumberFormatException, ArrayIndexOutOfBoundsException, NullPointerException {
		if (ownermap == null) {
			ownermap = new HashMap<>();
		}
		ID = Integer.parseInt(data[0]);
		description = data[1];

		owners = new ArrayList<>();
		if (data[2].indexOf(',') > -1) {
			String[] ownerlist = data[2].split(",");
			for (String owner : ownerlist) {
				owners.add(owner);
			}
		} else {
			owners.add(data[2]);
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

	public static HashMap<String, Integer> getOwnerMap() {
		return ownermap;
	}

	public static void clearOwnerMap() {
		ownermap.clear();
	}
	
	public static void addToMap(final String owner) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (ownermap.containsKey(owner)) {
					ownermap.put(owner, ownermap.get(owner) + 1);
				} else {
					ownermap.put(owner, 1);
				}
			}
		}.runTaskAsynchronously(ChunkUp.instance);
	}
	
	public static void removeFromMap(final String owner) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Integer value = ownermap.get(owner);
				if (value == null || value <= 1) {
					ownermap.remove(owner);
				} else {
					ownermap.put(owner, value - 1);
				}
			}
		}.runTaskAsynchronously(ChunkUp.instance);
	}
	
	public boolean addOwner(String owner) {
		return owners.add(owner);
	}
	
	public boolean removeOwner(String owner) {
		return owners.remove(owner);
	}

	public List<String> getOwners() {
		return owners;
	}

	public String getMainOwner() {
		if (owners != null && !owners.isEmpty()) {
			return owners.get(0);
		}
		return null;
	}

	public int ownerCount() {
		return owners.size();
	}

	public boolean isOwner(String owner) {
		return owners.contains(owner);
	}

	public boolean isOnlyOwner(String owner) {
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
		boolean ret = false;
		if (chdata instanceof ChunkData) {
			if (world.equals(((ChunkData) chdata).getWorld())) {
				if (x == ((ChunkData) chdata).getX()) {
					if (z == ((ChunkData) chdata).getZ()) {
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	public String toConfString() {
		String owner = new String();
		if (owners != null) {
			for (int i = 0; i < owners.size(); i++) {
				owner += owners.get(i);
				if (i < owners.size() - 1) {
					owner += ",";
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
				owner += owners.get(i);
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
