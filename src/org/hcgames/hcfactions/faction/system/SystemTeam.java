package org.hcgames.hcfactions.faction.system;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.hcgames.hcfactions.faction.ClaimableFaction;

import lombok.Getter;
import lombok.Setter;
import technology.brk.util.mongo.Mongoable;
@Getter
/*
 * 00:38 4/4/2025 
 * Don't buy shit cores man.
 */
public class SystemTeam  extends ClaimableFaction implements ConfigurationSerializable, Mongoable, SystemFaction{

	    private final UUID uuid;	
	    private final long creationMillis;
	    @Setter private boolean safezone = true;

	    public SystemTeam(String name) {
	        super(name);
	        this.uuid = UUID.randomUUID();
	        this.creationMillis = System.currentTimeMillis();
	    }

	 
	    public String getFormattedName(CommandSender sender) {
	        return ChatColor.GRAY + (hasDisplayName() ? displayName : getName());
	    }
	

	    public boolean hasDisplayName() {
	        return displayName != null && !displayName.isEmpty();
	    }

	    public void sendInformation(CommandSender sender) {
	        sender.sendMessage(ChatColor.YELLOW + "System Faction: " + getFormattedName());
	    }

	    public boolean isSafezone() {
	        return safezone;
	    }

	    public boolean isDeathban() {
	        return !safezone;
	    }

	    public void setDeathban(boolean deathban) {
	        this.safezone = !deathban;
	    }
	    @Override
	    public Map<String, Object> serialize() {
	        Map<String, Object> map = new HashMap<>();

	        map.put("name", getName());
	        map.put("uuid", uuid.toString());
	        map.put("displayName", displayName);
	        map.put("safezone", safezone);
	        map.put("creationMillis", creationMillis);

	        // Puedes incluir claims u otros datos si querés
	        return map;
	    }

	    // -----------------------------
	    // SERIALIZATION METHODS BELOW
	    // -----------------------------

	    @Override
	    public Document toDocument() {
	        Document doc = new Document();

	        doc.put("name", getName());
	        doc.put("uuid", uuid.toString());
	        doc.put("displayName", displayName);
	        doc.put("safezone", safezone);
	        doc.put("creationMillis", creationMillis);

	        // Lo mismo, podés agregar "claims" si los estás guardando en Mongo
	        return doc;
	    }
	
}
