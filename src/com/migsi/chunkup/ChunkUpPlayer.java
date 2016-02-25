package com.migsi.chunkup;

import org.bukkit.entity.Player;

public class ChunkUpPlayer {

	private static int IgnoreCount = 10;

	private Player player = null;
	private long route = -1;
	private boolean mark = false;
	private int chunkcount = 0;
	private int ignore = 0;

	public ChunkUpPlayer(Player player, boolean mark) {
		this.player = player;
		this.mark = mark;
	}

	public ChunkUpPlayer(Player player, long route, boolean mark) {
		this.player = player;
		this.route = route;
		this.mark = mark;
	}

	public Player getPlayer() {
		return player;
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

	public boolean equals(Player player) {
		return this.player.equals(player);
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
