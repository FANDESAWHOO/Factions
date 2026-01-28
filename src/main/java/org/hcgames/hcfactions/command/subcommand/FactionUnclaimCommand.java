package org.hcgames.hcfactions.command.subcommand;


import com.google.common.collect.ImmutableList;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class FactionUnclaimCommand extends FactionCommand {

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final HCFactions plugin;


	public FactionUnclaimCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.unclaim", description = "Remove an ally pact with other factions.", aliases = {"f.unclaim"}, usage = "/f unclaim",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		FactionMember factionMember = playerFaction.getMember(player);

		if (factionMember.getRole() == Role.MEMBER || factionMember.getRole() == Role.CAPTAIN) {
			player.sendMessage(Lang.of("Commands-Factions-Unclaim-CoLeaderRequired"));
			return;
		}

		Collection<Claim> factionClaims = playerFaction.getClaims();

		if (factionClaims.isEmpty()) {
			player.sendMessage(Lang.of("Commands-Factions-Unclaim-NoClaims"));
			//player.sendMessage(ChatColor.RED + "Your faction does not own any claims.");
			return;
		}

		// Find out what claims the player wants removed.
		Collection<Claim> removingClaims;
		if (arg.length() > 1 && arg.getArgs(0).equalsIgnoreCase("all")) removingClaims = new ArrayList<>(factionClaims);
		else {
			Location location = player.getLocation();
			Claim claimAt = plugin.getFactionManager().getClaimAt(location);
			if (claimAt == null || !factionClaims.contains(claimAt)) {
				player.sendMessage(Lang.of("Commands-Factions-Unclaim-NoClaimHere"));
				//tell(ChatColor.RED + "Your faction does not own a claim here.");
				return;
			}

			removingClaims = Collections.singleton(claimAt);
		}

		if (!playerFaction.removeClaims(removingClaims, player)) {
			player.sendMessage(Lang.of("Commands-Factions-Unclaim-ErrorRemoving"));
			//tell(ChatColor.RED + "Error when removing claims, please contact an Administrator.");
			return;
		}

		int removingAmount = removingClaims.size();
		playerFaction.broadcast(Lang.of("Commands-Factions-Unclaim-RemovedClaims")
				.replace("{player}", factionMember.getRole().getAstrix() + player.getName())
				.replace("{amountOfClaims}", String.valueOf(removingAmount))
				.replace("{s}", (removingAmount > 1 ? "s" : "")));

		return;
	}

}
