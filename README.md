#ChunkUp

#Overview

This plugin allows you to select chunks and keep them loaded, also when no player is near. This can be useful for e.g. large redstone circuits, minecart transportation systems or commandblocks. *Yay* :D

#That means exactly?

There is still more! If you have a loooong railway, you could immagine how long it would take to mark every single chunk on the way by hand, wouldn't you? That's an absolutely no-go. And thats why there is a command to follow you while travelling around! So you could sit in your minecart, go all the way in minutes and relax. :) But there's not just the possibility to keep those marked chunks loaded. You will also own them, which means your chunks belong to you and nobody can unmark them (unless they've got the right permissions... :/). And when somebody else tries to own you chunk, don't be afraid. They will co-own this chunk, to guarantee it will be kept loaded. :)

For downloading a compiled version, see [here](http://dev.bukkit.org/bukkit-plugins/chunkup/).

#Commands

**/chunkup help** - Gives a short overview to the available commands

**/chunkup info** - Shows you basic informations about the chunk you're in

**/chunkup list** - Shows you a list of all marked chunks

**/chunkup mark [route]** - Marks the chunk you're in

**/chunkup follow mark|unmark [route]** - Follows the player and marks all chunks he travels through ("route" works only with "mark")

**/chunkup escape** - Stops chasing the player

**/chunkup unmark** - Unmarks the chunks you're in

**/chunkup unmarkall [player]** - Unmarks all your chunks. If [player] is given, all chunks of this player are unmarked

**/chunkup get|set ignoreInterval|refreshTime|altChunkLoader|owners|info [value]** - Shows/changes the "ignoreInterval", "refreshTime", "altChunkLoader" of "owners" value, "info" works only with "get"

**Alias** for "/chunkup" is **"/cu"**

---

The value **"route"** is used to group greater areas of chunks together. Actually the plugin just gives every marked chunk a route, but does nothing more with it. In a future release I'll maybe add some group functions, that make management easier.

**"ignoreInterval"** is used to skip some PlayerMovementEvents, which are used to follow the player. This makes the processing of chunks faster, while the player is chased. It's standart value is 10, which should be enough to speed the plugin up a little, while maintaining full functionality. Greater values can speed up the plugin, but you risk to miss some chunks, if you travel through them fast.

**"refreshTime"** tells the server how many ticks can go by until the plugin reloads marked chunks. A server running on normal speed should have around 20 ticks per second, while the standart value of "refreshTime" is set to 50. This means chunks are reloaded every 2,5 seconds. The standart value is set this low, to guarantee full functionality with most the servers. **It is highly recommended to fine tune this value by your own and post suggestions for other working values.**

**"altChunkLoader"** is a setting to toggle event based chunk loading. This makes the plugin much more efficient. As long as you don't have dozens of players moving around while keeping up dozens of chunks, it is **highly recommended to use this** way to keep your chunks loaded. To toggle it, use the [value] argument with true/false.

**"owners"** is a setting to determine if the owner system is used or not. If it is enabled, only the player who marked a chunk or everyone with the "chunkup.unmarkall" permission can unmark it. If it is disabled, marked chunks will still have their owners, but everybody with the "chunkup.unmarkall.own" permission can remove all chunks.

#Permission nodes

**chunkup.***

**chunkup.help**

**chunkup.info**

**chunkup.list**

**chunkup.mark**

**chunkup.follow**

**chunkup.escape**

**chunkup.unmark**

**chunkup.unmarkall**

**chunkup.unmarkall.own**

**chunkup.set**

**chunkup.get**

#Compatibility

The plugin was testet with PaperSpigot and should work with Bukkit too. **It doesn't work with Spigot**, because Spigot blocks any attempt to load chunks out of range from players.

#Other

Keep in mind, that this plugin **might impact** on your **server performance**, depending on how many chunks you want to stay loaded. :/

Any **suggestions and feedback** from the community are **welcome**! And one last thing: I'm a native German, so I apologize for my speaking/spelling errors.

If you like my work, feel free do donate :)
