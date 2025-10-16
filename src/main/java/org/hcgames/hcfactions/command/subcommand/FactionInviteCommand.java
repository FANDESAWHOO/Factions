package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.lib.PlayerUtil;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;
import org.mineacademy.fo.model.SimpleComponent;
import java.util.Set;

public final class FactionInviteCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionInviteCommand() {
		super("invite|inv|invitemember|inviteplayer");
		setDescription("Invite a player to the faction.");
		plugin = HCFactions.getInstance();

	}

	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
			tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		if (args[1].length() > 17) {
			tell(Lang.of("Commands-Factions-Invite-InvalidUsername")
					.replace("{username}", args[1]));
			return;
		}

		Player invitee = PlayerUtil.getPlayerByNick(args[1], true);

		if (invitee == null) {
			tell(Lang.of("Commands-Pay-UnknownPlayer").replace("{player}", args[1]));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			tell(Lang.of("Commands-Factions-Invite-OfficerRequired"));
			return;
		}

		Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
		String name = args[1];

		if (playerFaction.findMember(name) != null) {
			tell(Lang.of("Commands-Factions-Invite-AlreadyInFaction")
					.replace("{player}", name));
			return;
		}

		if (!Configuration.kitMap) { // && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()
			tell(Lang.of("Commands-Factions-Invite-NoInviteWhileRaidable"));
			return;
		}

		if (!invitedPlayerNames.add(name.toLowerCase())) {
			tell(Lang.of("Commands-Factions-Invite-AlreadyInvited")
					.replace("{player}", name));
			return;
		}

		Player target = PlayerUtil.getPlayerByNick(name, true);;
		if (target != null) {
			name = target.getName(); // fix casing.
			SimpleComponent component = SimpleComponent.of(
			        Lang.of("Commands-Factions-Invite-InviteReceived")
			            .replace("{relationColour}", Relation.ENEMY.toChatColour() + "")
			            .replace("{sender}", sender.getName())
			            .replace("{factionName}", playerFaction.getName())
			    )
			    .onClickRunCmd("/" + getLabel() + " accept " + playerFaction.getName());
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
