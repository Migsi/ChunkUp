package com.migsi.chunkup.data;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.migsi.chunkup.ChunkUp;

public class ChunkUpPlayer {

	private static int IgnoreCount = 10;

	private UUID uuid = null;
	private String description = null;
	private long route = -1;
	private boolean mark = false;
	private int chunkcount = 0;
	private int ignore = 0;

	public ChunkUpPlayer(Player player) {
		this.uuid = player.getUniqueId();
		ChunkUp.verbose(Level.WARNING, "UUID of this player: " + uuid.toString());
	}

	public ChunkUpPlayer(Player player, boolean mark) {
		this.uuid = player.getUniqueId();
		this.route = ChunkData.getNextRoute();
		this.mark = mark;
	}

	public ChunkUpPlayer(Player player, String description, boolean mark) {
		this.uuid = player.getUniqueId();
		if (description != null) {
			this.description = description;
		} else {
			this.route = ChunkData.getNextRoute();
		}
		this.mark = mark;
	}

	public UUID getUUID() {
		return uuid;
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public String getName() {
		return Bukkit.getPlayer(uuid).getName();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getRoute() {
		return route;
	}

	public void setRoute(long route) {
		this.route = route;
	}

	public boolean isMarking() {
		return mark;
	}

	public void incrementChunkCount() {
		chunkcount++;
	}

	public int getChunkCount() {
		return chunkcount;
	}

	public boolean Ignore() {
		if (ignore > IgnoreCount) {
			ignore = 0;
			return false;
		}
		ignore++;
		return true;
	}

	@Override
	public boolean equals(Object player) {
		if (player instanceof ChunkUpPlayer) {
			ChunkUp.verbose(Level.WARNING, "Compared uuid's");
			return this.uuid.equals(((ChunkUpPlayer) player).getUUID());
		} else {
			ChunkUp.verbose(Level.WARNING, "Not compared uuid's!");
			return false;
		}
	}

	public String toConfString() {
		return uuid.toString();
	}

	public static void setIgnoreCount(int count) {
		if (count > -1) {
			IgnoreCount = count;
		}
	}

	public static int getIgnoreCount() {
		return IgnoreCount;
	}

}
