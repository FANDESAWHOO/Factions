package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FactionShowCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionShowCommand() {
        super("show|i|info|who");
        setDescription("Get details about a faction.");
        plugin = HCFactions.getInstance();

    }

 
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " [playerName|factionName]";
    }

    @Override
    public void onCommand() {
        Faction playerFaction = null;
        Faction namedFaction = null;

        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
                return;
            }

            try {
                namedFaction = plugin.getFactionManager().getPlayerFaction((Player) sender);
            } catch (NoFactionFoundException e) {
                tell(Lang.of("Commands-Factions-Global-NotInFaction"));
                return;
            }

            if (namedFaction == null) {
                tell(Lang.of("Commands-Factions-Global-NotInFaction"));
                return;
            }

            namedFaction.sendInformation(sender);
        } else {
            try{
                namedFaction = plugin.getFactionManager().getFaction(args[1]);
                namedFaction.sendInformation(sender);
            } catch (NoFactionFoundException ignored){}

            Faction finalNamedFaction = namedFaction;
            plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
                @Override
                public void onSuccess(PlayerFaction faction) {
                    if(finalNamedFaction != null && finalNamedFaction.equals(faction)) return;
                    faction.sendInformation(sender);
                }

                @Override
                public void onFail(FailReason reason) {
                    if(finalNamedFaction == null)
						tell(Lang.of("commands.error.faction_not_found", args[1]));
                }
            }, true);

        }

        return;
    }

    @Override
    public List<String> tabComplete() {
        if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

        if (args[1].isEmpty()) return null;

        Player player = (Player) sender;
        List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers())
			if (player.canSee(target) && !results.contains(target.getName())) results.add(target.getName());

        return results;
    }
}
