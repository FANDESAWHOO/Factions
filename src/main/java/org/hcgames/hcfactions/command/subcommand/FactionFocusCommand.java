package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;

import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.focus.FocusTarget;
import org.hcgames.hcfactions.util.PlayerUtil;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionFocusCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionFocusCommand() {
		plugin = HCFactions.getInstance();
	}

	public String getUsage() {
		return Lang.of("Commands.Factions.Focus.Usage");
	}

	@Command(name = "faction.focus", description = "Focus on a player or argument", aliases = { "faction.unfocus", "f.focus", "f.unfocus"}, usage = "/f focus <faction>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();

		if (arg.length() < 1) {
			player.sendMessage(getUsage());
			return;
		}

		PlayerFaction faction;
		try {
			faction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Error-Messages.NotInFaction"));
			//not in faction
			return;
		}

		String name = arg.getArgs(0);

		Player targetPlayer = PlayerUtil.getPlayerByNick(name, false);
		Faction targetFaction;

		if (targetPlayer == null) {
			try {
				targetFaction = plugin.getFactionManager().getFaction(name);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands.Factions.Focus.Error.NotFound").replace("{name}", name));
				//player or faction not found
				return;
			}

			if (!(targetFaction instanceof PlayerFaction)) {
				player.sendMessage(Lang.of("Commands.Factions.Focus.Error.NotPlayerFaction"));
				//faction isn't player faction
				return;
			}

			handleFactionFocus(player, faction, (PlayerFaction) targetFaction);
			return;
		}

		handleFactionFocus(player, faction, targetPlayer);
		return;
	}

	private void handleFactionFocus(CommandSender sender, PlayerFaction current, PlayerFaction target) {
		if (current.isFocused(target.getUniqueID())) {
			plugin.getFactionManager().getFocusHandler().unfocus(current.fmk(target.getUniqueID()));
			current.broadcast(Lang.of("Commands.Factions.Focus.UnFocus.Faction")
					.replace("{player}", sender.getName()).replace("{focusedFaction}", target.getName()));
			return;
		}

		plugin.getFactionManager().getFocusHandler().focus(current, new FocusTarget(plugin, current, target));
		current.broadcast(Lang.of("Commands.Factions.Focus.Focus.Faction")
				.replace("{player}", sender.getName()).replace("{focusedFaction}", target.getName()));
		//focused
	}

	private void handleFactionFocus(CommandSender sender, PlayerFaction current, Player target) {
		if (current.isFocused(target.getUniqueId())) {
			plugin.getFactionManager().getFocusHandler().unfocus(current.fmk(target.getUniqueId()));
			current.broadcast(Lang.of("Commands.Factions.Focus.UnFocus.Player")
					.replace("{player}", sender.getName()).replace("{focusedPlayer}", target.getName()));
			//already focused
			return;
		}

		plugin.getFactionManager().getFocusHandler().focus(current, new FocusTarget(plugin, current, target));
		current.broadcast(Lang.of("Commands.Factions.Focus.Focus.Player")
				.replace("{player}", sender.getName()).replace("{focusedPlayer}", target.getName()));
	}

}
