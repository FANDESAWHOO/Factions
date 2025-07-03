package org.hcgames.hcfactions.user;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.remain.Remain;


public class UserListener implements Listener {

	@Getter
	private static final UserListener userListener = new UserListener();
	private final HCFactions plugin;

	private UserListener() {
		plugin = HCFactions.getInstance();
	}

	public static String getDisplayName(@NonNull ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();

		return ((ItemStack) Remain.asNMSCopy(item)).getItemMeta().getDisplayName();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerStatIncrease(PlayerDeathEvent event) {
		FactionUser dead = plugin.getUserManager().getUser(event.getEntity().getUniqueId());
		Player killer = event.getEntity().getKiller();

		if (killer != null) {
			FactionUser killerUser = plugin.getUserManager().getUser(killer.getUniqueId());
			killerUser.incrementKills();

			EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
			String item = killer.getItemInHand() == null ? "" : getDisplayName(killer.getItemInHand());

			killerUser.addKill(event.getEntity().getName(), event.getEntity().getUniqueId(), item, cause);
			dead.addDeath(killer.getName(), killer.getUniqueId(), item, cause);
		}

		dead.incrementDeaths();
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		FactionUser user = plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
		user.updateName(event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		FactionUser user = plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
		user.updateName(event.getPlayer().getName());
	}

}
