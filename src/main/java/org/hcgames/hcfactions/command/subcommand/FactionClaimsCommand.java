package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FactionClaimsCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionClaimsCommand() {
		super("claims");
		setDescription("View all claims for a faction.");
		plugin = HCFactions.getInstance();
	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " [factionName]";
	}

	@Override
	public void onCommand() {
		PlayerFaction selfFaction = null;
		try {
			selfFaction = sender instanceof Player ? plugin.getFactionManager().getPlayerFaction((Player) sender) : null;
		} catch (NoFactionFoundException ignored) {
		}
		ClaimableFaction targetFaction;

		if (args.length < 2) {
			if (!(sender instanceof Player)) {
				tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
				return;
			}

			if (selfFaction == null) {
				tell(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}

			targetFaction = selfFaction;
		} else {
			PlayerFaction finalSelfFaction = selfFaction;
			plugin.getFactionManager().advancedSearch(args[1], ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {
				@Override
				public void onSuccess(ClaimableFaction faction) {
					handle(sender, faction, finalSelfFaction);
				}

				@Override
				public void onFail(FailReason reason) {
					tell(Lang.of("commands.error.faction_not_found", args[1]));
				}
			});
			return;
		}

		handle(sender, targetFaction, selfFaction);
	}

	private void handle(CommandSender sender, ClaimableFaction targetFaction, PlayerFaction selfFaction) {
		Collection<Claim> claims = targetFaction.getClaims();

		if (claims.isEmpty()) {
			tell(Lang.of("Commands-Factions-Claims-FactionClaimedNothing")
					.replace("{factionName}", targetFaction.getFormattedName(sender)));
			return;
		}

		if (sender instanceof Player && !sender.isOp() && (targetFaction instanceof PlayerFaction && !((PlayerFaction) targetFaction).getHome().isPresent()))
			if (selfFaction != targetFaction) {
				tell(Lang.of("Commands-Factions-Claims-CannotViewNoHome")
						.replace("{factionName}", targetFaction.getFormattedName(sender)));
				return;
			}

		tell(Lang.of("Commands-Factions-Claims-ClaimListHeader")
				.replace("{factionName}", targetFaction.getFormattedName(sender))
				.replace("{claimsAmount}", String.valueOf(claims.size())));

		for (Claim claim : claims)
			tell(Lang.of("Commands-Factions-Claims-ClaimListItem")
					.replace("{claimName}", claim.getFormattedName()));
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();
		else if (args[1].isEmpty()) return null;
		else {
			Player player = ((Player) sender);
			List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
			for (Player target : Bukkit.getOnlinePlayers())
				if (player.canSee(target) && !results.contains(target.getName())) results.add(target.getName());

			return results;
		}
	}
}
