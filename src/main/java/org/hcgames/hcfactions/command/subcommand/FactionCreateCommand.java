package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableMap;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.JavaUtils;

import java.util.UUID;

public final class FactionCreateCommand extends FactionCommand {
	private final static ImmutableMap<String, UUID> RESTRICTED_NAMES = ImmutableMap.<String, UUID>builder().put("test", UUID.fromString("42e61ded-4f50-46c7-82a8-723bdcda4991")).put("yaml", UUID.fromString("ea50290c-8225-4222-8664-32f2f5070974")).build();
	private final HCFactions plugin;

	public FactionCreateCommand() {
		plugin = HCFactions.getInstance();
	}
	 @Command(name = "faction.create", description = "Create a faction.", aliases = { "f.create","f.make","f.define","faction.make","faction.define"}, usage = "/f create [factionName]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		 if (arg.length() < 1) {
				player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f create [factionName]"));
				return;
			}

			String name = arg.getArgs(0);

			if (Configuration.factionDisallowedNames.contains(name)) {
				player.sendMessage(Lang.of("Commands-Factions-Create-BlockedName").replace("{factionName}", name));
				return;
			}

			String nameLower = name.toLowerCase();
			if (RESTRICTED_NAMES.containsKey(nameLower) && !player.getUniqueId().equals(RESTRICTED_NAMES.get(nameLower))) {
				player.sendMessage(Lang.of("Commands-Factions-Create-BlockedName").replace("{factionName}", name));
				return;
			}

			int value = Configuration.factionNameMinCharacters;

			if (name.length() < value) {
				player.sendMessage(Lang.of("Commands-Factions-Create-MinimumChars")
						.replace("{minChars}", String.valueOf(value)));
				return;
			}

			value = Configuration.factionNameMaxCharacters;

			if (name.length() > value) {
				player.sendMessage(Lang.of("Commands-Factions-Create-MaximumChars")
						.replace("{maxChars}", String.valueOf(value)));
				return;
			}

			if (!JavaUtils.isAlphanumeric(name)) {
				player.sendMessage(Lang.of("Commands-Factions-Create-MustBeAlphanumeric"));
				return;
			}

			try {
				if (plugin.getFactionManager().getFaction(name) != null) {
					player.sendMessage(Lang.of("Commands-Factions-Create-NameAlreadyExists")
							.replace("{factionName}", name));
					return;
				}
			} catch (NoFactionFoundException e) {
			}

			try {
				if (plugin.getFactionManager().getPlayerFaction(player) != null) {
					player.sendMessage(Lang.of("Commands-Factions-Create-AlreadyInFaction"));
					return;
				}
			} catch (NoFactionFoundException e) {
			}

			plugin.getFactionManager().createFaction(new PlayerFaction(name), player);
	 }

}
