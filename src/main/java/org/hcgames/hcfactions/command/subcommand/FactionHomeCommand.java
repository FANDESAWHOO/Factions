package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.TimerAPI;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Optional;
import java.util.UUID;

public final class FactionHomeCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionHomeCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.home", description = "Teleport to the faction home.", aliases = { "f.home", "f.hq"}, usage = "/f home",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();

		if (arg.length() >= 1 && arg.getArgs(0).equalsIgnoreCase("set")) {
			player.performCommand("f sethome");
			return;
		}

		UUID uuid = player.getUniqueId();


		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		Optional<Location> home = playerFaction.getHome();

		if (!home.isPresent()) {
			player.sendMessage(Lang.of("Commands-Factions-Home-NoFactionHomeSet"));
			return;
		}

		if (Configuration.maxHeightFactionHome != -1 && home.get().getY() > Configuration.maxHeightFactionHome) {
			player.sendMessage(Lang.of("Commands-Factions-Home-HomeAboveHeightLimit")
					.replace("{factionHomeHeightLimit}", String.valueOf(Configuration.maxHeightFactionHome))
					.replace("{factionHomeX}", String.valueOf(home.get().getBlockX()))
					.replace("{factionHomeZ}", String.valueOf(home.get().getBlockZ())));

			return;
		}
		TimerAPI.callHome(player, playerFaction, arg.getLabel());
	
	}
}
