package org.hcgames.hcfactions.wand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Originally we used WorldEdit to
 * Handle claim selection but
 * WorldEdit change the API
 * On determinate versions
 * And im lazy to manage that xd
 * UPDATE: CHANGED THIS TO USE CUBOID :)
 */
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

	/*
	protected Cuboid crossVersion(Player player) {
		Cuboid region = null;
		LocalSession session = WorldEdit.getInstance().getSessionManager().get((SessionOwner) player);
		Selection selection = session.getSelection();
		region = new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()); // CROSSVERSION 1.13+
		
		return region;
	} soon.*/


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
		ZoneClaim claim = claimCache.computeIfAbsent(uuid, u -> new ZoneClaim(null, null)); // Cancelar selección 
		if (action == Action.RIGHT_CLICK_AIR) {
			claimCache.remove(uuid);
			player.getInventory().setItemInHand(null);
			player.sendMessage(ChatColor.RED + "Selection cancelled.");
			event.setCancelled(true);
			return;
		} // Confirmar selección
		if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if (claim.getLocation1() == null || claim.getLocation2() == null)
				player.sendMessage(ChatColor.RED + "You must select both points first.");
			else {
				Cuboid cuboid = new Cuboid(claim.getLocation1(), claim.getLocation2()); // TODO: acá va la lógica de guardar zona/KOTH/etc.
				claim.setCuboId(cuboid);
				player.sendMessage(ChatColor.GREEN + "Region selected: " + cuboid.toString());
			}
			claimCache.remove(uuid);
			player.getInventory().setItemInHand(null);
			event.setCancelled(true);
			return;
		} // Punto 1
		if (action == Action.RIGHT_CLICK_BLOCK && block != null) {
			Location clicked = block.getLocation();
			claim.setLocation1(clicked);
			player.sendMessage(ChatColor.GREEN + "First point set at " + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ());
			event.setCancelled(true);
			return;
		} // Punto 2
		if (action == Action.LEFT_CLICK_BLOCK && block != null) {
			Location clicked = block.getLocation();
			claim.setLocation2(clicked);
			player.sendMessage(ChatColor.GREEN + "Second point set at " + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ());
			event.setCancelled(true);
		}
	}

	@Setter @Getter @AllArgsConstructor
	public static class ZoneClaim {

		private Location location1;

		private Location location2;

		private Cuboid cuboId;

		/*public Cuboid getCuboId(){
			return new Cuboid(location1, location2);
		}*/

		public ZoneClaim(Location loc1, Location loc2){
			location1 = loc1;
			location2 = loc2;
		}
	}
}




