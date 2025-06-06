package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public final class FactionClaimCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionClaimCommand() {
		super("claim|claimland");
		setDescription("Claim land in the Wilderness.");
		plugin = HCFactions.getInstance();

	}

    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}



	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	public void onCommand() {
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}


		if (playerFaction.isRaidable()) {
			tell(Lang.of("Commands-Factions-Claim-NoClaimRaidable"));
			return;
		}

		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
			tell(Lang.of("Commands-Factions-Claim-ClaimWandInvAlready"));
			return;
		}

		if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
			tell(Lang.of("Commands-Factions-Claim-SubClaimInInvError"));
			return;
		}

		if (!inventory.addItem(plugin.getClaimHandler().getClaimWand()).isEmpty()) {
			tell(Lang.of("Commands-Factions-Claim-InvFull"));
			return;
		}

		tell(Lang.of("Commands-Factions-Claim-Added")
				.replace("{commandLabel}", getLabel()));

		return;
	}
}
