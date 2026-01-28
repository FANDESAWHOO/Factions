package org.hcgames.hcfactions.util.uuid;


import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

//TODO: Add a way of going uuid -> username
public class UUIDHandler implements Listener {

	private final static ExpiringMap<String, UUID> cache = ExpiringMap.builder().variableExpiration().build();

	public static UUID getUUID(String username) {
		username = username.toLowerCase();

		if (cache.containsKey(username)) {
			return cache.get(username);
		}

		UUID userUUID;
		try {
			userUUID = UUIDFetcher.getUUIDOf(username.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		cache.put(username, userUUID, ExpirationPolicy.CREATED, 2, TimeUnit.HOURS);
		return userUUID;
	}

	public static boolean isLoaded(String username) {
		return cache.containsKey(username.toLowerCase());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		cache.put(event.getPlayer().getName().toLowerCase(), event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (cache.containsKey(event.getPlayer().getName().toLowerCase())) {
			cache.setExpiration(event.getPlayer().getName().toLowerCase(), 2, TimeUnit.HOURS);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		if (cache.containsKey(event.getPlayer().getName().toLowerCase())) {
			cache.setExpiration(event.getPlayer().getName().toLowerCase(), 2, TimeUnit.HOURS);
		}
	}
}
