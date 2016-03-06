package com.migsi.chunkup;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkData {

	private static long NextID = 0;
	private static long NextRoute = 0;
	private final long ID;
	private long route;
	private Vector<String> owners = null;
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
			route = NextRoute;
			NextID++;
			NextRoute++;
		} else {
			ID = -1;
		}

		owners = new Vector<String>();
		owners.add(sender.getName());

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(CommandSender sender, long route) {
		ID = NextID;
		NextID++;
		if (route > -1) {
			this.route = route;
		} else {
			this.route = NextRoute;
			NextRoute++;
		}
		owners = new Vector<String>();
		owners.add(sender.getName());

		Location loc = ((Player) sender).getLocation();
		world = loc.getWorld().getName();
		x = loc.getChunk().getX();
		z = loc.getChunk().getZ();
	}

	public ChunkData(String[] data) throws NumberFormatException, ArrayIndexOutOfBoundsException, NullPointerException {
		ID = Integer.parseInt(data[0]);
		route = Integer.parseInt(data[1]);
		
		owners = new Vector<String>();
		if (data[2].indexOf(',') > -1) {
			String[] owners = data[2].split(",");
			for (String owner : owners) {
				addOwner(owner);
			}
		} else {
			addOwner(data[2]);
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

	public static long getNextRoute() {
		return NextRoute;
	}

	public static long getNextRouteInc() {
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

	public Vector<String> getOwners() {
		return owners;
	}

	public String getMainOwner() {
		return owners.firstElement();
	}

	public int ownerCount() {
		return owners.size();
	}

	public boolean addOwner(String owner) {
		return owners.add(owner);
	}

	public boolean removeOwner(String owner) {
		return owners.remove(owner);
	}

	public boolean isOwner(String owner) {
		return owners.contains(owner);
	}

	public boolean isOnlyOwner(String owner) {
		return (owners.size() == 1 && isOwner(owner));
	}

	public long getRoute() {
		return route;
	}

	public void setRoute(long route) {
		this.route = route;
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
		return ID + ";" + route + ";" + owner + ";" + world + ";" + x + ";" + z;
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
			owner = "Nobody";
		}

		return ChatColor.BOLD + "Owner: " + ChatColor.RESET + ChatColor.GRAY + owner + ChatColor.RESET + ChatColor.BOLD
				+ " Route: " + ChatColor.RESET + ChatColor.GRAY + route + ChatColor.RESET + ChatColor.BOLD + "\nWorld: "
				+ ChatColor.RESET + ChatColor.GRAY + world + ChatColor.RESET + ChatColor.BOLD + " X: " + ChatColor.RESET
				+ ChatColor.GRAY + x + ChatColor.RESET + ChatColor.BOLD + " Z: " + ChatColor.RESET + ChatColor.GRAY + z
				+ ChatColor.RESET;
	}
}
