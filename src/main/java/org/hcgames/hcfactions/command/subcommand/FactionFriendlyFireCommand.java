package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

public final class FactionFriendlyFireCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionFriendlyFireCommand() {
		super("friendlyfire|damage|ff");
		setDescription("Toggle friendly fire.");
		plugin = HCFactions.getInstance();

	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		Player player = (Player) sender;
		PlayerFaction playerFaction;

		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			tell("Must be an Officer or higher...");
			return;
		}
		if (playerFaction.isFriendly_fire()) playerFaction.broadcast("An officer has toggled off the friendly fire...");
		else playerFaction.broadcast("An officer has toggled on the friendly fire...");
		playerFaction.setFriendly_fire(!playerFaction.isFriendly_fire());
	}
}
