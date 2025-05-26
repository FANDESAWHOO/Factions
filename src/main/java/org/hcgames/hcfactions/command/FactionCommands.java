package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class FactionCommands extends SimpleCommandGroup {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static SimpleCommandGroup instance = new FactionCommands();

	/**
	 * Extending method to register subcommands, call
	 * {@link #registerSubcommand(SimpleSubCommand)} and {@link #registerHelpLine(String...)}
	 * there for your command group.
	 */
	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(MainSubCommand.class);
	}

	/**
	 * Represents the foundation for plugin commands
	 * used for /channel and /chc subcommands
	 */
	public static abstract class GenericSubCommand extends SimpleSubCommand  {

		protected GenericSubCommand(final SimpleCommandGroup group, final String sublabel) {
			super(group, sublabel);
		}

		/**
		 * @see org.mineacademy.fo.command.SimpleCommand#completeLastWordPlayerNames()
		 */
		@Override
		protected final List<String> completeLastWordPlayerNames() {
			return Common.getPlayerNames();
		}

		/**
		 * @see org.mineacademy.fo.command.SimpleCommand#findPlayerInternal(java.lang.String)
		 */
		@Override
		public final Player findPlayerInternal(final String name) {
			return findPlayer(name);
		}
	}

	/**
	 * Represents the foundation for plugin commands
	 * used for /channel and /chc subcommands
	 */
	public static abstract class MainSubCommand extends GenericSubCommand {

		protected MainSubCommand(final String sublabel) {
			super(HCFactions.getInstance().getMainCommand(), sublabel);
		}
	}
}
