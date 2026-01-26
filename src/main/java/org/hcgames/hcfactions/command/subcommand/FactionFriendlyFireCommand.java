package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionFriendlyFireCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionFriendlyFireCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.friendlyfire", description ="Toggle friendly fire.", aliases = { "faction.damage", "f.ff", "f.friendlyfire"}, usage = "/f ff",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerFaction playerFaction;

		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage("Must be an Officer or higher...");
			return;
		}
		if (playerFaction.isFriendly_fire()) playerFaction.broadcast("An officer has toggled off the friendly fire...");
		else playerFaction.broadcast("An officer has toggled on the friendly fire...");
		playerFaction.setFriendly_fire(!playerFaction.isFriendly_fire());
	}
}
