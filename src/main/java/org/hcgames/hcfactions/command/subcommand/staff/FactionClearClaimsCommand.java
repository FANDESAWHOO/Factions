package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


/**
 * Faction argument used to set the DTR Regeneration cooldown of {@link Faction}s.
 */
public final class FactionClearClaimsCommand extends FactionCommand {

	private final ConversationFactory factory;
	private final HCFactions plugin;

	public FactionClearClaimsCommand() {
		plugin = HCFactions.getInstance();
		//  this.permission = "hcf.command.faction.argument." + getName();

		factory = new ConversationFactory(plugin).
				withFirstPrompt(new ClaimClearAllPrompt(plugin)).
				withEscapeSequence("/no").
				withTimeout(10).
				withModality(false).
				withLocalEcho(true);
	}

	 @Command(name = "faction.clearclaims", description = "Clears the claims of a faction." ,permission = "factions.command.clearclaims", aliases = { "f.clearclaims"}, usage = "/<command>  clearclaims",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 CommandSender sender = arg.getSender();
		if (arg.length() < 1) {
			sender.sendMessage(Lang.of("command.error.usage", "f.clearclaims"));
			return;
		}
		if (args[1].equalsIgnoreCase("all")) {
			if (!(sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Lang.of("Commands.error.console_only"));
				return;
			}

			Conversable conversable = (Conversable) arg.getSender();
			conversable.beginConversation(factory.buildConversation(conversable));
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {
			@Override
			public void onSuccess(ClaimableFaction claimableFaction) {
				claimableFaction.removeClaims(claimableFaction.getClaims(), sender);
				if (claimableFaction instanceof PlayerFaction)
					((PlayerFaction) claimableFaction).broadcast(Lang.of("Commands.staff.clearclaims.cleared_faction_broadcast", sender.getName()));
				sender.sendMessage(Lang.of("Commands.staff.clearclaims.cleared", claimableFaction.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				sender.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

	private static class ClaimClearAllPrompt extends StringPrompt {

		private final HCFactions plugin;

		public ClaimClearAllPrompt(HCFactions plugin) {
			this.plugin = plugin;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return Lang.of("Commands.staff.clearclaims.console_prompt.prompt");
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String string) {
			switch (string.toLowerCase()) {
				case "yes": {
					for (Faction faction : plugin.getFactionManager().getFactions())
						if (faction instanceof ClaimableFaction) {
							ClaimableFaction claimableFaction = (ClaimableFaction) faction;
							claimableFaction.removeClaims(claimableFaction.getClaims(), plugin.getServer().getConsoleSender());
						}

					Conversable conversable = context.getForWhom();
					plugin.getServer().broadcastMessage(Lang.of("Commands.staff.clearclaims.console_prompt.cleared",
							(conversable instanceof CommandSender ? " by " + ((CommandSender) conversable).getName() : "")));

					return Prompt.END_OF_CONVERSATION;
				}
				case "no": {
					context.getForWhom().sendRawMessage(Lang.of("Commands.staff.clearclaims.console_prompt.cancelled"));
					return Prompt.END_OF_CONVERSATION;
				}
				default: {
					context.getForWhom().sendRawMessage(Lang.of("Commands.staff.clearclaims.console_prompt.cancelled_unknown"));
					return Prompt.END_OF_CONVERSATION;
				}
			}
		}
	}
}
