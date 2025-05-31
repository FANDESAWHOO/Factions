package org.hcgames.hcfactions.command.subcommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

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

		if(args[1].length() > 17){
			tell(Lang.of("Commands-Factions-Invite-InvalidUsername")
					.replace("{username}", args[1]));
			return;
		}

		Player invitee = plugin.getServer().getPlayer(args[1]);

		if(invitee == null){
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

		Player target = Bukkit.getPlayer(name);
		if (target != null) {
			name = target.getName(); // fix casing.
			TextComponent component = Component.text().content(Lang.of("Commands-Factions-Invite-InviteReceived")
					.replace("{relationColour}", Relation.ENEMY.toChatColour() + "")
					.replace("{sender}", sender.getName())
					.replace("{factionName}", playerFaction.getName()))
					.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/" + getLabel() + " accept " + playerFaction.getName()))
					.build();
		/*FIXED?	FancyMessage message = new FancyMessage(Lang.of("Commands-Factions-Invite-InviteReceived")
					.replace("{relationColour}", Relation.ENEMY.toChatColour() + "")
					.replace("{sender}", sender.getName())
					.replace("{factionName}", playerFaction.getName()));
			message.command("/" + getLabel() + " accept " + playerFaction.getName());
			message.send(target);
            /*net.md_5.bungee.api.ChatColor enemyRelationColor = toBungee(Relation.ENEMY.toChatColour());
            ComponentBuilder builder = new ComponentBuilder(sender.getName()).color(enemyRelationColor);
            builder.append(" has invited you to join ", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.YELLOW);
            builder.append(playerFaction.getName()).color(enemyRelationColor).append(". ", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.YELLOW);
            builder.append("Click here").color(net.md_5.bungee.api.ChatColor.GREEN).
                    event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " accept " + playerFaction.getName())).
                    event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.AQUA).
                            append(playerFaction.getName(), ComponentBuilder.FormatRetention.NONE).color(enemyRelationColor).
                            append(".", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.AQUA).create()));
            builder.append(" to accept this invitation.", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.YELLOW);
            target.spigot().sendMessage(builder.create());*/

		}

		playerFaction.broadcast(Lang.of("Commands-Factions-Invite-InviteBroadcast")
				.replace("{player}", sender.getName()) //I don't get this, surly it should get the relation, not assume?
				.replace("{invitee}", name));
		// playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has invited " + Relation.ENEMY.toChatColour() + name + ChatColor.YELLOW + " into the faction.");

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
