package org.hcgames.hcfactions.command.subcommand.staff;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactionSetDtrRegenCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionSetDtrRegenCommand() {
		super("setdtrregen|setdtrregeneration");
		setDescription("Sets the DTR cooldown of a faction.");
		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}


	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <playerName|factionName> <newRegen>";
	}

	@Override
	public void onCommand() {
		if (args.length < 3) {
			tell(ChatColor.RED + "Usage: " + getUsage());
			return;
		}

		long newRegen = JavaUtils.parse(args[2]);

		if (newRegen < 0L) {
			tell(ChatColor.RED + "Faction DTR regeneration duration cannot be negative.");
			return;
		}

		if (newRegen > FactionManager.MAX_DTR_REGEN_MILLIS) {
			tell(ChatColor.RED + "Cannot set factions DTR regen above " + FactionManager.MAX_DTR_REGEN_WORDS + ".");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				long previousRegenRemaining = faction.getRemainingRegenerationTime();
				faction.setRemainingRegenerationTime(newRegen);

				Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR regen of " + faction.getName() +
						(previousRegenRemaining > 0L ? " from " + DurationFormatUtils.formatDurationWords(previousRegenRemaining, true, true) : "") + " to " +
						DurationFormatUtils.formatDurationWords(newRegen, true, true) + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				tell(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

	@Override
	public List<String> tabComplete() {
		if (args.length != 2) return Collections.emptyList();
		else if (args[1].isEmpty()) return null;
		else {
			List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
			Player senderPlayer = sender instanceof Player ? ((Player) sender) : null;
			// Make sure the player can see.
			for (Player player : Bukkit.getOnlinePlayers())
				if (senderPlayer == null || senderPlayer.canSee(player)) results.add(player.getName());

			return results;
		}
	}
}
