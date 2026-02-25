package org.hcgames.hcfactions;

import java.util.List;

import org.bukkit.ChatColor;
import org.hcgames.hcfactions.util.configuration.Config;

public final class Lang {
    private static final Config lang = HCFactions.getInstance().getLang();
    
	public static String of(String path) {
		if (lang.getString(path) == null)
			return "Something is wrong on the specify path: " +path;
		return ChatColor.translateAlternateColorCodes('&', lang.getString(path));
	}
	
	public static Object ofList(String path, Object type) {
		return lang.get(path, type);
	}
	
	public static List<String> ofList(String path) {
		return lang.getStringList(path);
	}
	
	/*
	 * Return a key from our localization, failing if not exists
	 */
	private static String getStringStrict(String path) {
		final String key = of(path);
		if (key == null)
			return "Something is wrong on the specify path: " +path;

		return ChatColor.translateAlternateColorCodes('&', key);
	}
	
	public static String of(String path, String... args) {
	    String message = of(path);

	    if (message == null) {
	        return ChatColor.RED + "Missing lang key: " + path;
	    }

	    for (int i = 0; i < args.length; i++) {
	        message = message.replace("%" + (i + 1) + "%", args[i]);
	    }

	    return ChatColor.translateAlternateColorCodes('&', message);
	}

}
