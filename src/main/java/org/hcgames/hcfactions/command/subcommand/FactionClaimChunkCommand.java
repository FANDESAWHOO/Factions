package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

public class FactionClaimChunkCommand extends SimpleSubCommand {

	private static final int CHUNK_RADIUS = 7;
	private final HCFactions plugin;

	public FactionClaimChunkCommand() {
		super("claimchunk | chunkclaim");
		setDescription("Claim a chunk of land in the Wilderness.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		checkConsole();
		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try{
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		}catch (NoFactionFoundException e){
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}


		if (playerFaction.isRaidable()) {
			sender.sendMessage(Lang.of("Commands-Factions-ClaimChunk-NoClaimRaidable"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			sender.sendMessage(Lang.of("Commands-Factions-ClaimChunk-OfficerRequired"));
			return;
		}

		Location location = player.getLocation();
		plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction,
				location.clone().add(CHUNK_RADIUS, ClaimHandler.MIN_CLAIM_HEIGHT, CHUNK_RADIUS),
				location.clone().add(-CHUNK_RADIUS, ClaimHandler.MAX_CLAIM_HEIGHT, -CHUNK_RADIUS)));

		return;
	}
}
