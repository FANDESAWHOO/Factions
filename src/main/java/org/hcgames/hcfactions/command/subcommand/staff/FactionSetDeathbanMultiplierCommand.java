package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactionSetDeathbanMultiplierCommand extends FactionCommand {

	private static final double MIN_MULTIPLIER = 0.0;
	private static final double MAX_MULTIPLIER = 5.0;

	private final HCFactions plugin;

	public FactionSetDeathbanMultiplierCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}


	 @Command(name = "faction.setdeathbanmultiplier", description = "Sets the deathban multiplier of a faction.", permission = "factions.command.setdeathbanmultiplier", aliases = {"f.setdeathbanmultiplier"}, usage = "/<command> setdtr <playerName|factionName> <newMultiplier>",  playerOnly = false, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 String args[] = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 3) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command> setdtr <playerName|factionName> <newMultiplier>");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], Faction.class, new SearchCallback<Faction>() {
			@Override
			public void onSuccess(Faction faction) {
				Double multiplier = JavaUtils.tryParseDouble(args[2]);

				if (multiplier == null) {
					player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
					return;
				}

				if (multiplier < MIN_MULTIPLIER) {
					player.sendMessage(ChatColor.RED + "Deathban multipliers may not be less than " + MIN_MULTIPLIER + '.');
					return;
				}

				if (multiplier > MAX_MULTIPLIER) {
					player.sendMessage(ChatColor.RED + "Deathban multipliers may not be more than " + MAX_MULTIPLIER + '.');
					return;
				}

				double previousMultiplier = faction.getDeathbanMultiplier();
				faction.setDeathbanMultiplier(multiplier);

				org.bukkit.command.Command.broadcastCommandMessage(player, ChatColor.YELLOW + "Set deathban multiplier of " + faction.getName() + " from " + previousMultiplier + " to " + multiplier + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});

		return;
	}
}

