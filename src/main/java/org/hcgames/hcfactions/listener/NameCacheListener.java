package org.hcgames.hcfactions.listener;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;


public class NameCacheListener implements Listener {

	@Getter
	private final static NameCacheListener nameCacheListener = new NameCacheListener();
	private final HCFactions plugin;

	private NameCacheListener() {
		plugin = HCFactions.getInstance();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerFaction faction;


		try {
			faction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
		} catch (NoFactionFoundException ignored) {
			return;
		}

		FactionMember member = faction.getMember(player);
		member.setCachedName(player.getName());
	}

}
