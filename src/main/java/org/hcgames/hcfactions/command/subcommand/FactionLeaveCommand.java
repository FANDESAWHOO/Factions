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

import java.util.UUID;

public final class FactionLeaveCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionLeaveCommand() {
		plugin = HCFactions.getInstance();
	}


	@Command(name = "faction.leave", description = "Leave your current faction.", aliases = { "f.leave"}, usage = "/f leave",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		UUID uuid = player.getUniqueId();
		if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
			player.sendMessage(Lang.of("Commands-Factions-Leave-CannotLeaveAsLeader")
					.replace("{commandLabel}", arg.getLabel()));
			//player.sendMessage(ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " +
			//       ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');

			return;
		}

		if (playerFaction.removeMember(player, player, player.getUniqueId(), false, false)) {
			player.sendMessage(Lang.of("Commands-Factions-Leave-LeaveSuccess"));
			//tell(ChatColor.YELLOW + "Successfully left the faction.");
			playerFaction.broadcast(Lang.of("Commands-Factions-Leave-LeaveBroadcast")
					.replace("{sender}", player.getName()));
			//playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
		}

	}
}
