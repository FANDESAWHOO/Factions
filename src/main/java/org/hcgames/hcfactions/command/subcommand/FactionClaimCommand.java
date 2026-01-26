package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


import java.util.UUID;

public final class FactionClaimCommand extends FactionCommand {
	private final HCFactions plugin;
	public FactionClaimCommand() {
		plugin = HCFactions.getInstance();
		

	}
	 @Command(name = "faction.claim", description = "Claim land in the Wilderness.", aliases = { "f.claim", "f.claimland"}, usage = "/f claim",  playerOnly = true, adminsOnly = false)
		public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		 
		 UUID uuid = player.getUniqueId();

			PlayerFaction playerFaction;
			try {
				playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}


			if (playerFaction.isRaidable()) {
				player.sendMessage(Lang.of("Commands-Factions-Claim-NoClaimRaidable"));
				return;
			}

			PlayerInventory inventory = player.getInventory();

			if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
				player.sendMessage(Lang.of("Commands-Factions-Claim-ClaimWandInvAlready"));
				return;
			}

			if (inventory.contains(plugin.getClaimHandler().getClaimWand())) {
				player.sendMessage(Lang.of("Commands-Factions-Claim-SubClaimInInvError"));
				return;
			}

			if (!inventory.addItem(plugin.getClaimHandler().getClaimWand()).isEmpty()) {
				player.sendMessage(Lang.of("Commands-Factions-Claim-InvFull"));
				return;
			}

			player.sendMessage(Lang.of("Commands-Factions-Claim-Added")
					.replace("{commandLabel}", arg.getLabel()));

			return;
		 
	 }
}
