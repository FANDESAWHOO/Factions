package org.hcgames.hcfactions.timer;


import lombok.Getter;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.argument.TimerCheckArgument;
import org.hcgames.hcfactions.timer.argument.TimerSetArgument;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Handles the execution and tab completion of the timer command.
 */
@AutoRegister
public final class TimerExecutor extends SimpleCommand {

    /**
     * The singleton of this class
     */
    @Getter
    private final static SimpleCommand instance = new TimerExecutor();
    private final ArrayList<TimerSubCommand> arguments;

    private void addArgument(TimerSubCommand subCommand){
        arguments.add(subCommand);
    }

    private TimerSubCommand getArgument(String argument){
        for(TimerSubCommand subCommand : arguments)
            if(subCommand.getName().equalsIgnoreCase(argument)) return subCommand;

        return null;
    }

    public TimerExecutor(){
        super("timer");
        arguments = new ArrayList<>();
        addArgument(new TimerCheckArgument(HCFactions.getInstance()));
        addArgument(new TimerSetArgument(HCFactions.getInstance()));
    }
    @Override
    protected List<String> tabComplete() {
        if (args.length == 1) return arguments.stream()
				.map(TimerSubCommand::getName)
				.filter(name -> name.startsWith(args[0].toLowerCase()))
				.collect(Collectors.toList());
        TimerSubCommand sub = getArgument(args[0]);
        return sub != null ? sub.onTabComplete(sender, null, args[0], args) : null;
    }
    @Override
    public void onCommand(){
    	checkPerm(getPermission());
        if(args.length >= 1){
            TimerSubCommand argument = getArgument(args[0]);
            if(argument == null){
                tell(Lang.of("Commands-Unknown-Subcommand")
                        .replace("{subCommand}", args[0])
                        .replace("{commandLabel}", getName()));
                return;
            }else argument.onCommand(sender, getLabel(), args);
        }
       
    }
}
