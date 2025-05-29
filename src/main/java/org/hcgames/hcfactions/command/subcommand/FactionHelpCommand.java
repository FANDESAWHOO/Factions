package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;


public class FactionHelpCommand extends FactionSubCommand {

	private static final int HELP_PER_PAGE = 10;

	private ImmutableMultimap<Integer, String> pages;

	public FactionHelpCommand() {
		super("help");
		setDescription("View help on how to use factions.");
	}
    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
			showPage(sender, getLabel(), 1);
			return;
		}

		Integer page = JavaUtils.tryParseInt(args[1]);

		if (page == null) {
			sender.sendMessage(Lang.of("Commands-Invalid-Number")
					.replace("{number}", args[1]));
			return;
		}

		showPage(sender, getLabel(), page);
	}

	private void showPage(CommandSender sender, String label, int pageNumber) {
		// Create the multimap.
		if (pages == null) {
			boolean isPlayer = sender instanceof Player;
			int val = 1;
			int count = 0;
			Multimap<Integer, String> pages = ArrayListMultimap.create();
			for (FactionSubCommand argument : FactionCommand.getInstance().getSubCommands()) {
				if (argument == this) continue;

				// Check the permission and if the player can access.
				String permission = argument.getPermission();
				if (permission != null && !sender.hasPermission(permission)) continue;


				count++;
				pages.get(val).add(Lang.of("Commands-Factions-Help-MenuEntry")
						.replace("{commandLabel}", label)
						.replace("{commandArgument}", argument.getName())
						.replace("{commandDescription}", argument.getDescription()));
				if (count % HELP_PER_PAGE == 0) val++;
			}

			// Finally assign it.
			this.pages = ImmutableMultimap.copyOf(pages);
		}

		int totalPageCount = (pages.size() / HELP_PER_PAGE) + 1;

		if (pageNumber < 1) {
			sender.sendMessage(Lang.of("Commands-Factions-Help-PageLessThanOne"));
			return;
		}

		if (pageNumber > totalPageCount) {
			sender.sendMessage(Lang.of("Commands-Factions-Help-NoMorePages")
					.replace("{totalPageCount}", String.valueOf(totalPageCount)));
			return;
		}

		sender.sendMessage(Lang.of("Commands-Factions-Help-Header")
				.replace("{currentPageNumber}", String.valueOf(pageNumber))
				.replace("{totalPageCount}", String.valueOf(totalPageCount)));

		for (String message : pages.get(pageNumber)) sender.sendMessage("  " + message);

		sender.sendMessage(Lang.of("Commands-Factions-Help-Footer")
				.replace("{currentPageNumber}", String.valueOf(pageNumber))
				.replace("{totalPageCount}", String.valueOf(totalPageCount))
				.replace("{commandLabel}", label));
	}
}
