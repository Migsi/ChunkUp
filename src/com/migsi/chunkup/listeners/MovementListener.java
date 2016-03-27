package com.migsi.chunkup.listeners;

import java.util.Vector;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.migsi.chunkup.ChunkUp;
import com.migsi.chunkup.data.ChunkData;
import com.migsi.chunkup.data.ChunkDataVector;
import com.migsi.chunkup.data.ChunkUpPlayer;

public class MovementListener implements Listener {

	private Vector<ChunkUpPlayer> following = null;

	public MovementListener() {
		following = new Vector<ChunkUpPlayer>();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!following.isEmpty()) {
			ChunkUpPlayer cuplayer = get(event.getPlayer());
			if (cuplayer != null && !cuplayer.Ignore()) {
				if (automark(cuplayer)) {
					cuplayer.incrementChunkCount();
				}
			}
		}
	}

	public boolean add(Player player, boolean mark, String description) {
		if (!contains(player)) {
			// if (description != null) {
			return following.add(new ChunkUpPlayer(player, description, mark));
			// }
			// return following.add(new ChunkUpPlayer(player, mark));
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

	private boolean automark(ChunkUpPlayer player) {
		if (player.isMarking()) {
			if (ChunkDataVector.add(new ChunkData(player.getPlayer(), player.getDescription(), player.getRoute()))) {
				ChunkData.addToMap(player.getPlayer().getName());
				return true;
			}
			return false;
		}
		if (ChunkDataVector.remove(new ChunkData(player.getPlayer(), true))) {
			ChunkData.removeFromMap(player.getPlayer().getName());
			return true;
		}
		return false;
	}
}
