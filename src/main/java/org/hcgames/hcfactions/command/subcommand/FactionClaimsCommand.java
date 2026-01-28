package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Collection;
public final class FactionClaimsCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionClaimsCommand() {
		plugin = HCFactions.getInstance();
	}

	 @Command(name = "faction.claims", description = "View all claims for a faction.", aliases = { "f.claims"}, usage = "/f claims [factionName]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		 PlayerFaction selfFaction = null;
			try {
				selfFaction = plugin.getFactionManager().getPlayerFaction(player);
			} catch (NoFactionFoundException ignored) {
			}
			ClaimableFaction targetFaction;

			if (arg.length() < 1) {
				if (selfFaction == null) {
					player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
					return;
				}

				targetFaction = selfFaction;
			} else {
				PlayerFaction finalSelfFaction = selfFaction;
				plugin.getFactionManager().advancedSearch(arg.getArgs(0), ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {
					@Override
					public void onSuccess(ClaimableFaction faction) {
						handle(player, faction, finalSelfFaction);
					}

					@Override
					public void onFail(FailReason reason) {
						player.sendMessage(Lang.of("commands.error.faction_not_found", arg.getArgs(0)));
					}
				});
				return;
			}

			handle(player, targetFaction, selfFaction);
	 }
	

	private void handle(Player player, ClaimableFaction targetFaction, PlayerFaction selfFaction) {
		Collection<Claim> claims = targetFaction.getClaims();

		if (claims.isEmpty()) {
			player.sendMessage(Lang.of("Commands-Factions-Claims-FactionClaimedNothing")
					.replace("{factionName}", targetFaction.getFormattedName(player)));
			return;
		}

		if (!player.isOp() && (targetFaction instanceof PlayerFaction && !((PlayerFaction) targetFaction).getHome().isPresent()))
			if (selfFaction != targetFaction) {
				player.sendMessage(Lang.of("Commands-Factions-Claims-CannotViewNoHome")
						.replace("{factionName}", targetFaction.getFormattedName(player)));
				return;
			}

		player.sendMessage(Lang.of("Commands-Factions-Claims-ClaimListHeader")
				.replace("{factionName}", targetFaction.getFormattedName(player))
				.replace("{claimsAmount}", String.valueOf(claims.size())));

		for (Claim claim : claims)
			player.sendMessage(Lang.of("Commands-Factions-Claims-ClaimListItem")
					.replace("{claimName}", claim.getFormattedName()));
	}

}
