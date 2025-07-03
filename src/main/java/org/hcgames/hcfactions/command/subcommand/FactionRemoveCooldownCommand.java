package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.settings.Lang;

public final class FactionRemoveCooldownCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionRemoveCooldownCommand() {
		super("removecooldown");
		setDescription("Removes a faction cool down for a player.");
		plugin = HCFactions.getInstance();

	}


	@Override
	public String getUsage() {
		return Lang.of("Commands.Factions.RemoveCooldown.Usage");
	}

	@Override
	public void onCommand() {
		if (args.length < 3) {
			tell(getUsage());
			return;
		}

		Player player = plugin.getServer().getPlayer(args[1]);

		if (player == null) {
			tell(Lang.of("Error-Messages.InvalidPlayer").replace("{player}", args[1]));
			return;
		}

		Faction faction;
		try {
			faction = plugin.getFactionManager().getFaction(args[2]);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Error-Messages.InvalidFaction").replace("{faction}", args[2]));
			return;
		}

		if (!(faction instanceof PlayerFaction)) {
			tell(Lang.of("Commands.Factions.RemoveCooldown.InvalidFactionType").replace("{faction}", faction.getName()));
			return;
		}

		PlayerFaction pFaction = (PlayerFaction) faction;

		if (!pFaction.hasCooldown(player.getUniqueId())) {
			tell(Lang.of("Commands.Factions.RemoveCooldown.NotOnCooldown").replace("{player}", player.getName()));
			return;
		}

		pFaction.removeCooldown(player.getUniqueId());
		tell(Lang.of("Commands.Factions.RemoveCooldown.CooldownRemoved").replace("{player}", player.getName()).replace("{faction}", faction.getName()));

	}

}
