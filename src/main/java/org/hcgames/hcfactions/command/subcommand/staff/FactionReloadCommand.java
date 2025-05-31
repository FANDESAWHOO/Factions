package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.hcgames.hcfactions.command.FactionSubCommand;

import java.util.Collections;
import java.util.List;
/**
 * Why SystemUpdate removed this?
 * Other thing i don't gonna know.
 */
public final class FactionReloadCommand extends FactionSubCommand {

   
    public FactionReloadCommand() {
        super("reload");
        setDescription( "Reloads the messages file from .");
      //  this.permission = "hcf.command.faction.argument." + getName();
    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        //plugin.getMessages().reload();
        tell(ChatColor.RED + "Command disabled.");
        return;
    }

    @Override
    public List<String> tabComplete() {
        return args.length == 2 ? null : Collections.emptyList();
    }
}
