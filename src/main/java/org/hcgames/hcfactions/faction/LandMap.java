package org.hcgames.hcfactions.faction;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.util.BukkitUtils;
import org.hcgames.hcfactions.visualise.VisualBlockData;
import org.hcgames.hcfactions.visualise.VisualType;

import com.cryptomorin.xseries.XMaterial;

import java.util.*;

public class LandMap {
	private static final int FACTION_MAP_RADIUS_BLOCKS;

	static {
		FACTION_MAP_RADIUS_BLOCKS = 22;
	}

	@SuppressWarnings("deprecation")
	public static boolean updateMap(Player player, HCFactions plugin, VisualType visualType, boolean inform) {
		Location location = player.getLocation();
		World world = player.getWorld();
		int locationX = location.getBlockX();
		int locationZ = location.getBlockZ();
		int minimumX = locationX - FACTION_MAP_RADIUS_BLOCKS;
		int minimumZ = locationZ - FACTION_MAP_RADIUS_BLOCKS;
		int maximumX = locationX + FACTION_MAP_RADIUS_BLOCKS;
		int maximumZ = locationZ + FACTION_MAP_RADIUS_BLOCKS;
		Set<Claim> board = new LinkedHashSet<Claim>();
		if (visualType != VisualType.CLAIM_MAP) {
			player.sendMessage(ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
			return false;
		}

		for (int x = minimumX; x <= maximumX; ++x)
			for (int z = minimumZ; z <= maximumZ; ++z) {
				Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
				if (claim != null) board.add(claim);
			}
		if (board.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No claims are in your visual range to display.");
			return false;
		}
		LOOP_MAIN:
		for (Claim claim2 : board) {
			int maxHeight = Math.min(world.getMaxHeight(), 256);
			Location[] corners = claim2.getCornerLocations();
			List<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
			for (Location corner : corners)
				for (int y = 0; y < maxHeight; ++y)
					shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
			Map<Location, VisualBlockData> dataMap = HCFactions.getInstance().getVisualiseHandler().generate(player, shown, visualType, true);
			if (dataMap.isEmpty()) continue LOOP_MAIN;
			String materialName = ChatColor.RED + "Error!";
			LOOP_1:
			for (VisualBlockData visualBlockData : dataMap.values())
				if (visualBlockData.getItemType() == XMaterial.BLACK_STAINED_GLASS.parseMaterial()) continue LOOP_1;
				else {
					materialName = HCFactions.getInstance().getItemDb().getName(new ItemStack(visualBlockData.getItemType()));
					break LOOP_1;
				}
			if (!inform) continue LOOP_MAIN;
			player.sendMessage(claim2.getFaction().getFormattedName() + ChatColor.YELLOW + " owns claim displayed by the " + ChatColor.AQUA + materialName);
		}
		return true;
	}

	public static Location getNearestSafePosition(Player player, Location origin, int searchRadius) {
		Location location = player.getLocation();
		player.getWorld();
		location.getBlockX();
		location.getBlockZ();
		Claim claimAt = HCFactions.getInstance().getFactionManager().getClaimAt(player.getLocation());
		Location closest = null;
		for (Location claim : claimAt.getCornerLocations())
			if (closest == null) closest = claim;
			else if (claim.distance(player.getLocation()) < closest.distance(player.getLocation())) closest = claim;
		if (closest == null) return null;
		closest.add(0, 1, 0);
		return BukkitUtils.getHighestLocation(closest);
	}
}
