package org.hcgames.hcfactions.nametag;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.nametag.adapter.FactionsAdapter;
import org.hcgames.hcfactions.nametag.listener.NametagListener;
import org.hcgames.hcfactions.util.JavaUtils;
import org.hcgames.hcfactions.util.Tasks;
import org.hcgames.hcfactions.util.text.FastReplaceString;
import org.mineacademy.fo.MinecraftVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NametagManager {
	private final Map<UUID, Nametag> nametags;
	private final NametagAdapter adapter;
	private final HCFactions instance;
	private final Boolean placeHolder;

	private String parsePapi(Player player, String text) {
		return (placeHolder) ?
				PlaceholderAPI.setPlaceholders(player, text) : text;
	}
	public NametagManager() {
		nametags = new ConcurrentHashMap<>();
		adapter = new FactionsAdapter();
		instance = HCFactions.getInstance();
		placeHolder = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

		Bukkit.getPluginManager().registerEvents(new NametagListener(), HCFactions.getInstance());
	}


  public void handleUpdate(Player viewer, Player target) {
	  if (viewer == null || target == null) return; // Possibly?
	  String prefix = getAdapter().getAndUpdate(viewer, target);
	  updateLunarTags(viewer, target, prefix);
  }

  public void updateLunarTags(Player viewer, Player target, String prefix) {
	//  if (getInstance().getClientHook().getClients().isEmpty()) return;
	  if (!nametags.containsKey(viewer.getUniqueId())) return;

	/*  FactionUser user = getInstance().getUserManager().getUser(viewer.getUniqueId());

	  if (!user.isLunarNametags()) {
		  if (user.isClearedNametags()) return;
		  user.setClearedNametags(true);
		  getInstance().getClientHook().clearNametags(viewer);
		  return;
	  }*/

	  PlayerFaction pt = FactionsAPI.getPlayerFaction(target);
	//  String pos = getInstance().getUserManager().getPrefix(target);
	  String name = prefix + target.getName();
	//  String killTag = (pos != null ? pos : "");
	  String teamTag = getTeamTag(pt, viewer);
	 // boolean vanished = getInstance().getStaffManager().isVanished(target);
	  //user.setClearedNametags(false);

	/*  if (getInstance().getStaffManager().isStaffEnabled(target)) {
		  List<String> format = new ArrayList<>();

		  for (String s : Config.NAMETAG_MOD_MODE) {
			  String line = s
					  .replace("%name%", name)
					  .replace("%killtag%", killTag)
					  .replace("%teamtag%", teamTag)
					  .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(target)))
					  .replace("%rank-name%", CC.t(getInstance().getRankHook().getRankName(target)))
					  .replace("%vanishsymbol%", (vanished ? Config.VANISHED_SYMBOL : ""));
			  line = parsePapi(target, line);
			  format.add(line);
		  }

		  handleLunar(target, viewer, format);
		  return;
	  }*/

	  if (pt != null) {
		  List<String> format = new ArrayList<>();

		  for (String s : Configuration.NAMETAG_IN_TEAM) {
			  String line = s
					  .replace("%name%", name)
					  .replace("%teamtag%", teamTag);
			  line = parsePapi(target, line);
			  format.add(line);
		  }

		  handleLunar(target, viewer, format);
		  return;
	  }

	  List<String> format = new ArrayList<>();

	  for (String s : Configuration.NAMETAG_NO_TEAM) {
		  String line = s
				  .replace("%name%", name)
				  .replace("%teamtag%", teamTag);
		  line = parsePapi(target, line);
		  format.add(line);
	  }

	  handleLunar(target, viewer, format);
  }

  private void handleLunar(Player target, Player viewer, List<String> format) {
	  // you can't send the packet asynchronously in modern versions
	  MinecraftVersion.V version = MinecraftVersion.V.v1_16;
	  if (MinecraftVersion.newerThan(version) || MinecraftVersion.equals(version)) {
		  Tasks.execute(getInstance(), () -> getInstance().getClientAPI().overrideNametags(target, viewer, format));
		  return;
	  }

	  getInstance().getClientAPI().overrideNametags(target, viewer, format);
  }

  private String getTeamTag(PlayerFaction pt, Player viewer) {
	  if (pt == null) return "";
	  return new FastReplaceString(Configuration.NAMETAGS_NORMAL)
			  .replaceAll("%name%", pt.getFormattedName(viewer))
			  .replaceAll("%dtr%", JavaUtils.format(pt.getDeathsUntilRaidable(false)))
			  .replaceAll("%dtr-color%", pt.getDtrColour().toString())
			  .endResult();
  }

}
