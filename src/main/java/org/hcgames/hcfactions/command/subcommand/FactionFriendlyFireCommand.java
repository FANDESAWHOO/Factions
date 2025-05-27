package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

public class FactionFriendlyFireCommand extends SimpleSubCommand {

	private final HCFactions plugin;

	public FactionFriendlyFireCommand(HCFactions plugin) {
		super("friendlyfire | damage | ff");
		setDescription("Toggle friendly fire.");
		this.plugin = plugin;
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		checkConsole();
		Player player = (Player) sender;
		PlayerFaction playerFaction;

		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage("Must be an Officer or higher...");
			return;
		}
		if(playerFaction.isFriendly_fire()) playerFaction.broadcast("An officer has toggled off the friendly fire...");
		else playerFaction.broadcast("An officer has toggled on the friendly fire...");
		playerFaction.setFriendly_fire(!playerFaction.isFriendly_fire());
	}
}
