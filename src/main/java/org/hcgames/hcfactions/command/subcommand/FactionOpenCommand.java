package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class FactionOpenCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionOpenCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.open", description = "Opens the faction to the public.", aliases = {"f.open"}, usage = "/f open",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerFaction playerFaction = null;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

		if (factionMember.getRole() != Role.LEADER) {
			player.sendMessage(Lang.of("Commands-Factions-Open-LeaderRequired"));
			return;
		}

		boolean newOpen = !playerFaction.isOpen();
		playerFaction.setOpen(newOpen);
		playerFaction.broadcast(Lang.of("Commands-Factions-Open-Broadcast")
				.replace("{player}", player.getName())
				.replace("{state}", (newOpen ? Lang.of("Commands-Factions-Open-OpenedText") : Lang.of("Commands-Factions-Open-ClosedText"))));
		//playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? ChatColor.GREEN + "opened" : ChatColor.RED + "closed") + ChatColor.YELLOW + " the faction to public.");

	}
}
