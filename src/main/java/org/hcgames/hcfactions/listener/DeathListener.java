package org.hcgames.hcfactions.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.user.FactionUser;
import org.mineacademy.fo.remain.CompEntityType;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.NmsEntity;

@RequiredArgsConstructor
public class DeathListener implements Listener {

	private final HCFactions plugin;

	public static String getDisplayName(@NonNull ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();

		if (item instanceof CraftItemStack) {
			CraftItemStack craftItemStack = (CraftItemStack) item;

		}

		return CraftItemStack.asNMSCopy(item).getName();
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

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (plugin.getServer().spigot().getTPS()[0] > 15) {
			Location location = player.getLocation();
			World world = location.getWorld();


			NmsEntity lightningEntity = new NmsEntity(
					location,
					CompEntityType.LIGHTNING_BOLT.getEntityClass()
			);


			Entity spawnedLightning = lightningEntity.addEntity(CreatureSpawnEvent.SpawnReason.LIGHTNING);

			// 3. Reproducir sonido para todos los jugadores
			Sound thunderSound = CompSound.ENTITY_LIGHTNING_BOLT_THUNDER.getSound();

			for (Player target : Bukkit.getOnlinePlayers())
				target.playSound(target.getLocation(), thunderSound, 1.0F, 1.0F);
		}
	}
/*	//TODO: Move death message to factions
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (plugin.getServer().spigot().getTPS()[0] > 15) { // Prevent unnecessary lag during prime times.
			Location location = player.getLocation();
			WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
			EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
			PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
			Bukkit.getOnlinePlayers().forEach(target -> {
				((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
				target.playSound(target.getLocation(), CompSound.ENTITY_LIGHTNING_BOLT_THUNDER.getSound(), 1.0F, 1.0F);
			});
		}
	}*/

	private Entity getKiller(Player player) {//TODO Account for time difference & look into getKiller from DeathMessageListener
		if (player.getKiller() != null) return player.getKiller();

		if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) player.getLastDamageCause();
			return event.getDamager();
		}

		return null;
	}
}