package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to set the DTR Regeneration cooldown of {@link Faction}s.
 */
public final class FactionClearClaimsCommand extends FactionSubCommand {

    private final ConversationFactory factory;
    private final HCFactions plugin;

    public FactionClearClaimsCommand() {
        super("clearclaims");
        setDescription("Clears the claims of a faction.");
        plugin = HCFactions.getInstance();
      //  this.permission = "hcf.command.faction.argument." + getName();

        factory = new ConversationFactory(plugin).
                withFirstPrompt(new ClaimClearAllPrompt(plugin)).
                withEscapeSequence("/no").
                withTimeout(10).
                withModality(false).
                withLocalEcho(true);
    }

    
    @Override
	public String getUsage() {
        return Lang.of("Commands.staff.clearclaims.usage", label, getName());
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            tell(Lang.of("command.error.usage", getUsage()));
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                tell(Lang.of("Commands.error.console_only"));
                return;
            }

            Conversable conversable = (Conversable) sender;
            conversable.beginConversation(factory.buildConversation(conversable));
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {
            @Override
            public void onSuccess(ClaimableFaction claimableFaction) {
                claimableFaction.removeClaims(claimableFaction.getClaims(), sender);
                if (claimableFaction instanceof PlayerFaction)
					((PlayerFaction) claimableFaction).broadcast(Lang.of("Commands.staff.clearclaims.cleared_faction_broadcast", sender.getName()));
                tell(Lang.of("Commands.staff.clearclaims.cleared", claimableFaction.getName()));
            }

            @Override
            public void onFail(FailReason reason) {
                tell(Lang.of("Commands.error.faction_not_found", args[1]));
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
