package org.hcgames.hcfactions.timer;


import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.argument.TimerCheckArgument;
import org.hcgames.hcfactions.timer.argument.TimerSetArgument;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;


/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends SimpleCommand {

    private final ArrayList<TimerSubCommand> arguments;

    private void addArgument(TimerSubCommand subCommand){
        arguments.add(subCommand);
    }

    private TimerSubCommand getArgument(String argument){
        for(TimerSubCommand subCommand : arguments)
            if(subCommand.getName().equalsIgnoreCase(argument)) return subCommand;

        return null;
    }

    public TimerExecutor(HCFactions plugin){
        super("timer");
        arguments = new ArrayList<>();
        addArgument(new TimerCheckArgument(plugin));
        addArgument(new TimerSetArgument(plugin));
    }

    @Override
    public void onCommand(){
        if(args.length >= 1){
            TimerSubCommand argument = getArgument(args[0]);
            if(argument == null){
                sender.sendMessage(Lang.of("Commands-Unknown-Subcommand")
                        .replace("{subCommand}", args[0])
                        .replace("{commandLabel}", getName()));
                return;
            }else return argument.onCommand(sender, getLabel(), args);
        }

        return super.onCommand(sender, getLabel(), args);
    }
}
