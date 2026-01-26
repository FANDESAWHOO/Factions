package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
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

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.*;

public final class FactionAllyCommand extends FactionCommand {

	private static final Relation RELATION = Relation.ALLY;

	private final HCFactions plugin;

	public FactionAllyCommand() {
		plugin = HCFactions.getInstance();

	}


	 @Command(name = "faction.ally", description = "Make an ally pact with other factions.", aliases = { "f.ally","f.allies"}, usage = "/<command> <aliases> <faction>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
	    if (Configuration.factionMaxAllies <= 0) {
				player.sendMessage(Lang.of("Commands-Factions-Ally-AlliesDisabled"));
				return;
		}
		if (arg.getArgs().length < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f ally <faction>"));
			return;
		}
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Ally-OfficerRequired"));
			return;
		}

		plugin.getFactionManager().advancedSearch(arg.getArgs(0), PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				if (playerFaction == faction) {
					player.sendMessage(Lang.of("Commands-Factions-Ally-RequestingOwnFaction").replace("{relationName}", RELATION.getDisplayName()));
					return;
				}

				Collection<UUID> allied = playerFaction.getAllied();

				if (allied.size() >= Configuration.factionMaxAllies) {
					player.sendMessage(Lang.of("Commands-Factions-Ally-OwnFactionLimitReached").replace("{allyLimit}", String.valueOf(Configuration.factionMaxAllies)));
					return;
				}

				if (faction.getAllied().size() >= Configuration.factionMaxAllies) {
					player.sendMessage(Lang.of("Commands-Factions-Ally-OtherFactionLimitReached")
							.replace("{allyLimit}", String.valueOf(Configuration.factionMaxAllies))
							.replace("{otherFactionName}", faction.getFormattedName(player)));

					return;
				}

				if (allied.contains(faction.getUniqueID())) {
					player.sendMessage(Lang.of("Commands-Factions-Ally-RequestingOwnFaction")
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
					player.sendMessage(Lang.of("Commands-Factions-Ally-AlreadyRequested")
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
				player.sendMessage(Lang.of("commands.error.faction_not_found", arg.getArgs(0)));
			}
		});
	 }
	
}
