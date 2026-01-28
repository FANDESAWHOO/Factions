package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.PlayerUtil;
import org.hcgames.hcfactions.util.text.SimpleComponent;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;
import java.util.Set;

public final class FactionInviteCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionInviteCommand() {
		plugin = HCFactions.getInstance();

	}

	@Command(name = "faction.invite", description = "Invite a player to the faction.", aliases = { "f.invite","faction.inv", "f.inv"}, usage = "/f invite <playerName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f invite <playerName>"));
			return;
		}

		if (arg.getArgs(0).length() > 17) {
			player.sendMessage(Lang.of("Commands-Factions-Invite-InvalidUsername")
					.replace("{username}", arg.getArgs(0)));
			return;
		}

		Player invitee = PlayerUtil.getPlayerByNick(arg.getArgs(0), true);

		if (invitee == null) {
			player.sendMessage(Lang.of("Commands-Pay-UnknownPlayer").replace("{player}", arg.getArgs(0)));
			return;
		}

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Invite-OfficerRequired"));
			return;
		}

		Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
		String name = arg.getArgs(0);

		if (playerFaction.findMember(name) != null) {
			player.sendMessage(Lang.of("Commands-Factions-Invite-AlreadyInFaction")
					.replace("{player}", name));
			return;
		}

		if (!Configuration.kitMap) { // && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()
			player.sendMessage(Lang.of("Commands-Factions-Invite-NoInviteWhileRaidable"));
			return;
		}

		if (!invitedPlayerNames.add(name.toLowerCase())) {
			player.sendMessage(Lang.of("Commands-Factions-Invite-AlreadyInvited")
					.replace("{player}", name));
			return;
		}

		Player target = PlayerUtil.getPlayerByNick(name, true);;
		if (target != null) {
			name = target.getName(); // fix casing.
			SimpleComponent component = SimpleComponent.of(
			        Lang.of("Commands-Factions-Invite-InviteReceived")
			            .replace("{relationColour}", Relation.ENEMY.toChatColour() + "")
			            .replace("{sender}", player.getName())
			            .replace("{factionName}", playerFaction.getName())
			    )
			    .onClickRunCmd("/" + arg.getLabel() + " accept " + playerFaction.getName());
            component.send(target);
		
		}
	}
	
    /*@Override //fixme // WHY YOU REMOVE THIS, SYSTEM UPDATE, CAN YOU TELL WHY YOU DO EVERYTHING?
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER)) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                if (playerFaction != plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) {
                    results.add(target.getName());
                }
            }
        }

        return results;
    }*/
}
