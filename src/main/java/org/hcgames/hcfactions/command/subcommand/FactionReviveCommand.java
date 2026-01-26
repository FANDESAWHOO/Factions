package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionReviveCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionReviveCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.revive", description = "Revive a player with faction lives", aliases = {"f.revive"}, usage = "/f revive <player>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();

		if (arg.length() < 1) {
			player.sendMessage("/f revive <player>");
			return;
		}

		PlayerFaction faction;
		try {
			faction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Error-Messages.NotInFaction"));
			//invalid faction
			return;
		}

		if (faction.getMember(player).getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands.Factions.Revive.Officer-Required"));
			//officer required
			return;
		}

		if (faction.getLives() <= 0) {
			player.sendMessage(Lang.of("Commands.Factions.Revive.Not-Enough").replace("{player}", arg.getArgs(0)));
			//faction doesn't have enough lives
			return;
		}

		/**
		 * REMOVED TEMPORALLY BECAUSE
		 * FACTION SUB COMMAND RIGHT NOW
		 * REALLY DOESN'T HAVE ANYTHING METHOD
		 * LIKE THAT AND I DONT WANNA LOSE TIME RIGHT NOW
		 * XD
		 *
		 findOfflinePlayer(args[1], deadPlayer -> {
		 if(deadPlayer == null){
		 tell(Lang.of("Error-Messages.InvalidPlayer").replace("{player}", args[1]));
		 //player not found
		 return;
		 }

		 /*  FactionUser user;
		 if(!HCF.getPlugin().getUserManager().userExists(deadPlayer.getUniqueId()) || ((user = HCF.getPlugin().getUserManager().getUser(deadPlayer.getUniqueId())) == null) || user.getDeathban() == null){
		 tell(Lang.of("Commands.Factions.Revive.Not-Deathbanned"));
		 //not deathbanned
		 return;
		 }

		 faction.setLives(faction.getLives() - 1);
		 //  user.removeDeathban();
		 faction.broadcast(Lang.of("Commands.Factions.Revive.Broadcast").replace("{player}",
		 player.getName()).replace("{victim}", deadPlayer.getName()));
		 //broadcast
		 });*/

		return;
	}
}
