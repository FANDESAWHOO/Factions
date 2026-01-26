package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionDisbandCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionDisbandCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.demote", description = "Disband your faction.", aliases = { "f.disband"}, usage = "/f disband",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.isRaidable() && !Configuration.kitMap) { //  && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld()
			player.sendMessage(Lang.of("Commands-Factions-Disband-Raidable"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			player.sendMessage(Lang.of("Commands-Factions-Disband-LeaderRequired"));
			return;
		}
		plugin.getFactionManager().removeFaction(playerFaction, player);

	}
}
