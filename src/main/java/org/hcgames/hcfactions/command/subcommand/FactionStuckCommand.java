package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.api.TimerAPI;
import org.hcgames.hcfactions.command.FactionCommand;


import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

/**
 * Faction argument used to teleport to a nearby {@link org.bukkit.Location} safely if stuck.
 */
public final class FactionStuckCommand extends FactionCommand {


	@Command(name = "faction.stuck", description = "Teleport to a safe position.", aliases = {"f.stuck","faction.trap","f.trap","faction.trapped","f.trapped"}, usage = "/f stuck",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
        /*
        if(HCF.getPlugin().getSOTWManager().isPaused()){
            tell(HCF.getPlugin().getMessages().getString("Commands.Factions.Subcommand.Stuck.SOTW-Paused-Disabled"));
            return true;
        }*/

		if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
			player.sendMessage(Lang.of("Commands-Factions-Stuck-OverworldOnly"));
			return;
		}
		TimerAPI.callStuck(player, FactionsAPI.getPlayerFaction(player), arg.getLabel());
		return;
	}
}
