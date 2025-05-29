package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.event.playerfaction.FactionRelationRemoveEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collection;
import java.util.Collections;

public class FactionUnallyArgument extends SimpleSubCommand {

    private final Relation relation = Relation.ALLY;
    private final HCFactions plugin;

    public FactionUnallyArgument(HCFactions plugin) {
        super("unally | unalliance | neutral");
        setDescription("Remove an ally pact with other factions.");
        this.plugin = plugin;
        if(!FactionCommands.getArguments().contains(this))
            FactionCommands.getArguments().add(this);
    }

    
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <all|factionName>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.of("Commands-ConsoleOnly"));
            return;
        }

        if (Configuration.factionMaxAllies <= 0) {
            sender.sendMessage(Lang.of("Commands-Factions-Unally-DisabledOnMap"));
            //sender.sendMessage(ChatColor.RED + "Allies are disabled this map.");
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

        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(Lang.of("Commands-Factions-Unally-OfficerRequired"));
            //sender.sendMessage(ChatColor.RED + "You must be a faction officer to edit relations.");
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                sender.sendMessage(Lang.of("Commands-Factions-Unally-NoAllies"));
                //sender.sendMessage(ChatColor.RED + "Your faction has no allies.");
                return;
            }

            handle(sender, playerFaction, allies);
        } else
			plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
				@Override
				public void onSuccess(PlayerFaction faction) {
					handle(sender, playerFaction, Collections.singleton(faction));
				}

				@Override
				public void onFail(FailReason reason) {
					sender.sendMessage(Lang.of("Commands-Factions-Global-UnknownFaction").replace("{factionName}", args[1]));
				}
			});

        return;
    }

    private void handle(CommandSender sender, PlayerFaction faction, Collection<PlayerFaction> targets){
        for (PlayerFaction targetFaction : targets) {
            if (faction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(faction.getUniqueID()) == null) {
                sender.sendMessage(Lang.of("Commands-Factions-Unally-NotAllied")
                        .replace("{allyDisplayName}", relation.getDisplayName())
                        .replace("{otherFactionName}", targetFaction.getFormattedName(faction)));
                //sender.sendMessage(ChatColor.RED + "Your faction is not " + relation.getDisplayName() + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
                return;
            }

            FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(faction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                sender.sendMessage(Lang.of("Commands-Factions-Unally-CouldNotDrop")
                        .replace("{allyDisplayName}", relation.getDisplayName())
                        .replace("{otherFactionName}", targetFaction.getFormattedName(faction)));
                //sender.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
                return;
            }

            // Inform the affected factions.
            faction.broadcast(Lang.of("Commands-Factions-Unally-PlayerFactionBroadcast")
                    .replace("{allyDisplayName}", relation.getDisplayName())
                    .replace("{otherFactionName}", targetFaction.getFormattedName(faction)));
            //playerFaction.broadcast(ChatColor.YELLOW + "Your faction has dropped its " + relation.getDisplayName() + ChatColor.YELLOW + " with " +
            //        targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');

            targetFaction.broadcast(Lang.of("Commands-Factions-Unally-OtherFactionBroadcast")
                    .replace("{allyDisplayName}", relation.getDisplayName())
                    .replace("{factionName}", faction.getFormattedName(targetFaction)));
            //targetFaction.broadcast(ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " has dropped their " + relation.getDisplayName() +
            //        ChatColor.YELLOW + " with your faction.");
        }
    }

    /*@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }

        return Lists.newArrayList(Iterables.concat(COMPLETIONS, playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");*/
}
