package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Arrays;

public final class FactionMessageCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionMessageCommand() {
		plugin = HCFactions.getInstance();
	}



	@Command(name = "faction.message", description = "Sends a message to your faction.", aliases = {"faction.msg","f.msg", "f.message"}, usage = "/f msg <bla bla bla>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f msg <bla bla bla>"));
			return;
		}

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(arg.getArgs(), 1, arg.length())));
		for (Player target : playerFaction.getOnlinePlayers()) target.sendMessage(format);

		return;
	}
}
