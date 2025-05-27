package org.hcgames.hcfactions.faction.system;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.util.Mongoable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	        uuid = UUID.randomUUID();
	        creationMillis = System.currentTimeMillis();
	    }

	 
	    @Override
		public String getFormattedName(CommandSender sender) {
	        return ChatColor.GRAY + (hasDisplayName() ? displayName : getName());
	    }
	

	    public boolean hasDisplayName() {
	        return displayName != null && !displayName.isEmpty();
	    }

	    @Override
		public void sendInformation(CommandSender sender) {
	        sender.sendMessage(ChatColor.YELLOW + "System Faction: " + getFormattedName());
	    }

	@Override
	public boolean isDeathban() {
	        return !safezone;
	    }

	    @Override
		public void setDeathban(boolean deathban) {
	        safezone = !deathban;
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

	        return doc;
	    }
	
}
