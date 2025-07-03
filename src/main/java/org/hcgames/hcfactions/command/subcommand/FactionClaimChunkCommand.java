package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

public final class FactionClaimChunkCommand extends FactionSubCommand {

	private static final int CHUNK_RADIUS = 7;
	private final HCFactions plugin;

	public FactionClaimChunkCommand() {
		super("claimchunk|chunkclaim");
		setDescription("Claim a chunk of land in the Wilderness.");
		plugin = HCFactions.getInstance();

	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}


		if (playerFaction.isRaidable()) {
			tell(Lang.of("Commands-Factions-ClaimChunk-NoClaimRaidable"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			tell(Lang.of("Commands-Factions-ClaimChunk-OfficerRequired"));
			return;
		}

		Location location = player.getLocation();
		plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction,
				location.clone().add(CHUNK_RADIUS, ClaimHandler.MIN_CLAIM_HEIGHT, CHUNK_RADIUS),
				location.clone().add(-CHUNK_RADIUS, ClaimHandler.MAX_CLAIM_HEIGHT, -CHUNK_RADIUS)));

		return;
	}
}
