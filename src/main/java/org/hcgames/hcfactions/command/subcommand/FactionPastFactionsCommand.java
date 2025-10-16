package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.util.uuid.UUIDHandler;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.settings.Lang;

import java.util.List;
import java.util.UUID;

public class FactionPastFactionsCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionPastFactionsCommand() {
		super("pastfactions");
		setDescription("See past factions of a user");
		plugin = HCFactions.getInstance();
	}


	@Override
	public String getUsage() {
		return "/f " + label + " pastfactions [name]";
	}


	@Override
	public void onCommand() {
		if (args.length < 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
				return;
			}

			List<String> pastFactions = HCFactions.getInstance().getUserManager().getUser(((Player) sender).getUniqueId()).getPastFactions();
			sender.sendMessage(Lang.of("commands.pastfactions.own", pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
		} else {
			Player player = PlayerUtil.getPlayerByNick(args[1],false);

			if (player == null) plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				UUID user = UUIDHandler.getUUID(args[1]);

				if (user == null || !HCFactions.getInstance().getUserManager().userExists(user)) {
					sender.sendMessage(Lang.of("commands-Pay-UnknownPlayer").replace("{player}", args[1]));
					return;
				}

				List<String> pastFactions = HCFactions.getInstance().getUserManager().getUser(user).getPastFactions();
				sender.sendMessage(Lang.of("commands.pastfactions.other", args[1], pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
			});
			else {
				List<String> pastFactions = HCFactions.getInstance().getUserManager().getUser(player.getUniqueId()).getPastFactions();
				sender.sendMessage(Lang.of("commands.pastfactions.other", player.getName(), pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
			}
		}
	}
}
