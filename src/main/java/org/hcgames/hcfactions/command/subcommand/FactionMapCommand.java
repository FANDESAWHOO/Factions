package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.LandMap;
import org.hcgames.hcfactions.util.GuavaCompat;
import org.hcgames.hcfactions.visualise.VisualType;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.List;


public final class FactionMapCommand extends FactionCommand {

	private static final List<String> visualTypes;

	static {
		VisualType[] values = VisualType.values();
		visualTypes = new ArrayList<>(values.length);
		for (VisualType visualType : values) visualTypes.add(visualType.name());
	}

	private final HCFactions plugin;

	public FactionMapCommand() {
		plugin = HCFactions.getInstance();

	}
	@Command(name = "faction.map", description = "View all claims around your chunk.", aliases = { "f.map"}, usage = "/f map <factionName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();


		VisualType visualType;
		if (arg.length() < 1) visualType = VisualType.CLAIM_MAP;
		else if ((visualType = GuavaCompat.getIfPresent(VisualType.class, arg.getArgs(0)).orElse(VisualType.NONE)) == VisualType.NONE) {
			player.sendMessage(ChatColor.RED + "Visual type " + arg.getArgs(0) + " not found.");

			return;
		}

		boolean newShowingMap = !(player.hasMetadata("claimMap") && player.getMetadata("claimMap").get(0).asBoolean());
		if (newShowingMap) {
			if (!LandMap.updateMap(player, plugin, visualType, true)) return;
		} else {
			HCFactions.getInstance().getVisualiseHandler().clearVisualBlocks(player, visualType, null);
			player.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
		}

		player.setMetadata("claimMap", new FixedMetadataValue(plugin, newShowingMap));

	}
}
