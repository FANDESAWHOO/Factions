package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.event.playerfaction.FactionRelationRemoveEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Collection;
import java.util.Collections;

public final class FactionUnallyArgument extends FactionCommand {

	private final Relation relation = Relation.ALLY;
	private final HCFactions plugin;

	public FactionUnallyArgument() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.unally", description = "Remove an ally pact with other factions.", aliases = {"f.unally","faction.unalliance","f.neutral","faction.neutral","f.unalliance"}, usage = "/f unally <all|factionName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (Configuration.factionMaxAllies <= 0) {
			player.sendMessage(Lang.of("Commands-Factions-Unally-DisabledOnMap"));
			//player.sendMessage(ChatColor.RED + "Allies are disabled this map.");
			return;
		}

		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f unally <all|factionName>"));
			return;
		}

		PlayerFaction playerFaction;

		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Unally-OfficerRequired"));
			//player.sendMessage(ChatColor.RED + "You must be a faction officer to edit relations.");
			return;
		}

		if (arg.getArgs(0).equalsIgnoreCase("all")) {
			Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
			if (allies.isEmpty()) {
				player.sendMessage(Lang.of("Commands-Factions-Unally-NoAllies"));
				//player.sendMessage(ChatColor.RED + "Your faction has no allies.");
				return;
			}

			handle(player, playerFaction, allies);
		} else
			plugin.getFactionManager().advancedSearch(arg.getArgs(0), PlayerFaction.class, new SearchCallback<PlayerFaction>() {
				@Override
				public void onSuccess(PlayerFaction faction) {
					handle(player, playerFaction, Collections.singleton(faction));
				}

				@Override
				public void onFail(FailReason reason) {
					player.sendMessage(Lang.of("Commands-Factions-Global-UnknownFaction").replace("{factionName}", arg.getArgs(0)));
				}
			});

		return;
	}

	private void handle(Player player, PlayerFaction faction, Collection<PlayerFaction> targets) {
		for (PlayerFaction targetFaction : targets) {
			if (faction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(faction.getUniqueID()) == null) {
				player.sendMessage(Lang.of("Commands-Factions-Unally-NotAllied")
						.replace("{allyDisplayName}", relation.getDisplayName())
						.replace("{otherFactionName}", targetFaction.getFormattedName(faction)));
				//player.sendMessage(ChatColor.RED + "Your faction is not " + relation.getDisplayName() + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
				return;
			}

			FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(faction, targetFaction, Relation.ALLY);
			Bukkit.getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				player.sendMessage(Lang.of("Commands-Factions-Unally-CouldNotDrop")
						.replace("{allyDisplayName}", relation.getDisplayName())
						.replace("{otherFactionName}", targetFaction.getFormattedName(faction)));
				//player.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
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
