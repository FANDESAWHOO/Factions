package org.hcgames.hcfactions.faction;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.event.faction.FactionRenameEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.util.Mongoable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode
public abstract class Faction implements Mongoable, ConfigurationSerializable {

	protected final long creationMillis;
	private final UUID uuid;
	@Getter
	protected String displayName;
	@Setter
	@Getter
	protected double dtrLossMultiplier = 1.0;
	@Getter
	protected double deathbanMultiplier = 1.0;
	@Getter
	protected boolean safezone;
	private String name;
	@Getter
	private long lastRenameMillis;

	private Faction() {
		throw new RuntimeException("This should never happen");
	}

	public Faction(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
		creationMillis = System.currentTimeMillis();
	}

	public Faction(String name) {
		this(name, UUID.randomUUID());
	}

	public Faction(Map<String, Object> map) {
		uuid = UUID.fromString((String) map.get("uuid"));
		name = (String) map.get("name");
		creationMillis = Long.valueOf((String) map.get("creationMillis"));
		lastRenameMillis = Long.valueOf((String) map.get("lastRenameMillis"));
		safezone = (boolean) map.get("safezone");

		if (map.containsKey("displayName")) displayName = (String) map.get("displayName");
	}

	public Faction(Document document) {
		uuid = UUID.fromString(document.getString("_id"));
		name = document.getString("name");
		creationMillis = Long.valueOf(document.getString("creationMillis"));
		lastRenameMillis = Long.valueOf(document.getString("lastRenameMillis"));
		safezone = document.getBoolean("safezone");

		if (document.containsKey("displayName")) displayName = document.getString("displayName");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("uuid", uuid.toString());
		map.put("name", name);
		map.put("creationMillis", Long.toString(creationMillis));
		map.put("lastRenameMillis", Long.toString(lastRenameMillis));
		map.put("safezone", safezone);
		map.put("directpath", getClass().getName());
		if (hasDisplayName()) map.put("displayName", displayName);
		return map;
	}

	@Override
	public Document toDocument() {
		Document document = new Document();
		document.put("_id", uuid.toString());
		document.put("name", name);
		document.put("creationMillis", Long.toString(creationMillis));
		document.put("lastRenameMillis", Long.toString(lastRenameMillis));
		document.put("safezone", safezone);
		document.put("directpath", getClass().getName());
		if (hasDisplayName()) document.put("displayName", displayName);
		return document;
	}

	public String getName() {
		return name;
	}

	public boolean setName(String name) {
		return setName(name, Bukkit.getServer().getConsoleSender());
	}

	public boolean setName(String name, CommandSender sender) {
		if (this.name.equals(name)) return false;

		FactionRenameEvent event = new FactionRenameEvent(sender, this, this.name, name, false);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) return false;

		this.name = name;
		return true;
	}

	private boolean hasDisplayName() {
		return displayName != null;
	}

	public boolean setDisplayName(String name) {
		return setDisplayName(name, Bukkit.getServer().getConsoleSender());
	}

	public boolean setDisplayName(String name, CommandSender sender) {
		if (displayName.equals(name)) return false;

		FactionRenameEvent event = new FactionRenameEvent(sender, this, displayName, name, true);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) return false;

		displayName = name;
		return true;
	}

	public String getFormattedName() {
		return getFormattedName(Bukkit.getServer().getConsoleSender());
	}

	public String getFormattedName(CommandSender sender) {
		return getRelation(sender).toChatColour() + (hasDisplayName() ? getDisplayName() : getName());
	}

	public ChatColor getColorRelation(CommandSender sender) {
		return getRelation(sender).toChatColour();
	}

	public String getFormattedName(Faction faction) {
		return getFactionRelation(faction).toChatColour() + (hasDisplayName() ? getDisplayName() : getName());
	}

	public void sendInformation(CommandSender sender) {
	}

	public UUID getUniqueID() {
		return uuid;
	}

	public boolean isDeathban() {
		return !safezone && deathbanMultiplier > 0.0D;
	}

	public void setDeathban(boolean deathban) {
		if (deathban != isDeathban()) deathbanMultiplier = deathban ? 1.0D : 0.5D;
	}

	public void setDeathbanMultiplier(double deathbanMultiplier) {
		Preconditions.checkArgument(deathbanMultiplier >= 0, "Deathban multiplier may not be negative");
		this.deathbanMultiplier = deathbanMultiplier;
	}

	public Relation getFactionRelation(Faction faction) {
		if (faction instanceof PlayerFaction) {
			PlayerFaction playerFaction = (PlayerFaction) faction;
			if (playerFaction == this) return Relation.MEMBER;

			if (playerFaction.getAllied().contains(uuid)) return Relation.ALLY;
		}

		return Relation.ENEMY;
	}

	public Relation getRelation(CommandSender sender) {
		try {
			return sender instanceof Player ? getFactionRelation(FactionsAPI.getPlayerFaction((Player) sender)) : Relation.ENEMY;
		} catch (NoFactionFoundException e) {
			return Relation.ENEMY;
		}
	}
}
