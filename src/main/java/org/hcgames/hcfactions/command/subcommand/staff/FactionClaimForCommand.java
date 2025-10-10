package org.hcgames.hcfactions.command.subcommand.staff;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.cuboid.Cuboid;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Changed from WorldEdit to Custom Wand
 * Used to claim land for other {@link ClaimableFaction}s.
 */
public final class FactionClaimForCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionClaimForCommand() {
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
			tell(Lang.of("commands.error.player_only"));
			return;
		}
		checkPerm();
		if (args.length < 2) {
			tell(Lang.of("commands.error.usage", getUsage()));
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], ClaimableFaction.class, new SearchCallback<ClaimableFaction>() {

			@Override
			public void onSuccess(ClaimableFaction faction) {
				Player player = (Player) sender;
				Cuboid pos = HCFactions.getInstance().getWandManager().getSelection(player);

				if (pos == null) {
					tell(ChatColor.RED + "You need to select 2 positions with the claim wand. Use /claimwand and right/left click with the stick.");
					return;
				}
				Location selection = pos.getMinimumPoint();
				Location selection2 = pos.getMaximumPoint();
                if (selection == null || selection2 == null) {
                	tell(ChatColor.RED + "You need to select 2 positions with the claim wand. Use /claimwand and right/left click with the stick.");
					return;
                }
				if (faction.addClaim(new Claim(faction, selection, selection2), sender))
					tell(Lang.of("Commands.staff.claimfor.claimed", faction.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				tell(Lang.of("commands.error.faction_not_found", args[1]));
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
