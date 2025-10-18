package org.hcgames.hcfactions.listener;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.user.FactionUser;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.remain.CompEntityType;
import org.mineacademy.fo.remain.NmsEntity;
import org.mineacademy.fo.remain.Remain;

import java.util.concurrent.TimeUnit;


public final class DeathListener implements Listener {

	private final HCFactions plugin;
	@Getter private static final DeathListener instance = new DeathListener();
	private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L); // TODO: Make this configurable.
	private DeathListener(){
		plugin = HCFactions.getInstance();
	}

	public static String getDisplayName(ItemStack item) {
	    if (item == null || !item.getType().isSolid()) return "Air";
	    ItemMeta meta = item.getItemMeta();
	    if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();

	    try {

	        return item.getItemMeta().getLocalizedName();
	    } catch (Exception e) {
	        return ItemUtil.bountifyCapitalized(item.getType());
	    }
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
		World.Environment environment = player.getLocation().getWorld().getEnvironment();

		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.saveData();
		PlayerFaction playerFaction = HCFactions.getInstance().getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
			double dtrLoss = (environment == World.Environment.NETHER || environment == World.Environment.THE_END) ? 0.5 : (1.0D * factionAt.getDtrLossMultiplier());
			double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

			Role role = playerFaction.getMember(player.getUniqueId()).getRole();
			playerFaction.setRemainingRegenerationTime(BASE_REGEN_DELAY + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
			playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + Configuration.relationColourTeammate + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
		}

		if (Remain.getTPS() > 15) { // Prevent unnecessary lag during prime times.
			Location location = player.getLocation();
			spawnLightning(location);
		}
	}


	private void spawnLightning(Location location){
		NmsEntity entity = new NmsEntity(location,CompEntityType.LIGHTNING_BOLT.getEntityClass());
		entity.addEntity(CreatureSpawnEvent.SpawnReason.CUSTOM);
	}


}