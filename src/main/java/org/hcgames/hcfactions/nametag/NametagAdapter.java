package org.hcgames.hcfactions.nametag;

import org.bukkit.entity.Player;

public interface NametagAdapter {
	String getAndUpdate(Player player, Player target);
}