package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to forcefully remove {@link Faction}s.
 */
public final class FactionRemoveCommand extends FactionCommand {

	private final ConversationFactory factory;
	private final HCFactions plugin;

	public FactionRemoveCommand() {
		plugin = HCFactions.getInstance();
		// this.aliases = new String[]{"delete", "forcedisband", "forceremove"};
		// this.permission = "hcf.command.faction.argument." + getName();
		factory = new ConversationFactory(plugin).
				withFirstPrompt(new RemoveAllPrompt(plugin)).
				withEscapeSequence("/no").
				withTimeout(10).
				withModality(false).
				withLocalEcho(true);
	}

	 @Command(name = "faction.remove", description = "Remove a faction.", permission = "factions.command.remove", aliases = { "f.remove","f.delete","f.forcedisband","faction.forcedisband","faction.delete"}, usage = "/<command> remove <name>",  playerOnly = false, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 CommandSender sender = arg.getSender();
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: " + "/f remove <name>");
			return;
		}

		if (args[0].equalsIgnoreCase("all")) {
			if (!(sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
				return;
			}

			Conversable conversable = (Conversable) sender;
			conversable.beginConversation(factory.buildConversation(conversable));
			return;
		}

		plugin.getFactionManager().advancedSearch(args[0], Faction.class, new SearchCallback<Faction>() {
			@Override
			public void onSuccess(Faction faction) {
				if (plugin.getFactionManager().removeFaction(faction, sender))
					org.bukkit.command.Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Disbanded faction " + faction.getName() + ChatColor.YELLOW + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				sender.sendMessage(Lang.of("Commands.error.faction_not_found", args[0]));
			}
		});

		return;
	}


	private static class RemoveAllPrompt extends StringPrompt {

		private final HCFactions plugin;

		public RemoveAllPrompt(HCFactions plugin) {
			this.plugin = plugin;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All factions" + ChatColor.YELLOW + " will be cleared. " +
					"Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String string) {
			switch (string.toLowerCase()) {
				case "yes": {
					for (Faction faction : plugin.getFactionManager().getFactions())
						plugin.getFactionManager().removeFaction(faction, Bukkit.getConsoleSender());

					Conversable conversable = context.getForWhom();
					Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD +
							"All factions have been disbanded" + (conversable instanceof CommandSender ? " by " + ((CommandSender) conversable).getName() : "") + '.');

					return Prompt.END_OF_CONVERSATION;
				}
				case "no": {
					context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of disbanding all factions.");
					return Prompt.END_OF_CONVERSATION;
				}
				default: {
					context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of disbanding all factions cancelled.");
					return Prompt.END_OF_CONVERSATION;
				}
			}
		}
	}
}
