package org.hcgames.hcfactions.command.argument.staff;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to claim land for other {@link ClaimableFaction}s.
 */
public final class FactionClaimForArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionClaimForArgument() {
        super("claimfor");
        setDescription("Claims land for another faction.");
        plugin = HCFactions.getInstance();
     //   this.permission = "hcf.command.faction.argument." + getName();
    }

  
    @Override
	public String getUsage() {
        return Lang.of("commands.staff.claimfor.usage", label, getName());
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.of("commands.error.player_only"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.of("commands.error.usage", getUsage()));
            return;
        }

        plugin.getFactionManager().advancedSearch(args[1], ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {

            @Override
            public void onSuccess(ClaimableFaction faction) {
                Player player = (Player) sender;
                WorldEditPlugin worldEditPlugin = plugin.getWorldEdit();

                if (worldEditPlugin == null) {
                    sender.sendMessage(Lang.of("commands.claimfor.worldedit_required"));
                    return;
                }

                Selection selection = worldEditPlugin.getSelection(player);

                if (selection == null) {
                    sender.sendMessage(Lang.of("commands.claimfor.worldedit_selection_required"));
                    return;
                }

                if (faction.addClaim(new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint()), sender))
					sender.sendMessage(Lang.of("commands.claimfor.claimed", faction.getName()));
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
        if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

        if (args[1].isEmpty()) return null;

        Player player = (Player) sender;
        List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : plugin.getServer().getOnlinePlayers())
			if (player.canSee(target) && !results.contains(target.getName())) results.add(target.getName());

        return results;
    }
}
