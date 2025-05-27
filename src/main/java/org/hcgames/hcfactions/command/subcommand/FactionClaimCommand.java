package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public class FactionClaimCommand extends SimpleSubCommand {
	private final HCFactions plugin;

	public FactionClaimCommand(HCFactions plugin) {
		super("claim | claimland");
		setDescription("Claim land in the Wilderness.");
		this.plugin = plugin;
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}



	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	protected void onCommand() {
		checkConsole();

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}


		if (playerFaction.isRaidable()) {
			sender.sendMessage(Lang.of("Commands-Factions-Claim-NoClaimRaidable"));
			return;
		}

		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
			sender.sendMessage(Lang.of("Commands-Factions-Claim-ClaimWandInvAlready"));
			return;
		}

		if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
			sender.sendMessage(Lang.of("Commands-Factions-Claim-SubClaimInInvError"));
			return;
		}

		if (!inventory.addItem(plugin.getClaimHandler().getClaimWand()).isEmpty()) {
			sender.sendMessage(Lang.of("Commands-Factions-Claim-InvFull"));
			return;
		}

		sender.sendMessage(Lang.of("Commands-Factions-Claim-Added")
				.replace("{commandLabel}", getLabel()));

		return;
	}
}
