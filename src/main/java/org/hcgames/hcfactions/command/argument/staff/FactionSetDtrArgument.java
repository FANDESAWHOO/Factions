package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to set the DTR of {@link Faction}s.
 */
public final class FactionSetDtrArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionSetDtrArgument(HCFactions plugin) {
        super("setdtr | dtr");
        setDescription("Sets the DTR of a faction.");
        this.plugin = plugin;
    //    this.permission = "hcf.command.faction.argument." + getName();
    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <playerName|factionName> <newDtr>";
    }

    @Override
    public void onCommand() {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        Double[] newDTR = {JavaUtils.tryParseDouble(args[2])};

        if (newDTR[0] == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return;
        }

        if(sender instanceof Player)
			if (newDTR[0] <= 0 && !sender.hasPermission("hcf.command.faction.argument." + getName() + ".raidable")) {
				sender.sendMessage("You don't have permission to make factions raidable.");
				return;
			}

        /*if (args[1].equalsIgnoreCase("all")) {
            for (Faction faction : plugin.getFactionManager().getFactions()) {
                if (faction instanceof PlayerFaction) {
                    ((PlayerFaction) faction).setDeathsUntilRaidable(newDTR[0]);
                }
            }

            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of all factions to " + newDTR[0] + '.');
            return;
        }*/

        plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
            @Override
            public void onSuccess(PlayerFaction faction) {
                double previousDtr = faction.getDeathsUntilRaidable();
                newDTR[0] = faction.setDeathsUntilRaidable(newDTR[0]);

                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of " + faction.getName() + " from " + previousDtr + " to " + newDTR[0] + '.');
            }

            @Override
            public void onFail(FailReason reason) {
                sender.sendMessage(Lang.of("commands.error.faction_not_found", args[1]));
            }
        });


        return;
    }

    @Override
    public List<String> tabComplete() {
        if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();
		else if (args[1].isEmpty()) return null;
		else {
            Player player = (Player) sender;
            List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
            for (Player target : Bukkit.getOnlinePlayers())
				if (player.canSee(target) && !results.contains(target.getName())) results.add(target.getName());

            return results;
        }
    }
}
