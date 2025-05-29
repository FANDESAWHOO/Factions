package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.JavaUtils;
import org.hcgames.hcfactions.util.MapSorting;
import org.mineacademy.fo.settings.Lang;

import java.util.*;

public class FactionListCommand extends FactionSubCommand {


	private static final int MAX_FACTIONS_PER_PAGE = 10;

	private final HCFactions plugin;

	public FactionListCommand() {
		super("list | l");
		setDescription("See a list of all factions.");
		plugin = HCFactions.getInstance();
	}


	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		Integer page;
		if (args.length < 2) page = 1;
		else {
			page = JavaUtils.tryParseInt(args[1]);
			if (page == null) {
				sender.sendMessage(Lang.of("Commands-Invalid-Number")
						.replace("{number}", args[1]));
				//sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
				return;
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				showList(page, getLabel(), sender);
			}
		}.runTaskAsynchronously(plugin);
	}

	private void showList(int pageNumber, String label, CommandSender sender) {
		if (pageNumber < 1) {
			sender.sendMessage(Lang.of("Commands-Factions-List-PageLessThanOne"));
			return;
		}

		// Store a map of factions to their online player count.
		Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<>();
		Player senderPlayer = sender instanceof Player ? (Player) sender : null;
		for (Player target : Bukkit.getOnlinePlayers())
			if (senderPlayer == null || senderPlayer.canSee(target)) try {
				PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(target);
				factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
			} catch (NoFactionFoundException e) {
			}

		Map<Integer, List<String>> pages = new HashMap<>();
		List<Map.Entry<PlayerFaction, Integer>> sortedMap = MapSorting.sortedValues(factionOnlineMap, Comparator.reverseOrder());

		for (Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
			int currentPage = pages.size();

			List<String> results = pages.get(currentPage);
			if (results == null || results.size() >= MAX_FACTIONS_PER_PAGE)
				pages.put(++currentPage, results = new ArrayList<>(MAX_FACTIONS_PER_PAGE));

			PlayerFaction playerFaction = entry.getKey();
			String displayName = playerFaction.getFormattedName(sender);

			int index = results.size() + (currentPage > 1 ? (currentPage - 1) * MAX_FACTIONS_PER_PAGE : 0) + 1;
			String message = Lang.of("Commands-Factions-List-Item")
					.replace("{IndexID}", String.valueOf(index))
					.replace("{factionName}", displayName)
					.replace("{factionMembersOnline}", String.valueOf(entry.getValue()))
					.replace("{factionMembersAll}", String.valueOf(playerFaction.getMembers().size()))
					.replace("{deathsUntilRaidable}", JavaUtils.format(playerFaction.getDeathsUntilRaidable()))
					.replace("{MaximumDeathsUntilRaidable}", JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable()));
            /*ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.WHITE);
            builder.append(displayName).color(net.md_5.bungee.api.ChatColor.RED).
                    event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction.getName())).
                    event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.YELLOW + "Click to view " + displayName + ChatColor.YELLOW + '.')
                            .create()));

            // Show online member counts here.
            builder.append(" [" + entry.getValue() + '/' + playerFaction.getMembers().size() + ']', ComponentBuilder.FormatRetention.FORMATTING).
                    color(net.md_5.bungee.api.ChatColor.GRAY);

            // Show DTR rating here.
            builder.append(" [").color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
            builder.append(JavaUtils.format(playerFaction.getDeathsUntilRaidable())).color(fromBukkit(playerFaction.getDtrColour()));
            builder.append('/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable()) + " DTR]").color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);*/
			results.add(message);

		}

		int maxPages = pages.size();

		if (pageNumber > maxPages) {
			sender.sendMessage(Lang.of("Commands-Factions-List-NoMorePages")
					.replace("{totalPageCount}", String.valueOf(maxPages)));
			return;
		}

		sender.sendMessage(Lang.of("Commands-Factions-List-Header")
				.replace("{currentPageNumber}", String.valueOf(pageNumber))
				.replace("{totalPageCount}", String.valueOf(maxPages)));

		Collection<String> components = pages.get(pageNumber);
		for (String component : components) {
			if (component == null) continue;
			sender.sendMessage(component);
		}

		sender.sendMessage(Lang.of("Commands-Factions-List-Footer")
				.replace("{currentPageNumber}", String.valueOf(pageNumber))
				.replace("{totalPageCount}", String.valueOf(maxPages))
				.replace("{commandLabel}", label));
	}
}
