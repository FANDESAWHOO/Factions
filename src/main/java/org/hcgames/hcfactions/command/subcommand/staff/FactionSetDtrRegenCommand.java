package org.hcgames.hcfactions.command.subcommand.staff;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactionSetDtrRegenCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionSetDtrRegenCommand() {
		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.setdtrregen", description = "Sets the DTR cooldown of a faction.", permission = "factions.command.setdtrregen", aliases = {"f.setdtrregen"}, usage = "/<command> setdtrregen <playerName|factionName> <newDtr>",  playerOnly = false, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command> setdtrregen <playerName|factionName> <newDtr>");
			return;
		}

		long newRegen = JavaUtils.parse(args[1]);

		if (newRegen < 0L) {
			player.sendMessage(ChatColor.RED + "Faction DTR regeneration duration cannot be negative.");
			return;
		}

		if (newRegen > FactionManager.MAX_DTR_REGEN_MILLIS) {
			player.sendMessage(ChatColor.RED + "Cannot set factions DTR regen above " + FactionManager.MAX_DTR_REGEN_WORDS + ".");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[0], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				long previousRegenRemaining = faction.getRemainingRegenerationTime();
				faction.setRemainingRegenerationTime(newRegen);

				org.bukkit.command.Command.broadcastCommandMessage(player, ChatColor.YELLOW + "Set DTR regen of " + faction.getName() +
						(previousRegenRemaining > 0L ? " from " + DurationFormatUtils.formatDurationWords(previousRegenRemaining, true, true) : "") + " to " +
						DurationFormatUtils.formatDurationWords(newRegen, true, true) + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

}
