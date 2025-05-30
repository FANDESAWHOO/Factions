package org.hcgames.hcfactions.command.argument.staff;


import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.system.SystemTeam;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;



public final class FactionCreateSystemArgument extends FactionSubCommand {
	
	private final HCFactions plugin;
	
	public FactionCreateSystemArgument() {
	    super("createsystem");
	    setDescription("Create a system faction.");
		plugin = HCFactions.getInstance();
      //  this.permission = "hcf.command.faction.argument." + getName();
	}


	@Override
	public String getUsage() {
		return "f createsystem name";
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
            tell(Lang.of("Command.error.usage", getUsage()));
            return;
        }
	    String name = args[1];
	    int value = Configuration.factionNameMinCharacters;
	   
	    if (name.length() < value) {
            tell(Lang.of("Commands-Factions-Create-MinimumChars")
                    .replace("{minChars}", String.valueOf(value)));
            return;
        }

        value = Configuration.factionNameMaxCharacters;

        if (name.length() > value) {
            tell(Lang.of("Commands-Factions-Create-MaximumChars")
                    .replace("{maxChars}", String.valueOf(value)));
            return;
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            tell(Lang.of("Commands-Factions-Create-MustBeAlphanumeric"));
            return;
        }

        try {
            if(plugin.getFactionManager().getFaction(name) != null){
                tell(Lang.of("Commands-Factions-Create-NameAlreadyExists")
                        .replace("{factionName}", name));
                return;
            }
        } catch (NoFactionFoundException e) {}
        plugin.getFactionManager().createFaction(new SystemTeam(name), sender);
		return;
	}

}
