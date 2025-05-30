package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to forcefully join {@link Faction}s.
 */
public final class FactionForceJoinArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionForceJoinArgument() {
        super("forcejoin");
        setDescription("Forcefully join a faction.");
        plugin = HCFactions.getInstance();
       // this.permission = "hcf.command.faction.argument." + getName();
    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            tell(ChatColor.RED + "Only players can join factions.");
            return;
        }

        if (args.length < 2) {
            tell(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        Player player = (Player) sender;

        try{
            plugin.getFactionManager().getPlayerFaction(player);
            tell(ChatColor.RED + "You are already in a faction.");
        }catch (NoFactionFoundException e){
            plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
                @Override
                public void onSuccess(PlayerFaction faction) {
                    if (faction.addMember(player, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), true))
						faction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " has forcefully joined the faction.");
                }

                @Override
                public void onFail(FailReason reason) {
                    tell(Lang.of("commands.error.faction_not_found", args[1]));
                }
            });
        }
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
