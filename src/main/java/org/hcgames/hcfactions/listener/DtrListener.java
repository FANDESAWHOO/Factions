package org.hcgames.hcfactions.listener;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.JavaUtils;

import lombok.Getter;


public class DtrListener implements Listener {
	
	private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L); // TODO: Make this configurable.
	
	@Getter
	private static final DtrListener dtrListener = new DtrListener();
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.saveData();
		PlayerFaction playerFaction = HCFactions.getInstance().getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			World.Environment environment = player.getLocation().getWorld().getEnvironment();
			Faction factionAt = HCFactions.getInstance().getFactionManager().getFactionAt(player.getLocation());
			double dtrLoss = (environment == World.Environment.NETHER || environment == World.Environment.THE_END) ? 0.5 : (1.0D * factionAt.getDtrLossMultiplier());
			double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

			Role role = playerFaction.getMember(player.getUniqueId()).getRole();
			playerFaction.setRemainingRegenerationTime(BASE_REGEN_DELAY + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
			playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + Configuration.relationColourTeammate + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
		}


	}


}
