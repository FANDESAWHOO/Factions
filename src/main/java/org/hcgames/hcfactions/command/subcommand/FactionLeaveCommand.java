package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public final class FactionLeaveCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionLeaveCommand() {
		super("leave");
		setDescription("Leave your current faction.");
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

		UUID uuid = player.getUniqueId();
		if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
			tell(Lang.of("Commands-Factions-Leave-CannotLeaveAsLeader")
					.replace("{commandLabel}", getLabel()));
			//tell(ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " +
			//       ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');

			return;
		}

		if (playerFaction.removeMember(player, player, player.getUniqueId(), false, false)) {
			tell(Lang.of("Commands-Factions-Leave-LeaveSuccess"));
			//tell(ChatColor.YELLOW + "Successfully left the faction.");
			playerFaction.broadcast(Lang.of("Commands-Factions-Leave-LeaveBroadcast")
					.replace("{sender}", sender.getName()));
			//playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
		}

	}
}
