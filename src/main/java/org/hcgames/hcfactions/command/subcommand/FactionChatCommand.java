package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.*;

public final class FactionChatCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionChatCommand() {
		super("chat|c");
		setDescription("Toggle faction chat only mode on or off.");
		plugin = HCFactions.getInstance();
	}

    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " [fac|public|ally] [message]";
	}

	@Override
	public void onCommand() {


		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
		} catch (NoFactionFoundException e) {
			tell(ChatColor.RED + "You are not in a faction.");
			return;
		}

		FactionMember member = playerFaction.getMember(player.getUniqueId());
		ChatChannel currentChannel = member.getChatChannel();
		ChatChannel parsed;

		if(args.length >= 2){
			parsed = ChatChannel.parse(args[1], null);

			if(parsed != null && parsed == ChatChannel.OFFICER && member.getRole() == Role.MEMBER){
				tell(Lang.of("Commands-Factions-Chat-OfficerOnly"));
				return;
			}
		}else{
			parsed = currentChannel.getRotation();

			if(parsed == ChatChannel.OFFICER && member.getRole() == Role.MEMBER) parsed = currentChannel.getRotation();
		}

		if (parsed == null && currentChannel != ChatChannel.PUBLIC) {
			Collection<Player> recipients = playerFaction.getOnlinePlayers();
			if (currentChannel == ChatChannel.ALLIANCE)
				for (PlayerFaction ally : playerFaction.getAlliedFactions()) recipients.addAll(ally.getOnlinePlayers());

			String format = String.format(currentChannel.getRawFormat(player), "", HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length)));
			for (Player recipient : recipients) recipient.sendMessage(format);

			// spawn radius, border, allies, minigames,
			return;
		}

		ChatChannel newChannel = parsed == null ? currentChannel.getRotation() : parsed;

		if(newChannel == ChatChannel.OFFICER && member.getRole() == Role.MEMBER) newChannel = newChannel.getRotation();

		member.setChatChannel(newChannel);
		tell(Lang.of("Commands-Factions-Chat-SwitchedMode")
				.replace("{newMode}", newChannel.getDisplayName().toLowerCase()));

	}

	@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

		ChatChannel[] values = ChatChannel.values();
		List<String> results = new ArrayList<>(values.length);
		for (ChatChannel type : values) results.add(type.getName());

		return results;
	}
}
