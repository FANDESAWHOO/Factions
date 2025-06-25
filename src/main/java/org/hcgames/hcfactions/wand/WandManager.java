package org.hcgames.hcfactions.wand;

import lombok.Getter;
import org.bukkit.ChatColor;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Originally we used WorldEdit to
 * Handle claim selection but
 * WorldEdit change the API
 * On determinate versions
 * And im lazy to manage that xd
 * UPDATE: CHANGED THIS TO USE CUBOID :)
 */
public final class WandManager extends Tool {

	private final Map<Player, Map<String, Cuboid>> selectionMap = new ConcurrentHashMap<>();

	/**
	 * Singleton of the class
	 */
	@Getter
	private final static WandManager wandManager = new WandManager();

	private WandManager(){

	}
	public Map<String, Cuboid> getSelection(Player player){
		return selectionMap.get(player);
	}
    public Cuboid getSelection(Player player, String position) {
		return selectionMap.get(player).get(position);
	}
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.STICK,"&aClaim Selection", "Right click to select first point, Left click to select second point").make();
	}

	/**
	 * With this event we gonna put
	 * The selection players!
	 * @param event the event
	 */
	@Override
	protected void onBlockClick(PlayerInteractEvent event) {
		if(event.getPlayer().hasPermission("tools.use")){
			Action action = event.getAction();
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			HashMap<String, Cuboid> locs = !selectionMap.containsKey(player) ? new HashMap() : (HashMap) selectionMap.get(player);
			if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_AIR))
				player.sendMessage(ChatColor.RED + "You must select a block and not Air.");

			if(action.equals((Action.RIGHT_CLICK_BLOCK))) {
				locs.put("1", new Cuboid(block.getLocation()));
				selectionMap.put(player,locs);
				player.sendMessage(ChatColor.GREEN + "You selected the first point at: "+block.getX() + ", "+block.getY()+", "+block.getZ());
			} else if (action.equals(Action.LEFT_CLICK_BLOCK)) {
				locs.put("2", new Cuboid(block.getLocation()));
				selectionMap.put(player,locs);
				player.sendMessage(ChatColor.GREEN + "You selected the second point at: "+block.getX() + ", "+block.getY()+", "+block.getZ());
			}
			event.setCancelled(true);
		}
	}
}
