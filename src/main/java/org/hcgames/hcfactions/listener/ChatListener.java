package org.hcgames.hcfactions.listener;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;


public class ChatListener implements Listener {


	@Getter
	private static final ChatListener chatListener = new ChatListener();
	private final boolean placeholder;
	private ChatListener() {
		placeholder = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}

	public static String parsePapi(Player player, String text) {
		return PlaceholderAPI.setPlaceholders(player, text);
	}

	/**
	 * Checks if a message should be posted in {@link ChatChannel#PUBLIC}.
	 *
	 * @param input the message to check
	 * @return true if the message should be posted in {@link ChatChannel#PUBLIC}
	 */
	public static boolean isGlobalChannel(String input) {
		int length = input.length();

		if (length > 1 && input.startsWith("!")) for (int i = 1; i < length; i++) {
			char character = input.charAt(i);

			// Ignore whitespace to prevent blank messages
			if (Character.isWhitespace(character)) continue;

			// Player is faking a command
			return character != '/';
		}

		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();

		PlayerFaction playerFaction = FactionsAPI.hasFaction(player) ? FactionsAPI.getPlayerFaction(player) : null;
		String displayName = player.getDisplayName();
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		String defaultFormat = (placeholder ? parsePapi(player, getChatFormat(player, playerFaction, console)) : getChatFormat(player, playerFaction, console));

		// Handle the custom messaging here.
		event.setFormat(defaultFormat);
		event.setCancelled(true);
		console.sendMessage(String.format(defaultFormat, displayName, message));
		for (Player recipient : event.getRecipients())
			recipient.sendMessage(String.format(getChatFormat(player, playerFaction, recipient), displayName, message));
	}

	private String getChatFormat(Player player, PlayerFaction playerFaction, CommandSender viewer) {
		String factionTag = (playerFaction == null ? ChatColor.RED.toString() + '*' : playerFaction.getFormattedName(viewer));
		String result;
		result = ChatColor.GOLD + "[" + factionTag + ChatColor.GOLD + "] %1$s" + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s";


		return result;
	}
}