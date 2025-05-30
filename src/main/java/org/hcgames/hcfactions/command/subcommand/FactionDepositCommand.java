package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.EconomyAPI;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;

public final class FactionDepositCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionDepositCommand() {
		super("deposit|d");
		setDescription("Deposits money to the faction balance.");
		plugin = HCFactions.getInstance();
	}

    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <all|amount>";
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
			tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		// UNUSED NOW UUID uuid = player.getUniqueId();
		Integer playerBalance = EconomyAPI.getBalance(player).intValue();

		Integer amount;
		if (args[1].equalsIgnoreCase("all")) amount = playerBalance;
		else if ((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
			tell(Lang.of("Commands-Factions-Deposit-InvalidNumber")
					.replace("{amount}", args[1]));
			return;
		}

		if (amount <= 0) {
			tell(Lang.of("Commands-Factions-Deposit-AmountNotPositive"));
			return;
		}

		if (playerBalance < amount) {
			tell(Lang.of("Commands-Factions-Deposit-NotEnoughFunds")
					.replace("{requiredAmount}", "$" + JavaUtils.format(amount))
					.replace("{currentAmount}",  "$" + JavaUtils.format(playerBalance)));

			return;
		}

		EconomyAPI.subtractBalance(player, amount);

		playerFaction.setBalance(playerFaction.getBalance() + amount);
		playerFaction.broadcast(Lang.of("Commands-Factions-Deposit-BroadcastDeposit")
				.replace("{player}", playerFaction.getMember(player).getRole().getAstrix() + sender.getName())
				.replace("{amount}", "$" + JavaUtils.format(amount)));

		return;
	}

	@Override
	public List<String> tabComplete() {
		return args.length == 2 ? COMPLETIONS : Collections.<String>emptyList();
	}

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
}
