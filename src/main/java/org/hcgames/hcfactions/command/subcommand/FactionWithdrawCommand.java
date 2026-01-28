package org.hcgames.hcfactions.command.subcommand;


import com.google.common.collect.ImmutableList;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.api.EconomyAPI;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.JavaUtils;


import java.util.UUID;

public final class FactionWithdrawCommand extends FactionCommand {

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final HCFactions plugin;

	public FactionWithdrawCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.withdraw", description = "Withdraws money from the faction balance.", aliases = {"faction.w","f.withdraw","f.w"}, usage = "/f withdraw <all|amount>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f withdraw <all|amount>"));
			return;
		}

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		UUID uuid = player.getUniqueId();
		FactionMember factionMember = playerFaction.getMember(uuid);

		if (factionMember.getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Withdraw-OfficerRequired"));
			//player.sendMessage(ChatColor.RED + "You must be a faction officer to withdraw money.");
			return;
		}

		int factionBalance = playerFaction.getBalance();
		Integer amount;

		if (arg.getArgs(0).equalsIgnoreCase("all")) amount = factionBalance;
		else if ((amount = (JavaUtils.tryParseInt(arg.getArgs(0)))) == null) {
			player.sendMessage(Lang.of("Commands-Factions-Deposit-InvalidNumber")
					.replace("{amount}", arg.getArgs(0)));
			return;
		}

		if (amount <= 0) {
			player.sendMessage(Lang.of("Commands-Factions-Withdraw-MustBePositive"));
			//player.sendMessage(ChatColor.RED + "Amount must be positive.");
			return;
		}

		if (amount > factionBalance) {
			player.sendMessage(Lang.of("Commands-Factions-Withdraw-MustBePositive")
					.replace("{requiredAmount}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(amount))
					.replace("{currentBalance}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(factionBalance)));
			//player.sendMessage(ChatColor.RED + "Your faction need at least " + EconomyManager.ECONOMY_SYMBOL +
			//        JavaUtils.format(amount) + " to do this, whilst it only has " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(factionBalance) + '.');

			return;
		}

		EconomyAPI.addBalance(player, amount);
		playerFaction.setBalance(factionBalance - amount);
		playerFaction.broadcast(Lang.of("Commands-Factions-Withdraw-Broadcast")
				.replace("{player}", factionMember.getRole().getAstrix() + player.getName())
				.replace("{amount}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(amount)));
		//playerFaction.broadcast(plugin.getConfiguration().getRelationColourTeammate() + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has withdrew " +
		//        ChatColor.BOLD + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + ChatColor.YELLOW + " from the faction balance.");

		return;
	}

}
