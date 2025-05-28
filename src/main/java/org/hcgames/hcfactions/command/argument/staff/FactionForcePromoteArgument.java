package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;

public class FactionForcePromoteArgument extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionForcePromoteArgument(HCFactions plugin) {
        super("forcepromote");
        setDescription("Forces the promotion status of a player.");
        this.plugin = plugin;
       // this.permission = "hcf.command.faction.argument." + getName();
    }

  
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(getLabel()));
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
            @Override
            public void onSuccess(PlayerFaction faction) {
                FactionMember member = null;

                for(FactionMember search : faction.getMembers().values()){
                    if(search.getCachedName().equalsIgnoreCase(args[1])){
                        member = search;
                        break;
                    }
                }

                if (member == null) {
                    sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
                    return;
                }

                if (member.getRole() != Role.MEMBER) {
                    sender.sendMessage(ChatColor.RED + member.getCachedName() + " is already a " + member.getRole().getName() + '.');
                    return;
                }

                member.setRole(Role.CAPTAIN);
                faction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " has been forcefully assigned as a captain.");
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
        return args.length == 2 ? null : Collections.emptyList();
    }
}
