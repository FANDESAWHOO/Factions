package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.LandMap;
import org.hcgames.hcfactions.util.GuavaCompat;
import org.hcgames.hcfactions.visualise.VisualType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FactionMapCommand extends FactionSubCommand {

	private static final List<String> visualTypes;

	static {
		VisualType[] values = VisualType.values();
		visualTypes = new ArrayList<>(values.length);
		for (VisualType visualType : values) visualTypes.add(visualType.name());
	}

	private final HCFactions plugin;

	public FactionMapCommand() {
		super("map");
		setDescription("View all claims around your chunk.");
		plugin = HCFactions.getInstance();

	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " [factionName]";
	}

	@Override
	public void onCommand() {
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();

//		final FactionUser factionUser = HCF.getPlugin().getUserManager().getUser(uuid);
		VisualType visualType;
		if (args.length < 2) visualType = VisualType.CLAIM_MAP;
		else if ((visualType = GuavaCompat.getIfPresent(VisualType.class, args[1]).orElse(VisualType.NONE)) == VisualType.NONE) {
			player.sendMessage(ChatColor.RED + "Visual type " + args[1] + " not found.");
			// player.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Map-VisualTypeNotFound")
			//       .replace("{visualType}", args[1]));
			return;
		}

		boolean newShowingMap = !(player.hasMetadata("claimMap") && player.getMetadata("claimMap").get(0).asBoolean());
		if (newShowingMap) {
			if (!LandMap.updateMap(player, plugin, visualType, true)) return;
		} else {
			HCFactions.getInstance().getVisualiseHandler().clearVisualBlocks(player, visualType, null);
			//     tell(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Map-DisabledClaimPillars"));
			tell(ChatColor.RED + "Claim pillars are no longer shown.");
		}

		player.setMetadata("claimMap", new FixedMetadataValue(plugin, newShowingMap));

	}

	@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

		return visualTypes;
	}

}
