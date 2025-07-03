package org.hcgames.hcfactions.command.subcommand;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import java.util.concurrent.TimeUnit;

public final class FactionRenameCommand extends FactionSubCommand {
	private static final long FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
	private static final String FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FACTION_RENAME_DELAY_MILLIS, true, true);

	private final HCFactions plugin;

	public FactionRenameCommand() {
		super("rename|changename|setname");
		setDescription("Change the name of your faction.");
		plugin = HCFactions.getInstance();
	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <newFactionName>";
	}

	@Override
	public void onCommand() {
		if (!(sender instanceof Player)) {
			tell(ChatColor.RED + "Only players can create faction.");
			return;
		}

		if (args.length < 2) {
			tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		Role role = playerFaction.getMember(player.getUniqueId()).getRole();
		if (!(role == Role.LEADER || role == Role.COLEADER)) {
			//tell(ChatColor.RED + "You must be a faction leader to edit the name.");
			tell(Lang.of("Commands-Factions-Rename-CoLeaderRequired"));
			return;
		}

		String newName = args[1];

		if (Configuration.factionDisallowedNames.contains(newName)) {
			//tell(ChatColor.RED + "'" + newName + "' is a blocked faction name.");
			tell(Lang.of("Commands-Factions-Rename-BlockedName")
					.replace("{factionName}", newName));
			return;
		}

		int value = Configuration.factionNameMinCharacters;

		if (newName.length() < value) {
			//tell(ChatColor.RED + "Faction names must have at least " + value + " characters.");
			tell(Lang.of("Commands-Factions-Rename-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (newName.length() > value) {
			//tell(ChatColor.RED + "Faction names cannot be longer than " + value + " characters.");
			tell(Lang.of("Commands-Factions-Rename-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(newName)) {
			//tell(ChatColor.RED + "Faction names may only be alphanumeric.");
			tell(Lang.of("Commands-Factions-Rename-MustBeAlphanumeric"));
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(newName) != null) {
				//tell(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
				tell(Lang.of("Commands-Factions-Rename-NameAlreadyExists")
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

		playerFaction.setName(args[1], sender);
		return;
	}
}
