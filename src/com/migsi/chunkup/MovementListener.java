package com.migsi.chunkup;

import java.util.Vector;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

	private ChunkUp chunkUp = null;
	private Vector<ChunkUpPlayer> following = null;

	public MovementListener(ChunkUp chunkUp) {
		this.chunkUp = chunkUp;
		following = new Vector<ChunkUpPlayer>();
	}

	public boolean add(Player player, boolean mark, int route) {
		if (!contains(player)) {
			if (mark) {
				if (route > -1) {
					return following.add(new ChunkUpPlayer(player, route, mark));
				}
				return following.add(new ChunkUpPlayer(player, ChunkData.getNextRouteInc(), mark));
			}
			return following.add(new ChunkUpPlayer(player, mark));
		}
		return false;
	}

	public boolean remove(Player player) {
		ChunkUpPlayer cuplayer = get(player);
		if (cuplayer != null) {
			if (cuplayer.isMarking()) {
				ChunkUp.message(cuplayer.getChunkCount() + " chunks marked by " + cuplayer.getPlayer().getName());
			} else {
				ChunkUp.message(cuplayer.getChunkCount() + " chunks demarked by " + cuplayer.getPlayer().getName());
			}
			return following.remove(cuplayer);
		}
		return false;
	}

	public ChunkUpPlayer get(Player player) {
		for (int i = 0; i < following.size(); i++) {
			if (following.get(i).equals(player)) {
				return following.get(i);
			}
		}
		return null;
	}

	public boolean contains(Player player) {
		for (int i = 0; i < following.size(); i++) {
			if (following.get(i).equals(player)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!following.isEmpty()) {
			ChunkUpPlayer cuplayer = get(event.getPlayer());
			if (cuplayer != null && !cuplayer.Ignore()) {
				if (chunkUp.automark(cuplayer)) {
					cuplayer.incrementChunkCount();
				}
			}
		}
	}
}
