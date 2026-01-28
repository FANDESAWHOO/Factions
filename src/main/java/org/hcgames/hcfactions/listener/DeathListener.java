package org.hcgames.hcfactions.listener;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.user.FactionUser;
import org.hcgames.hcfactions.util.SpigotUtils;



public final class DeathListener implements Listener {

	private final HCFactions plugin;
	@Getter private static final DeathListener instance = new DeathListener();
	private DeathListener(){
		plugin = HCFactions.getInstance();
	}

	public static String getDisplayName(ItemStack item) {
	    if (item == null || !item.getType().isSolid()) return "Air";
	    ItemMeta meta = item.getItemMeta();
	    if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();

	    return item.getItemMeta().getDisplayName();

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

		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.saveData();
		if (SpigotUtils.getTPS() > 15) { // Prevent unnecessary lag during prime times.
		    Location loc = player.getLocation();

		    loc.getWorld().strikeLightningEffect(loc);

		    for (Player target : Bukkit.getOnlinePlayers()) {
		        target.playSound(
		            target.getLocation(),
		            Sound.AMBIENCE_THUNDER,
		            1.0F,
		            1.0F
		        );
		    }
		}

	}


}