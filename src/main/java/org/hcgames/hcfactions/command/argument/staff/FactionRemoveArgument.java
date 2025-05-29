package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to forcefully remove {@link Faction}s.
 */
public final class FactionRemoveArgument extends FactionSubCommand {

    private final ConversationFactory factory;
    private final HCFactions plugin;

    public FactionRemoveArgument(HCFactions plugin) {
        super("remove | delete | forcedisband | forceremove");
        setDescription( "Remove a faction.");
        this.plugin = plugin;
       // this.aliases = new String[]{"delete", "forcedisband", "forceremove"};
       // this.permission = "hcf.command.faction.argument." + getName();
        factory = new ConversationFactory(plugin).
                withFirstPrompt(new RemoveAllPrompt(plugin)).
                withEscapeSequence("/no").
                withTimeout(10).
                withModality(false).
                withLocalEcho(true);
    }

    
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <all|factionName>";
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
                return;
            }

            Conversable conversable = (Conversable) sender;
            conversable.beginConversation(factory.buildConversation(conversable));
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], Faction.class, new SearchCallback<Faction>() {
            @Override
            public void onSuccess(Faction faction) {
                if (plugin.getFactionManager().removeFaction(faction, sender))
					Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Disbanded faction " + faction.getName() + ChatColor.YELLOW + '.');
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
