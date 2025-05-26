package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.subcommand.FactionCreateCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.Arrays;
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
    	registerSubcommand(FactionCreateCommand.class);
	}
	public FactionCommands(){
		super("faction", Arrays.asList("f","fac"));
	}

}
