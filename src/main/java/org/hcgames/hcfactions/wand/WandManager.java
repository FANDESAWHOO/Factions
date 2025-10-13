package org.hcgames.hcfactions.wand;


import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.util.cuboid.Cuboid;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;


import crossversion.worldeditx.CrossVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class WandManager extends Tool {

	/**
	 * Singleton of the class
	 */
	@Getter
	private final static WandManager wandManager = new WandManager();
	@Getter private final Map<UUID, ZoneClaim> claimCache = new HashMap<>();


	private WandManager() {

	}


	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.STICK, "&aClaim Selection", "Right click block = First point", "Left click block = Second point", "Shift + Left click = Confirm region", "Right click air = Cancel selection").make();
	}

    public Cuboid getSelection(Player player) {
    	Cuboid region = null;
    	if (Bukkit.getPluginManager().isPluginEnabled("WorldEditX"))
    		region = CrossVersion.getInstance().worldEditManager.getSelection(player);
    	else 
    		region = claimCache.get(player.getUniqueId()).getCuboId();
    	return region;
    }

	/**
	 * With this event we gonna put
	 * The selection players!
	 *
	 * @param event the event
	 */
	@Override
	protected void onBlockClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("tools.use")) return;
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		UUID uuid = player.getUniqueId();
		ZoneClaim claim = claimCache.computeIfAbsent(uuid, u -> new ZoneClaim(null, null)); 
		if (action == Action.RIGHT_CLICK_AIR) {
			claimCache.remove(uuid);
			player.getInventory().setItemInHand(null); // TODO: we need to check if this give error in 1.9+
			player.sendMessage(ChatColor.RED + "Selection cancelled.");
			event.setCancelled(true);
			return;
		}
		if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if (claim.getLocation1() == null || claim.getLocation2() == null)
				player.sendMessage(ChatColor.RED + "You must select both points first.");
			else {
				Cuboid cuboid = new Cuboid(claim.getLocation1(), claim.getLocation2()); 
				player.sendMessage(ChatColor.GREEN + "Region selected: " + cuboid.toString());
			}
			claimCache.remove(uuid);
			player.getInventory().setItemInHand(null);
			event.setCancelled(true);
			return;
		}
		if (action == Action.RIGHT_CLICK_BLOCK && block != null) {
			Location clicked = block.getLocation();
			claim.setLocation1(clicked);
			player.sendMessage(ChatColor.GREEN + "First point set at " + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ());
			event.setCancelled(true);
			return;
		} 
		if (action == Action.LEFT_CLICK_BLOCK && block != null) {
			Location clicked = block.getLocation();
			claim.setLocation2(clicked);
			player.sendMessage(ChatColor.GREEN + "Second point set at " + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ());
			event.setCancelled(true);
		}
	}

	@Setter @Getter
	public static class ZoneClaim {

		private Location location1;

		private Location location2;

		public Cuboid getCuboId(){
			return new Cuboid(location1, location2);
		}

		public ZoneClaim(Location loc1, Location loc2){
			location1 = loc1;
			location2 = loc2;
		}
	}
}




