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
import org.hcgames.hcfactions.util.JavaUtils;
import java.util.Collections;
import java.util.List;

public final class FactionDepositCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionDepositCommand() {
		plugin = HCFactions.getInstance();
	}
	
	@Command(name = "faction.deposit", description = "Deposits money to the faction balance.", aliases = { "f.d","faction.dr"}, usage = "/f deposit [amount]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 2) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f deposit [amount]"));
			return;
		}

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		// UNUSED NOW UUID uuid = player.getUniqueId();
		Integer playerBalance = EconomyAPI.getBalance(player).intValue();

		Integer amount;
		if (arg.getArgs(0).equalsIgnoreCase("all")) amount = playerBalance;
		else if ((amount = (JavaUtils.tryParseInt(arg.getArgs(0)))) == null) {
			player.sendMessage(Lang.of("Commands-Factions-Deposit-InvalidNumber")
					.replace("{amount}", arg.getArgs(0)));
			return;
		}

		if (amount <= 0) {
			player.sendMessage(Lang.of("Commands-Factions-Deposit-AmountNotPositive"));
			return;
		}

		if (playerBalance < amount) {
			player.sendMessage(Lang.of("Commands-Factions-Deposit-NotEnoughFunds")
					.replace("{requiredAmount}", "$" + JavaUtils.format(amount))
					.replace("{currentAmount}", "$" + JavaUtils.format(playerBalance)));

			return;
		}

		EconomyAPI.subtractBalance(player, amount);

		playerFaction.setBalance(playerFaction.getBalance() + amount);
		playerFaction.broadcast(Lang.of("Commands-Factions-Deposit-BroadcastDeposit")
				.replace("{player}", playerFaction.getMember(player).getRole().getAstrix() + player.getName())
				.replace("{amount}", "$" + JavaUtils.format(amount)));

		return;
	}

}
