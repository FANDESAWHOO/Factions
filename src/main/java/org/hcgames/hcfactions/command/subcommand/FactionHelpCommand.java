package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;


public final class FactionHelpCommand extends FactionCommand {

	private static final int HELP_PER_PAGE = 10;

	private ImmutableMultimap<Integer, String> pages;


	 @Command(name = "faction.help", description = "View help on how to use factions.", aliases = { "f.help"}, usage = "/f help",  playerOnly = true, adminsOnly = false)
		public void onCommand(CommandArgs arg) {
		 CommandSender sender = arg.getSender();
		if (arg.length() < 1) {
			showPage(sender, arg.getLabel(), 1);
			return;
		}

		Integer page = JavaUtils.tryParseInt(arg.getArgs(0));

		if (page == null) {
			sender.sendMessage(Lang.of("Commands-Invalid-Number")
					.replace("{number}", arg.getArgs(0)));
			return;
		}

		showPage(sender, arg.getLabel(), page);
	}

	private void showPage(CommandSender sender, String label, int pageNumber) {
		// Create the multimap.
		if (pages == null) {
			int val = 1;
			int count = 0;
			Multimap<Integer, String> pages = ArrayListMultimap.create();
			for (FactionCommand argument : FactionCommand.getInstance().getCommands()) {
				if (argument == this) continue;

				// Check the permission and if the player can access.
			//	String permission = argument;
			//	if (permission != null && !sender.hasPermission(permission)) continue;


				count++;
				pages.get(val).add(Lang.of("Commands-Factions-Help-MenuEntry")
						.replace("{commandLabel}", label));
					//	.replace("{commandArgument}", argument.getName())
					//	.replace("{commandDescription}", argument.getDescription()));
				if (count % HELP_PER_PAGE == 0) val++;
			}

			// Finally assign it.
			this.pages = ImmutableMultimap.copyOf(pages);
		}

		int totalPageCount = (pages.size() / HELP_PER_PAGE) + 1;

		if (pageNumber < 1) {
		//	tell(Lang.of("Commands-Factions-Help-PageLessThanOne"));
			return;
		}

		if (pageNumber > totalPageCount) {
		//	tell(Lang.of("Commands-Factions-Help-NoMorePages")
					//.replace("{totalPageCount}", String.valueOf(totalPageCount)));
			return;
		}

	//	tell(Lang.of("Commands-Factions-Help-Header")
				//.replace("{currentPageNumber}", String.valueOf(pageNumber))
			//	.replace("{totalPageCount}", String.valueOf(totalPageCount)));

		//for (String message : pages.get(pageNumber)) tell("  " + message);

	//	tell(Lang.of("Commands-Factions-Help-Footer")
				//.replace("{currentPageNumber}", String.valueOf(pageNumber))
				//.replace("{totalPageCount}", String.valueOf(totalPageCount))
				//.replace("{commandLabel}", label));
	}
}
