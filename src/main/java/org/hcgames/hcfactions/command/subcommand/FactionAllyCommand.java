package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.event.playerfaction.FactionRelationCreateEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionRelation;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.*;

public final class FactionAllyCommand extends FactionSubCommand {

	private static final Relation RELATION = Relation.ALLY;

	private final HCFactions plugin;

	public FactionAllyCommand() {
		super("ally | alliance");
		setDescription("Make an ally pact with other factions.");
		plugin = HCFactions.getInstance();

	}

    @Override
 	public String getUsage() {
		return '/' + label + ' ' + getName() + " <factionName>";
	}

	@Override
	public void onCommand() {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.of("Commands-ConsoleOnly"));
			return;
		}

		if (Configuration.factionMaxAllies <= 0) {
			sender.sendMessage(Lang.of("Commands-Factions-Ally-AlliesDisabled"));
			return;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			sender.sendMessage(Lang.of("Commands-Factions-Ally-OfficerRequired"));
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				if (playerFaction == faction) {
					sender.sendMessage(Lang.of("Commands-Factions-Ally-RequestingOwnFaction").replace("{relationName}", RELATION.getDisplayName()));
					return;
				}

				Collection<UUID> allied = playerFaction.getAllied();

				if (allied.size() >= Configuration.factionMaxAllies) {
					sender.sendMessage(Lang.of("Commands-Factions-Ally-OwnFactionLimitReached").replace("{allyLimit}", String.valueOf(Configuration.factionMaxAllies)));
					return;
				}

				if (faction.getAllied().size() >= Configuration.factionMaxAllies) {
					sender.sendMessage(Lang.of("Commands-Factions-Ally-OtherFactionLimitReached")
							.replace("{allyLimit}", String.valueOf(Configuration.factionMaxAllies))
							.replace("{otherFactionName}", faction.getFormattedName(sender)));

					return;
				}

				if (allied.contains(faction.getUniqueID())) {
					sender.sendMessage(Lang.of("Commands-Factions-Ally-RequestingOwnFaction")
							.replace("{relationName}", RELATION.getDisplayName())
							.replace("{otherFactionName}", faction.getFormattedName(playerFaction)));

					return;
				}

				// Their faction has already requested us, lets' accept.
				if (faction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
					FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, faction, RELATION);
					Bukkit.getPluginManager().callEvent(event);

					faction.getRelations().put(playerFaction.getUniqueID(), new FactionRelation(RELATION, playerFaction.getUniqueID()));
					faction.broadcast(Lang.of("Commands-Factions-Ally-NowAllied")
							.replace("{relationName}", RELATION.getDisplayName())
							.replace("{otherFactionName}", playerFaction.getFormattedName(faction)));

					playerFaction.getRelations().put(faction.getUniqueID(), new FactionRelation(RELATION, faction.getUniqueID()));
					playerFaction.broadcast(Lang.of("Commands-Factions-Ally-NowAllied")
							.replace("{relationName}", RELATION.getDisplayName())
							.replace("{otherFactionName}", faction.getFormattedName(playerFaction)));

					return;
				}

				if (playerFaction.getRequestedRelations().putIfAbsent(faction.getUniqueID(), RELATION) != null) {
					sender.sendMessage(Lang.of("Commands-Factions-Ally-AlreadyRequested")
							.replace("{relationName}", RELATION.getDisplayName())
							.replace("{otherFactionName}", faction.getFormattedName(playerFaction)));

					return;
				}

				// Handle the request.

				playerFaction.broadcast(Lang.of("Commands-Factions-Ally-RequestSent")
						.replace("{relationName}", RELATION.getDisplayName())
						.replace("{otherFactionName}", faction.getFormattedName(playerFaction)));

				faction.broadcast(Lang.of("Commands-Factions-Ally-RequestReceived")
						.replace("{relationName}", RELATION.getDisplayName())
						.replace("{otherFactionName}", playerFaction.getFormattedName(faction)));
			}

			@Override
			public void onFail(FailReason reason) {
				sender.sendMessage(Lang.of("commands.error.faction_not_found", args[1]));
			}
		});

	}

	@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			return Collections.emptyList();
		}

		List<String> results = new ArrayList<>();
		for (Player target : Bukkit.getOnlinePlayers())
			if (!target.equals(player) && player.canSee(target) && !results.contains(target.getName())) {
				Faction targetFaction = null;
				try {
					targetFaction = plugin.getFactionManager().getPlayerFaction(target);
				} catch (NoFactionFoundException e) {
				}

				if (targetFaction != null && playerFaction != targetFaction)
					if (playerFaction.getRequestedRelations().get(targetFaction.getUniqueID()) != RELATION)
						results.add(targetFaction.getName());
			}

		return results;
	}



}
