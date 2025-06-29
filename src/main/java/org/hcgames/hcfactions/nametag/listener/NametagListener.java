package org.hcgames.hcfactions.nametag.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.nametag.Nametag;

public class NametagListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST) // call first
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		HCFactions.getInstance().getNametagManager().getNametags().put(player.getUniqueId(), new Nametag(player));
	}

	@EventHandler(priority = EventPriority.HIGHEST) // call last
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Nametag nametag = HCFactions.getInstance().getNametagManager().getNametags().remove(player.getUniqueId());
		if (nametag != null) nametag.delete();
	}
}
