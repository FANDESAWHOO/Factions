package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionSnowCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionSnowCommand() {
		plugin = HCFactions.getInstance();

	}


	@Command(name = "faction.snow", description = "Toggle snow fall in your faction", aliases = {"f.snow"}, usage = "/f snow",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
       /* if(!BreakConfig.christmasMap){
            tell(ChatColor.RED + "Requires christmas map break config option.");
            return true;
        }*/
		Player player = arg.getPlayer();
		ClaimableFaction faction;
		boolean own = false;

		if (arg.length() < 1) {

			if (!plugin.getFactionManager().hasFaction(player)) {
				player.sendMessage(ChatColor.RED + "You are not in a faction.");
				return;
			}

			PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
			if (playerFaction.getMember(player).getRole() == Role.MEMBER) {
				player.sendMessage(Lang.of("Commands-Factions-Kick-OfficerRequired"));
				return;
			}

			faction = playerFaction;
			own = true;
		} else if (arg.getSender() instanceof ConsoleCommandSender || player.hasPermission("factions.command.staff")) {
			Faction found;

			try {
				found = plugin.getFactionManager().getFaction(arg.getArgs(0));
			} catch (NoFactionFoundException e) {
				player.sendMessage(ChatColor.RED + "No faction found by name " + arg.getArgs(0));
				return;
			}

			if (!(found instanceof ClaimableFaction)) {
				player.sendMessage(ChatColor.RED + "You cannot toggle snow for that faction.");
				return;
			}

			faction = (ClaimableFaction) found;
		} else {
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}

		boolean newState = !faction.isSnowfall();
		faction.setSnowfall(newState);
		player.sendMessage(Lang.of("commands.snow." + (own ? "own" : "other"), newState ? "enabled" : "disabled", faction.getName()));
		return;
	}
}
