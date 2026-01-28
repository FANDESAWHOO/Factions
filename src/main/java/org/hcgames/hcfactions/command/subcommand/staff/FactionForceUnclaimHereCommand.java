package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class FactionForceUnclaimHereCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceUnclaimHereCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}


	 @Command(name = "faction.forceunclaimhere", description = "Forces land unclaim where you are standing.",permission = "factions.command.forceunclaimhere", aliases = { "f.forceunclaimhere"}, usage = "/<command>  forceunclaimhere",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
			Player player = arg.getPlayer();
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage: " +"/<command>  forceunclaimhere");
			return;
		}
		Claim claimAt = plugin.getFactionManager().getClaimAt(player.getLocation());

		if (claimAt == null) {
			player.sendMessage(ChatColor.RED + "There is not a claim at your current position.");
			return;
		}

		try {
			if (claimAt.getFaction().removeClaim(claimAt, player)) {
				player.sendMessage(ChatColor.YELLOW + "Removed claim " + claimAt.getClaimUniqueID().toString() + " owned by " + claimAt.getFaction().getName() + ".");
				return;
			}
		} catch (NoFactionFoundException e) {
			player.sendMessage(ChatColor.RED + "Failed to remove claim " + claimAt.getClaimUniqueID().toString());
		}

		player.sendMessage(ChatColor.RED + "Failed to remove claim " + claimAt.getClaimUniqueID().toString());
		return;
	}
}
