package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactionShowCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionShowCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.show", description = "Get details about a faction.", aliases = {"faction.i","faction.info","faction.who","f.i","f.info","f.who","f.show"}, usage = "/f show [factionName]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Faction namedFaction = null;
        Player player = arg.getPlayer();
		if (arg.length() < 1) {
			try {
				namedFaction = plugin.getFactionManager().getPlayerFaction(player);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}

			if (namedFaction == null) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}

			namedFaction.sendInformation(player);
		} else {
			try {
				namedFaction = plugin.getFactionManager().getFaction(arg.getArgs(0));
				namedFaction.sendInformation(player);
			} catch (NoFactionFoundException ignored) {
			}

			Faction finalNamedFaction = namedFaction;
			plugin.getFactionManager().advancedSearch(arg.getArgs(0), PlayerFaction.class, new SearchCallback<PlayerFaction>() {
				@Override
				public void onSuccess(PlayerFaction faction) {
					if (finalNamedFaction != null && finalNamedFaction.equals(faction)) return;
					faction.sendInformation(player);
				}

				@Override
				public void onFail(FailReason reason) {
					if (finalNamedFaction == null)
						player.sendMessage(Lang.of("Commands.error.faction_not_found", arg.getArgs(0)));
				}
			}, true);

		}

		return;
	}

}
