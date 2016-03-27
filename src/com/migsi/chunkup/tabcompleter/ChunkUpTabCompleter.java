package com.migsi.chunkup.tabcompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import com.migsi.chunkup.commands.Commands;

public class ChunkUpTabCompleter implements TabCompleter {
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> ret = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("chunkup")) {
			if (args.length == 1 && args[0].isEmpty()) {
				ret = Commands.getCommands(command.getName());
			} else if (args.length == 1) {
				String lastWord = args[args.length - 1];
		        for (String cmd : Commands.getCommands(command.getName())) {
		            if (StringUtil.startsWithIgnoreCase(cmd, lastWord)) {
		                ret.add(cmd);
		            }
		        }
			} else if (args.length == 2 && args[1].isEmpty()) {
				ret = Commands.getCommands(args[0]);
			} else if (args.length == 2) {
				String lastWord = args[args.length - 1];
		        for (String cmd : Commands.getCommands(args[0])) {
		            if (StringUtil.startsWithIgnoreCase(cmd, lastWord)) {
		                ret.add(cmd);
		            }
		        }
			}
		}
        Collections.sort(ret, String.CASE_INSENSITIVE_ORDER);
		return ret;
	}

}
