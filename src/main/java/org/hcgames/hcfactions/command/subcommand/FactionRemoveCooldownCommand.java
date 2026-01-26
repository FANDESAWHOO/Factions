package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.lib.PlayerUtil;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionRemoveCooldownCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionRemoveCooldownCommand() {
		plugin = HCFactions.getInstance();

	}

	public String getUsage() {
		return Lang.of("Commands.Factions.RemoveCooldown.Usage");
	}

	@Command(name = "faction.removecooldown", description = "Removes a faction cooldown for a player.", aliases = {"f.removecooldown"}, usage = "/f removecooldown <player> <faction>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player pepe = arg.getPlayer();
		if (arg.length() < 2) {
			pepe.sendMessage(getUsage());
			return;
		}

		Player player = PlayerUtil.getPlayerByNick(arg.getArgs(0), false);

		if (player == null) {
			pepe.sendMessage(Lang.of("Error-Messages.InvalidPlayer").replace("{player}", arg.getArgs(0)));
			return;
		}

		Faction faction;
		try {
			faction = plugin.getFactionManager().getFaction(arg.getArgs(1));
		} catch (NoFactionFoundException e) {
			pepe.sendMessage(Lang.of("Error-Messages.InvalidFaction").replace("{faction}", arg.getArgs(1)));
			return;
		}

		if (!(faction instanceof PlayerFaction)) {
			pepe.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.InvalidFactionType").replace("{faction}", faction.getName()));
			return;
		}

		PlayerFaction pFaction = (PlayerFaction) faction;

		if (!pFaction.hasCooldown(player.getUniqueId())) {
			pepe.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.NotOnCooldown").replace("{player}", player.getName()));
			return;
		}

		pFaction.removeCooldown(player.getUniqueId());
		pepe.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.CooldownRemoved").replace("{player}", player.getName()).replace("{faction}", faction.getName()));

	}

}
