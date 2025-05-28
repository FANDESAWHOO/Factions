package org.hcgames.hcfactions.command.subcommand;


import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.EconomyAPI;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionWithdrawCommand extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionWithdrawCommand(HCFactions plugin) {
        super("withdraw | w");
        setDescription("Withdraws money from the faction balance.");
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <all|amount>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can update the faction balance.");
            return;
        }

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
        FactionMember factionMember = playerFaction.getMember(uuid);

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(Lang.of("Commands-Factions-Withdraw-OfficerRequired"));
            //sender.sendMessage(ChatColor.RED + "You must be a faction officer to withdraw money.");
            return;
        }

        int factionBalance = playerFaction.getBalance();
        Integer amount;

        if (args[1].equalsIgnoreCase("all")) {
            amount = factionBalance;
        } else {
            if ((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
                sender.sendMessage(Lang.of("Commands-Factions-Deposit-InvalidNumber")
                        .replace("{amount}", args[1]));
                return;
            }
        }

        if (amount <= 0) {
            sender.sendMessage(Lang.of("Commands-Factions-Withdraw-MustBePositive"));
            //sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return;
        }

        if (amount > factionBalance) {
            sender.sendMessage(Lang.of("Commands-Factions-Withdraw-MustBePositive")
                    .replace("{requiredAmount}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(amount))
                    .replace("{currentBalance}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(factionBalance)));
            //sender.sendMessage(ChatColor.RED + "Your faction need at least " + EconomyManager.ECONOMY_SYMBOL +
            //        JavaUtils.format(amount) + " to do this, whilst it only has " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(factionBalance) + '.');

            return;
        }

        EconomyAPI.addBalance(uuid, amount);
        playerFaction.setBalance(factionBalance - amount);
        playerFaction.broadcast(Lang.of("Commands-Factions-Withdraw-Broadcast")
                .replace("{player}", factionMember.getRole().getAstrix() + sender.getName())
                .replace("{amount}", EconomyAPI.ECONOMY_SYMBOL + JavaUtils.format(amount)));
        //playerFaction.broadcast(plugin.getConfiguration().getRelationColourTeammate() + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has withdrew " +
        //        ChatColor.BOLD + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + ChatColor.YELLOW + " from the faction balance.");

        return;
    }

    @Override
    public List<String> tabComplete() {
        return args.length == 2 ? COMPLETIONS : Collections.emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
}
