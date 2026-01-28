package org.hcgames.hcfactions.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.faction.Faction;


import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class LocationCommand {

	private final HCFactions plugin;

	public LocationCommand() {
		plugin = HCFactions.getInstance();
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
	}

    @Command(name = "location", description = "The main command for Location",aliases = {"loc","whereami"}, usage = "/location",  playerOnly = true, adminsOnly = false)
    public void onCommand(CommandArgs arg) {
    	String[] args = arg.getArgs();
		Player target;
		if (args.length >= 1) target = Bukkit.getPlayer(args[0]);
		else if (arg.getSender() instanceof Player) target = arg.getPlayer();
		else {
			arg.getSender().sendMessage(Lang.of("Commands.Location.Usage")
					.replace("{commandLabel}", arg.getLabel()));
			return;
		}

		if (target == null || (arg.getSender() instanceof Player && !arg.getPlayer().canSee(target))) {
			arg.getSender().sendMessage(Lang.of("Commands.Location.Output")
					.replace("{player}", args[0]));
			return;
		}

		Location location = target.getLocation();
		Faction factionAt = plugin.getFactionManager().getFactionAt(location);

		arg.getSender().sendMessage(Lang.of("Commands.Location.Output")
				.replace("{player}", target.getName())
				.replace("{factionName}", factionAt.getFormattedName(arg.getSender()))
				.replace("{isDeathBanLocation}", factionAt.isSafezone() ?
						Lang.of("Commands.Location.NonDeathban") :
						Lang.of("Commands.Location.Deathban")));
	}

}
