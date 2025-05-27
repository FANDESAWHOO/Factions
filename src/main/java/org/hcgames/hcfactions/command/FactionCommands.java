package org.hcgames.hcfactions.command;


import lombok.Getter;
import org.hcgames.hcfactions.command.subcommand.FactionClaimChunkCommand;
import org.hcgames.hcfactions.command.subcommand.FactionClaimCommand;
import org.hcgames.hcfactions.command.subcommand.FactionCreateCommand;
import org.hcgames.hcfactions.command.subcommand.FactionDisbandCommand;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoRegister
public class FactionCommands extends SimpleCommandGroup {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static SimpleCommandGroup instance = new FactionCommands();
    @Getter
	private static final List<SimpleSubCommand> arguments = new ArrayList<>();


	/**
	 * Extending method to register subcommands, call
	 * {@link #registerSubcommand(SimpleSubCommand)} and {@link #registerHelpLine(String...)}
	 * there for your command group.
	 */
	@Override
	protected void registerSubcommands() {
    	registerSubcommand(FactionCreateCommand.class);
		registerSubcommand(FactionClaimCommand.class);
		registerSubcommand(FactionClaimChunkCommand.class);
		registerSubcommand(FactionDisbandCommand.class);
	}
	private FactionCommands(){
		super("faction", Arrays.asList("f","fac"));
	}

}
