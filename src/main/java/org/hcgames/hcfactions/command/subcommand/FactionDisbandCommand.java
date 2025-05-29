package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

public final class FactionDisbandCommand extends FactionSubCommand {
    private final HCFactions plugin;

	public FactionDisbandCommand(){
		super("disband");
		setDescription("Disband your faction.");
		plugin = HCFactions.getInstance();

	}
    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	public void onCommand() {
		checkConsole();

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.isRaidable() && !Configuration.kitMap) { //  && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld()
			sender.sendMessage(Lang.of("Commands-Factions-Disband-Raidable"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			sender.sendMessage(Lang.of("Commands-Factions-Disband-LeaderRequired"));
			return;
		}
		plugin.getFactionManager().removeFaction(playerFaction, sender);

	}
}
