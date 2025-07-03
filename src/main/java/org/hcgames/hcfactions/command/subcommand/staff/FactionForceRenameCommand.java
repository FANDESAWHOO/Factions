/*
 *   COPYRIGHT NOTICE
 *
 *   Copyright (C) 2016, SystemUpdate, <admin@systemupdate.io>.
 *
 *   All rights reserved.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 *   NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 *   OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *   Except as contained in this notice, the name of a copyright holder shall not
 *   be used in advertising or otherwise to promote the sale, use or other dealings
 *   in this Software without prior written authorization of the copyright holder.
 */

package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.defaults.BukkitCommand;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;
import org.mineacademy.fo.settings.Lang;


public final class FactionForceRenameCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionForceRenameCommand() {
		super("forcename");
		setDescription("Forces a rename of a faction.");
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}


	@Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <oldName> <newName>";
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
			tell(ChatColor.RED + "Usage: " + getUsage());
			return;
		}

		String newName = args[2];

		if (Configuration.factionDisallowedNames.contains(newName)) {
			//tell(ChatColor.RED + "'" + newName + "' is a blocked faction name.");
			tell(Lang.of("Commands-Factions-Rename-BlockedName")
					.replace("{factionName}", newName));
			return;
		}

		int value = Configuration.factionNameMinCharacters;

		if (newName.length() < value) {
			//tell(ChatColor.RED + "Faction names must have at least " + value + " characters.");
			tell(Lang.of("Commands-Factions-Rename-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (newName.length() > value) {
			//tell(ChatColor.RED + "Faction names cannot be longer than " + value + " characters.");
			tell(Lang.of("Commands-Factions-Rename-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(newName)) {
			//tell(ChatColor.RED + "Faction names may only be alphanumeric.");
			tell(Lang.of("Commands-Factions-Rename-MustBeAlphanumeric"));
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(newName) != null) {
				//tell(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
				tell(Lang.of("Commands-Factions-Rename-NameAlreadyExists")
						.replace("{factionNewName}", newName));
				return;
			}
		} catch (NoFactionFoundException ignored) {
		}


		plugin.getFactionManager().advancedSearch(args[1], Faction.class, new SearchCallback<Faction>() {
			@Override
			public void onSuccess(Faction faction) {
				String oldName = faction.getName();
				if (faction.setName(newName, sender))
					BukkitCommand.broadcastCommandMessage(sender, ChatColor.YELLOW + "Renamed " + oldName + " to " + faction.getName(), true);
			}

			@Override
			public void onFail(FailReason reason) {
				tell(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});

		return;
	}

}
