package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FactionMuteArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionMuteArgument() {
        super("mute");
        setDescription( "Mutes every member in this faction.");
        plugin = HCFactions.getInstance();
    //    this.permission = "hcf.command.faction.argument." + getName();
    }

    
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <factionName> <time:(e.g. 1h2s)> <reason>";
    }

    @Override
    public void onCommand() {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<org.hcgames.hcfactions.faction.PlayerFaction>() {

            @Override
            public void onSuccess(org.hcgames.hcfactions.faction.PlayerFaction faction){
                String extraArgs = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 2, args.length));
                ConsoleCommandSender console = Bukkit.getConsoleSender();
                for (UUID uuid : faction.getMembers().keySet()) {
                    String commandLine = "mute " + uuid.toString() + " " + extraArgs;
                    sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Executing " + ChatColor.RED + commandLine);
                    console.getServer().dispatchCommand(sender, commandLine);
                }

                sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Executed mute action on faction " + faction.getName() + ".");
            }

            @Override
            public void onFail(FailReason reason){
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
