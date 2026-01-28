package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionClaimChunkCommand extends FactionCommand {

	private static final int CHUNK_RADIUS = 7;
	private final HCFactions plugin;

	public FactionClaimChunkCommand() {
		plugin = HCFactions.getInstance();

	}

	 @Command(name = "faction.claimchunk", description = "Claim a chunk of land in the Wilderness.", aliases = { "f.chunkclaim", "f.claimchunk"}, usage = "/f claimchunk",  playerOnly = true, adminsOnly = false)
	public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
			PlayerFaction playerFaction;
			try {
				playerFaction = plugin.getFactionManager().getPlayerFaction(player);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}


			if (playerFaction.isRaidable()) {
				player.sendMessage(Lang.of("Commands-Factions-ClaimChunk-NoClaimRaidable"));
				return;
			}

			if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
				player.sendMessage(Lang.of("Commands-Factions-ClaimChunk-OfficerRequired"));
				return;
			}

			Location location = player.getLocation();
			plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction,
					location.clone().add(CHUNK_RADIUS, ClaimHandler.MIN_CLAIM_HEIGHT, CHUNK_RADIUS),
					location.clone().add(-CHUNK_RADIUS, ClaimHandler.MAX_CLAIM_HEIGHT, -CHUNK_RADIUS)));

			return;
	 }

}
