package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class FactionForceLeaderCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionForceLeaderCommand() {
        super("forceleader");
        setDescription("Forces the leader of a faction.");
        plugin = HCFactions.getInstance();
     //   this.permission = "hcf.command.faction.argument." + getName();
    }

    
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            tell(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {

            @Override
            public void onSuccess(PlayerFaction faction) {
                FactionMember member = null;

                for(FactionMember search : faction.getMembers().values())
					if (search.getCachedName().equalsIgnoreCase(args[1])) {
						member = search;
						break;
					}

                if (member == null) {
                    tell(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
                    return;
                }

                if (member.getRole() == Role.LEADER) {
                    tell(ChatColor.RED + member.getCachedName() + " is already the leader of " + faction.getFormattedName(sender) + ChatColor.RED + '.');
                    return;
                }

                Optional<FactionMember> leader = faction.getLeader();
                String oldLeaderName = leader.isPresent() ? "none" : leader.get().getCachedName();
                String newLeaderName = member.getCachedName();

                // Demote the previous leader, promoting the new.
                if (leader.isPresent()) leader.get().setRole(Role.CAPTAIN);

                member.setRole(Role.LEADER);
                faction.broadcast(ChatColor.YELLOW + sender.getName() + " has forcefully set the leader to " + newLeaderName + '.');

                tell(ChatColor.GOLD.toString() + ChatColor.BOLD + "Leader of " + faction.getName() + " was forcefully set from " + oldLeaderName + " to " + newLeaderName + '.');
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
        return args.length == 2 ? null : Collections.emptyList();
    }
}
