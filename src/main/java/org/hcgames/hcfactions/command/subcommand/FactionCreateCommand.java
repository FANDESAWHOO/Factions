package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public class FactionCreateCommand extends FactionSubCommand {
	private final HCFactions plugin;
	private final static ImmutableMap<String, UUID> RESTRICTED_NAMES = ImmutableMap.<String, UUID>builder().put("test", UUID.fromString("42e61ded-4f50-46c7-82a8-723bdcda4991")).put("yaml", UUID.fromString("ea50290c-8225-4222-8664-32f2f5070974")).build();

	public FactionCreateCommand() {
		super("create | make | define");
		setDescription("Create a faction.");
		plugin = HCFactions.getInstance();
	}
    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <factionName>";
	}

	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	public void onCommand() {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.of("Commands-ConsoleOnly"));
			return;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		String name = args[1];

		if (Configuration.factionDisallowedNames.contains(name)) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-BlockedName").replace("{factionName}", name));
			return;
		}

		String nameLower = name.toLowerCase();
		if(RESTRICTED_NAMES.containsKey(nameLower) && !((Player) sender).getUniqueId().equals(RESTRICTED_NAMES.get(nameLower))){
			sender.sendMessage(Lang.of("Commands-Factions-Create-BlockedName").replace("{factionName}", name));
			return;
		}

		int value = Configuration.factionNameMinCharacters;

		if (name.length() < value) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (name.length() > value) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(name)) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MustBeAlphanumeric"));
			return;
		}

		try {
			if(plugin.getFactionManager().getFaction(name) != null){
				sender.sendMessage(Lang.of("Commands-Factions-Create-NameAlreadyExists")
						.replace("{factionName}", name));
				return;
			}
		} catch (NoFactionFoundException e) {}

		try {
			if (plugin.getFactionManager().getPlayerFaction((Player) sender) != null) {
				sender.sendMessage(Lang.of("Commands-Factions-Create-AlreadyInFaction"));
				return;
			}
		} catch (NoFactionFoundException e) {}

		plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);

	}
}
