package org.hcgames.hcfactions.command.subcommand.staff;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.cuboid.Cuboid;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


/**
 * Changed from WorldEdit to Custom Wand
 * Used to claim land for other {@link ClaimableFaction}s.
 */
public final class FactionClaimForCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionClaimForCommand() {
		plugin = HCFactions.getInstance();
		//   this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.claimfor", description = "Claims land for another faction.",permission = "factions.command.claimfor", aliases = { "f.claimfor"}, usage = "/<command>  claimfor",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
			Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("commands.error.usage", "/<command>  claimfor"));
			return;
		}

		plugin.getFactionManager().advancedSearch(arg.getArgs(0), ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {

			@Override
			public void onSuccess(ClaimableFaction faction) {
				Cuboid pos = HCFactions.getInstance().getWandManager().getSelection(player);

				if (pos == null) {
					player.sendMessage(ChatColor.RED + "You need to select 2 positions with the claim wand. Use /claimwand and right/left click with the stick.");
					return;
				}
				Location selection = pos.getMinimumPoint();
				Location selection2 = pos.getMaximumPoint();
                if (selection == null || selection2 == null) {
                	player.sendMessage(ChatColor.RED + "You need to select 2 positions with the claim wand. Use /claimwand and right/left click with the stick.");
					return;
                }
				if (faction.addClaim(new Claim(faction, selection, selection2), player))
					player.sendMessage(Lang.of("Commands.staff.claimfor.claimed", faction.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("commands.error.faction_not_found", arg.getArgs(0)));
			}
		});

		return;
	}

}
