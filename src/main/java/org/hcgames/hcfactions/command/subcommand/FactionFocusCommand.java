package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.focus.FocusTarget;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

public class FactionFocusCommand extends SimpleSubCommand {
	private final HCFactions plugin;

	public FactionFocusCommand(){
		super("focus | unfocus");
		setDescription("Focus on a player or argument");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}

	public String getUsage(String s) {
		return Lang.of("Commands.Factions.Focus.Usage");
	}

	@Override
	public void onCommand(){
		if(!(sender instanceof Player)){
			sender.sendMessage(Lang.of("Error-Messages.PlayerOnly"));
			//player only
			return;
		}

		Player player = (Player) sender;

		if(args.length < 2){
			sender.sendMessage(getUsage(getLabel()));
			return;
		}

		PlayerFaction faction;
		try{
			faction = plugin.getFactionManager().getPlayerFaction(player);
		}catch(NoFactionFoundException e){
			sender.sendMessage(Lang.of("Error-Messages.NotInFaction"));
			//not in faction
			return;
		}

		String name = args[1];

		Player targetPlayer = plugin.getServer().getPlayer(name);
		Faction targetFaction;

		if(targetPlayer == null){
			try{
				targetFaction = plugin.getFactionManager().getFaction(name);
			}catch(NoFactionFoundException e){
				sender.sendMessage(Lang.of("Commands.Factions.Focus.Error.NotFound").replace("{name}", name));
				//player or faction not found
				return;
			}

			if(!(targetFaction instanceof PlayerFaction)){
				sender.sendMessage(Lang.of("Commands.Factions.Focus.Error.NotPlayerFaction"));
				//faction isn't player faction
				return;
			}

			handleFactionFocus(sender, faction, (PlayerFaction) targetFaction);
			return;
		}

		handleFactionFocus(sender, faction, targetPlayer);
		return;
	}

	private void handleFactionFocus(CommandSender sender, PlayerFaction current, PlayerFaction target){
		if(current.isFocused(target.getUniqueID())){
			plugin.getFactionManager().getFocusHandler().unfocus(current.fmk(target.getUniqueID()));
			current.broadcast(Lang.of("Commands.Factions.Focus.UnFocus.Faction")
					.replace("{player}", sender.getName()).replace("{focusedFaction}", target.getName()));
			return;
		}

		plugin.getFactionManager().getFocusHandler().focus(current, new FocusTarget(plugin, current, target));
		current.broadcast(Lang.of("Commands.Factions.Focus.Focus.Faction")
				.replace("{player}", sender.getName()).replace("{focusedFaction}", target.getName()));
		//focused
	}

	private void handleFactionFocus(CommandSender sender, PlayerFaction current, Player target){
		if(current.isFocused(target.getUniqueId())){
			plugin.getFactionManager().getFocusHandler().unfocus(current.fmk(target.getUniqueId()));
			current.broadcast(Lang.of("Commands.Factions.Focus.UnFocus.Player")
					.replace("{player}", sender.getName()).replace("{focusedPlayer}", target.getName()));
			//already focused
			return;
		}

		plugin.getFactionManager().getFocusHandler().focus(current, new FocusTarget(plugin, current, target));
		current.broadcast(Lang.of("Commands.Factions.Focus.Focus.Player")
				.replace("{player}", sender.getName()).replace("{focusedPlayer}", target.getName()));
	}

}
