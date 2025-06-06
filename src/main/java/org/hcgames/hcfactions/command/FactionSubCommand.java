package org.hcgames.hcfactions.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.util.text.CC;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class to make more easy
 * Handle faction subcommands
 * because Foundation sucks
 * on this, (at least for me)
 */
@Getter
public abstract class FactionSubCommand implements TabCompleter {
	/**
	 * INFO FOR THE COMMAND
	 */
	private final String name;
	@Setter protected boolean isPlayerOnly = false;
	@Setter protected String description;
	@Setter protected String permission;
	@Setter protected String[] aliases;
	/**
	 * COMMAND FUNCTIONS!
	 */
	protected CommandSender sender;
	protected String label;
	protected String[] args;

	/**
	 * For have a method to return
	 * All SubCommands for /f help
	 *
	public void addArgument(){
	 if(!FactionCommand.getInstance().getCommands().contains(this))
        FactionCommand.getInstance().getCommands().add(this);
	}*/

	/**
	 * Simple methods to
	 * Copy Simple Command!
	 */
	protected boolean checkPerm(){
		if(!(sender.hasPermission(getPermission()))){
			sender.sendMessage(Lang.of("No_Permission"));
			return false;
		}
		return true;
	}
	protected void tell(String label){
		sender.sendMessage(CC.translate(label));
	}
	protected boolean checkConsole() {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.of("Commands.No_Console"));
			return false;
		}
		return true;
	}

	public FactionSubCommand(String rawName) {
		String[] parts = rawName.split("\\|");
		name = parts[0].trim();
		aliases = Arrays.stream(parts)
				.skip(1)
				.map(String::trim)
				.toArray(String[]::new);
		permission = "faction.command." + name;
		description = "No description set";
	//	addArgument();
	}

	public boolean matches(String input) {
		if (name.equalsIgnoreCase(input))
			return true;
		for (String alias : aliases)
			if (alias.equalsIgnoreCase(input))
				return true;
		return false;
	}

	public FactionSubCommand(String rawName, String description) {
		String[] parts = rawName.split("\\|");
		name = parts[0].trim();
		aliases = Arrays.stream(parts)
				.skip(1)
				.map(String::trim)
				.toArray(String[]::new);

		this.description = description;
		permission = "faction.command." + name;
	//	addArgument();
	}

	public FactionSubCommand(String rawName, String description, String permission) {
		String[] parts = rawName.split("\\|");
		name = parts[0].trim();
		aliases = Arrays.stream(parts)
				.skip(1)
				.map(String::trim)
				.toArray(String[]::new);

		this.description = description;
		this.permission = permission;
	//	addArgument();
	}
	public void execute(CommandSender sender, String label, String[] args) {
		this.sender = sender;
		this.label = label;
		this.args = args;
		if (isPlayerOnly && !(sender instanceof Player)) {
			sender.sendMessage(Lang.of("Commands.No_Console"));
			return;
		}
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(Lang.of("No_Permission"));
			return;
		}

		onCommand();
	}
	public abstract void onCommand();

	public abstract String getUsage();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		this.sender = sender;
		this.label = label;
		this.args = args;

		return tabComplete();
	}

	protected List<String> tabComplete() {
		return Collections.emptyList();
	}
}
