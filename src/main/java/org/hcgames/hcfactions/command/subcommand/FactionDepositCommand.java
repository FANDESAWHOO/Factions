package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.EconomyAPI;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionDepositCommand extends SimpleSubCommand {
	private final HCFactions plugin;

	public FactionDepositCommand() {
		super("deposit | d");
		setDescription("Deposits money to the faction balance.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <all|amount>";
	}

	@Override
	public void onCommand() {
		checkConsole();

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage(getLabel())));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		UUID uuid = player.getUniqueId();
		Integer playerBalance = EconomyAPI.getBalance(uuid).intValue();

		Integer amount;
		if (args[1].equalsIgnoreCase("all")) amount = playerBalance;
		else if ((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
			sender.sendMessage(Lang.of("Commands-Factions-Deposit-InvalidNumber")
					.replace("{amount}", args[1]));
			return;
		}

		if (amount <= 0) {
			sender.sendMessage(Lang.of("Commands-Factions-Deposit-AmountNotPositive"));
			return;
		}

		if (playerBalance < amount) {
			sender.sendMessage(Lang.of("Commands-Factions-Deposit-NotEnoughFunds")
					.replace("{requiredAmount}", "$" + JavaUtils.format(amount))
					.replace("{currentAmount}",  "$" + JavaUtils.format(playerBalance)));

			return;
		}

		EconomyAPI.subtractBalance(uuid, amount);

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
