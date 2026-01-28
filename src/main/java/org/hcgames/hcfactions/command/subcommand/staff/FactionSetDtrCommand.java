package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to set the DTR of {@link Faction}s.
 */
public final class FactionSetDtrCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionSetDtrCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.setdtr", description = "Sets the DTR of a faction.", permission = "factions.command.setdtr", aliases = {"f.setdtr"}, usage = "/<command> setdtr <playerName|factionName> <newDtr>",  playerOnly = false, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 3) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command> setdtr <playerName|factionName> <newDtr>");
			return;
		}

		Double[] newDTR = {JavaUtils.tryParseDouble(args[2])};

		if (newDTR[0] == null) {
			player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
			return;
		}

		if (arg.getSender() instanceof Player)
			if (newDTR[0] <= 0 && !player.hasPermission("hcf.command.faction.argument.setdtr.raidable")) {
				player.sendMessage("You don't have permission to make factions raidable.");
				return;
			}

        /*if (args[1].equalsIgnoreCase("all")) {
            for (Faction faction : plugin.getFactionManager().getFactions()) {
                if (faction instanceof PlayerFaction) {
                    ((PlayerFaction) faction).setDeathsUntilRaidable(newDTR[0]);
                }
            }

            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of all factions to " + newDTR[0] + '.');
            return;
        }*/

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				double previousDtr = faction.getDeathsUntilRaidable();
				newDTR[0] = faction.setDeathsUntilRaidable(newDTR[0]);

				org.bukkit.command.Command.broadcastCommandMessage(player, ChatColor.YELLOW + "Set DTR of " + faction.getName() + " from " + previousDtr + " to " + newDTR[0] + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});


		return;
	}

}
