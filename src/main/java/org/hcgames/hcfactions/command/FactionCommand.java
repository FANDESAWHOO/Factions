package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class to handle the command and tab completion for the faction command.
 */

@AutoRegister
public final class FactionCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static FactionCommand instance = new FactionCommand();
	@Getter
	private final List<FactionSubCommand> subCommands = new ArrayList<>();

	public FactionCommand() {
		super("faction | fac | f");
		registerFactionSubcommands(FactionSubCommand.class);
	}

	protected FactionSubCommand getById(String args){
		for(FactionSubCommand subCommand : subCommands)
			if(subCommand.getName().equalsIgnoreCase(args))
				return subCommand;
		return null;
	}

	protected final void registerFactionSubcommands(Class<? extends FactionSubCommand> parentClass) {
		for (Class<? extends FactionSubCommand> clazz : ReflectionUtil.getClasses(HCFactions.getInstance(), parentClass)) {
			if (Modifier.isAbstract(clazz.getModifiers()))
				continue;

			Valid.checkBoolean(Modifier.isFinal(clazz.getModifiers()), "Make child of " + parentClass.getSimpleName() + " class " + clazz.getSimpleName() + " final to auto register it!");

			try {
				FactionSubCommand subCommand = ReflectionUtil.instantiate(clazz);
				subCommand.addArgument(); // <- LLÁMALO AQUÍ, una vez FactionCommand ya está instanciado
			} catch (Throwable t) {
				Common.error(t, "Failed to register subcommand: " + clazz.getSimpleName());
			}
		}
	}

	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	protected void onCommand() {
		if (args.length == 1) for (FactionSubCommand subCommand : getSubCommands())
			if (args[0].startsWith(subCommand.getName())) {
				String permission = subCommand.getPermission();
				if (permission == null || sender.hasPermission(permission)) {
					subCommand.execute(sender, getLabel(), args);
					return;
				} else getById("help").execute(sender, "help", args);
			}
	}
}
