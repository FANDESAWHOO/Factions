package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public class FactionLeaveCommand extends SimpleSubCommand {
	private final HCFactions plugin;

	public FactionLeaveCommand() {
		super("leave");
		setDescription("Leave your current faction.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}

	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can leave faction.");
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		UUID uuid = player.getUniqueId();
		if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
			sender.sendMessage(Lang.of("Commands-Factions-Leave-CannotLeaveAsLeader")
					.replace("{commandLabel}", getLabel()));
			//sender.sendMessage(ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " +
			//       ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');

			return;
		}

		if (playerFaction.removeMember(player, player, player.getUniqueId(), false, false)) {
			sender.sendMessage(Lang.of("Commands-Factions-Leave-LeaveSuccess"));
			//sender.sendMessage(ChatColor.YELLOW + "Successfully left the faction.");
			playerFaction.broadcast(Lang.of("Commands-Factions-Leave-LeaveBroadcast")
					.replace("{sender}", sender.getName()));
			//playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
		}

	}
}
