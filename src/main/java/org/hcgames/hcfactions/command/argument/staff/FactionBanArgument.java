package org.hcgames.hcfactions.command.argument.staff;


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

public final class FactionBanArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionBanArgument() {
        super("ban");
        setDescription("Bans every member in this faction.");
        plugin = HCFactions.getInstance();
    //    this.permission = "hcf.command.faction.argument." + getName();
    }

    
    @Override
	public String getUsage() {
        return Lang.of("commands.staff.ban.usage", label, getName());
    }

    @Override
    public void onCommand() {
        if (args.length < 3) {
            sender.sendMessage(Lang.of("commands.error.usage", getUsage()));
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {

            @Override
            public void onSuccess(PlayerFaction faction){
                String extraArgs = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 2, args.length));
                ConsoleCommandSender console = plugin.getServer().getConsoleSender();

                for (UUID uuid : faction.getMembers().keySet()) {
                    String commandLine = "ban " + uuid.toString() + " " + extraArgs;
                    sender.sendMessage(Lang.of("commands.staff.ban.executing", commandLine));
                    console.getServer().dispatchCommand(sender, commandLine);
                }

                sender.sendMessage(Lang.of("commands.staff.ban.executed", faction.getName()));
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
