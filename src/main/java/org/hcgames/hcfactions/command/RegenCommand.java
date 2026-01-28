package org.hcgames.hcfactions.command;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.RegenStatus;
import org.hcgames.hcfactions.util.DurationFormatter;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class RegenCommand { // TODO: check all permissions.

	private final HCFactions plugin;

	public RegenCommand() {
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
		plugin = HCFactions.getInstance();
	}


	public long getRemainingRegenMillis(PlayerFaction faction) {
		long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
		double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
		return (long) ((10 / 60) * dtrRequired) - millisPassedSinceLastUpdate;
	}

    @Command(name = "regen", description = "The main command for Regenn", usage = "/location",  playerOnly = true, adminsOnly = false)
    public void onCommand(CommandArgs arg) {

		Player player = arg.getPlayer();
		PlayerFaction playerFaction;

		if (!plugin.getFactionManager().hasFaction(player)) {
			player.sendMessage(Lang.of("Error-Messages.NotInFaction"));
			return;
		}

		playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		RegenStatus regenStatus = playerFaction.getRegenStatus();
		switch (regenStatus) {
			case FULL:
				player.sendMessage(Lang.of("Commands.Regen.Full"));
				return;
			case PAUSED:
				player.sendMessage(Lang.of("Commands.Regen.Paused")
						.replace("{dtrFreezeTimeLeft}", DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true)));
				return;
			case REGENERATING:
				player.sendMessage(Lang.of("Commands.Regen.Regenerating")
						.replace("{regenSymbol}", regenStatus.getSymbol())
						.replace("{factionDeathsUntilRaidable}", String.valueOf(playerFaction.getDeathsUntilRaidable()))
						.replace("{factionDTRIncrement}", String.valueOf(Configuration.factionDtrUpdateIncrement))
						.replace("{factionDTRIncrementWords}", String.valueOf(DurationFormatter.getRemaining(Configuration.factionDtrUpdateIncrement.longValue(), false))
								.replace("{factionDTRETA}", DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true))));
				return;
		}

		player.sendMessage(Lang.of("Commands.Regen.Unknown"));

	}
}
