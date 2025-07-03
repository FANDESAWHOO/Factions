package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;


public final class FactionOpenCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionOpenCommand() {
		super("open");
		setDescription("Opens the faction to the public.");
		plugin = HCFactions.getInstance();
	}


	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		Player player = (Player) sender;
		PlayerFaction playerFaction = null;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

		if (factionMember.getRole() != Role.LEADER) {
			tell(Lang.of("Commands-Factions-Open-LeaderRequired"));
			return;
		}

		boolean newOpen = !playerFaction.isOpen();
		playerFaction.setOpen(newOpen);
		playerFaction.broadcast(Lang.of("Commands-Factions-Open-Broadcast")
				.replace("{player}", sender.getName())
				.replace("{state}", (newOpen ? Lang.of("Commands-Factions-Open-OpenedText") : Lang.of("Commands-Factions-Open-ClosedText"))));
		//playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? ChatColor.GREEN + "opened" : ChatColor.RED + "closed") + ChatColor.YELLOW + " the faction to public.");

	}
}
