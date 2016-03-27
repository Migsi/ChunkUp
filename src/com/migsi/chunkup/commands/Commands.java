package com.migsi.chunkup.commands;

import java.util.ArrayList;
import java.util.List;

import com.migsi.chunkup.data.ChunkData;

public class Commands {

	public static final String CHUNKUP = "chunkup";
	public static final String HELP = "help";
	public static final String INFO = "info";
	public static final String LIST = "list";
	public static final String MARK = "mark";
	public static final String UNMARK = "unmark";
	public static final String UNMARKALL = "unmarkall";
	public static final String FOLLOW = "follow";
	public static final String ESCAPE = "escape";
	public static final String SET = "set";
	public static final String GET = "get";

	public static final String IGNOREINTERVAL = "ignoreInterval";
	public static final String REFRESHTIME = "refreshTime";
	public static final String ALTCHUNKLOADER = "altChunkLoader";
	public static final String OWNERS = "owners";

	public static List<String> getCommands(String topcmd) {
		List<String> commands = new ArrayList<>();
		switch (topcmd) {
		case CHUNKUP:
			commands.add(HELP);
			commands.add(INFO);
			commands.add(LIST);
			commands.add(MARK);
			commands.add(UNMARK);
			commands.add(UNMARKALL);
			commands.add(FOLLOW);
			commands.add(ESCAPE);
			commands.add(SET);
			commands.add(GET);
			break;
		case FOLLOW:
			commands.add(MARK);
			commands.add(UNMARK);
			break;
		case UNMARKALL:
			commands.add("ALL");
			if (ChunkData.getOwnerMap() != null) {
				for (String owner : ChunkData.getOwnerMap().keySet()) {
					commands.add(owner);
				}
			}
			break;
		case SET:
		case GET:
			commands.add(IGNOREINTERVAL);
			commands.add(REFRESHTIME);
			commands.add(ALTCHUNKLOADER);
			commands.add(OWNERS);
			commands.add(INFO);
			break;
		}
		return commands;
	}

}
