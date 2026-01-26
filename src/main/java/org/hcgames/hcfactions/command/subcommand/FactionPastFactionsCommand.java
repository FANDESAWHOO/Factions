package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.util.uuid.UUIDHandler;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.List;
import java.util.UUID;

public class FactionPastFactionsCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionPastFactionsCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.pastfactions", description = "See past factions of a user", aliases = {"f.pastfactions"}, usage = "/f pastfactions [playerName]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		if (arg.length() < 1) {
			Player player = PlayerUtil.getPlayerByNick(arg.getArgs(0),false);

			if (player == null) plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				UUID user = UUIDHandler.getUUID(arg.getArgs(0));

				if (user == null || !HCFactions.getInstance().getUserManager().userExists(user)) {
					player.sendMessage(Lang.of("commands-Pay-UnknownPlayer").replace("{player}", arg.getArgs(0)));
					return;
				}

				List<String> pastFactions = HCFactions.getInstance().getUserManager().getUser(user).getPastFactions();
				player.sendMessage(Lang.of("commands.pastfactions.other", arg.getArgs(0), pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
			});
			else {
				List<String> pastFactions = HCFactions.getInstance().getUserManager().getUser(player.getUniqueId()).getPastFactions();
				player.sendMessage(Lang.of("commands.pastfactions.other", player.getName(), pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
			}
		}
	}
}
