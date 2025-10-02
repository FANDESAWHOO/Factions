package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.hcgames.hcfactions.command.subcommand.*;
import org.hcgames.hcfactions.command.subcommand.staff.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to handle the command and tab completion for the faction command.
 */

@AutoRegister
public final class FactionCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static FactionCommand instance = new FactionCommand();
	@Getter
	private final List<FactionSubCommand> commands = new LinkedList<>();
	private final FactionHelpCommand help;

	private FactionCommand() {
		super("faction|fac|f");
		setAutoHandleHelp(false);
		setMinArguments(0);
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionAllyCommand());
		addArgument(new FactionAnnouncementCommand());
		addArgument(new FactionChatCommand());
		//TODO addArgument(new FactionChatSpyArgument());
		addArgument(new FactionClaimCommand());
		addArgument(new FactionClaimChunkCommand());
		addArgument(new FactionClaimForCommand());
		addArgument(new FactionClearClaimsCommand());
		addArgument(new FactionDepositCommand());
		addArgument(new FactionDisbandCommand());
		addArgument(new FactionSetDtrRegenCommand());
		addArgument(new FactionForceDemoteCommand());
		addArgument(new FactionForceJoinCommand());
		addArgument(new FactionForceKickCommand());
		addArgument(new FactionForceLeaderCommand());
		addArgument(new FactionForcePromoteCommand());
		addArgument(new FactionForceUnclaimHereCommand());
		addArgument(new FactionSaveCommand());
		addArgument(help = new FactionHelpCommand());
		addArgument(new FactionHomeCommand());
		addArgument(new FactionInvitesCommand());
		addArgument(new FactionKickCommand());
		addArgument(new FactionLeaveCommand());
		addArgument(new FactionListCommand());
		addArgument(new FactionMapCommand());
		addArgument(new FactionMessageCommand());
		addArgument(new FactionMuteCommand());
		addArgument(new FactionBanCommand());
		addArgument(new FactionOpenCommand());
		addArgument(new FactionRemoveCommand());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionPromoteCommand());
		addArgument(new FactionSetDtrCommand());
		addArgument(new FactionSetDeathbanMultiplierCommand());
		addArgument(new FactionSetHomeCommand());
		addArgument(new FactionShowCommand());
		addArgument(new FactionStuckCommand());
		addArgument(new FactionUnclaimCommand());
		addArgument(new FactionUnallyArgument());
		addArgument(new FactionUninviteCommand());
		addArgument(new FactionWithdrawCommand());
		//addArgument(new FactionLivesCommand());
		addArgument(new FactionFocusCommand());
		addArgument(new FactionRemoveCooldownCommand());
		addArgument(new FactionReloadCommand());
		addArgument(new FactionForceRenameCommand());
		addArgument(new FactionSnowCommand());
		addArgument(new FactionCreateCommand());
		addArgument(new FactionPastFactionsCommand());
	}

	public void addArgument(FactionSubCommand class1) {
		commands.add(class1);
	}

	public FactionSubCommand getSubCommand(String key) {
		for (FactionSubCommand subCommand : commands) {
			if (subCommand.getName().equalsIgnoreCase(key)) return subCommand;
			if (subCommand.matches(key)) return subCommand;
		}


		return null;
	}


	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */

	@Override
	protected void onCommand() {
	//	checkPerm(getPermission());

		if (args.length == 0) {
			help.execute(sender, getLabel(), args);
			return;
		}

		FactionSubCommand sub = getSubCommand(args[0]);
		if (sub == null) {
			// Mensaje de error mejorado
			tell(Lang.of("Commands-Unknown-Subcommand")
					.replace("{subCommand}", args[0])
					.replace("{commandLabel}", getLabel()));
			return;
		}
		sub.execute(sender, getLabel(), args);
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length == 1) return commands.stream()
				.flatMap(sub -> Stream.concat(
						Stream.of(sub.getName()),
						Arrays.stream(sub.getAliases())
				))
				.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
				.collect(Collectors.toList());
		if (args.length >= 2) {
			FactionSubCommand sub = getSubCommand(args[0]);
			if (sub != null) {
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
				return sub.onTabComplete(sender, null, getLabel(), subArgs);
			}
		}
		return Collections.emptyList();
	}
}
