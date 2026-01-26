package org.hcgames.hcfactions.command.subcommand;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.concurrent.TimeUnit;

public final class FactionRenameCommand extends FactionCommand {
	private static final long FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
	private static final String FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FACTION_RENAME_DELAY_MILLIS, true, true);

	private final HCFactions plugin;

	public FactionRenameCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.rename", description = "Change the name of your faction.", aliases = {"faction.changename","faction.setname","f.changename","f.setname","f.rename"}, usage = "/f rename <newFactionName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
     Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f rename <newFactionName>"));
			return;
		}

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		Role role = playerFaction.getMember(player.getUniqueId()).getRole();
		if (!(role == Role.LEADER || role == Role.COLEADER)) {
			//player.sendMessage(ChatColor.RED + "You must be a faction leader to edit the name.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-CoLeaderRequired"));
			return;
		}

		String newName = arg.getArgs(0);

		if (Configuration.factionDisallowedNames.contains(newName)) {
			//player.sendMessage(ChatColor.RED + "'" + newName + "' is a blocked faction name.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-BlockedName")
					.replace("{factionName}", newName));
			return;
		}

		int value = Configuration.factionNameMinCharacters;

		if (newName.length() < value) {
			//player.sendMessage(ChatColor.RED + "Faction names must have at least " + value + " characters.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (newName.length() > value) {
			//player.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + value + " characters.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(newName)) {
			//player.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MustBeAlphanumeric"));
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(newName) != null) {
				//player.sendMessage(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
				player.sendMessage(Lang.of("Commands-Factions-Rename-NameAlreadyExists")
						.replace("{factionNewName}", newName));
				return;
			}
		} catch (NoFactionFoundException e) {
		}

		long difference = (playerFaction.getLastRenameMillis() - System.currentTimeMillis()) + FACTION_RENAME_DELAY_MILLIS;

		if (!player.isOp() && difference > 0L) {
			player.sendMessage(Lang.of("Commands-Factions-Rename-RenameDelay")
					.replace("{factionRenameDelay}", FACTION_RENAME_DELAY_WORDS)
					.replace("{factionRenameTimeLeft}", DurationFormatUtils.formatDurationWords(difference, true, true)));
			//player.sendMessage(ChatColor.RED + "There is a faction rename delay of " + FACTION_RENAME_DELAY_WORDS + ". Therefore you need to wait another " +
			//        DurationFormatUtils.formatDurationWords(difference, true, true) + " to rename your faction.");

			return;
		}

		playerFaction.setName(arg.getArgs(0), player);
		return;
	}
}
