package org.hcgames.hcfactions.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class NMSU {
	public static void sendBlockChange0(Player player, Location location, Block block) {
		try {
	//		player.sendBlockChange(location, block.getBlockData());
		} catch (NoSuchMethodError ex) {
			player.sendBlockChange(location, block.getType(), block.getData());
		}
	}
}
