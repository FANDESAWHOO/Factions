package org.hcgames.hcfactions.util.text;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.World;

public class Names {

	//TODO: Move into Base or make configurable
	private static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = /*TODO:Maps.immutableEnumMap*/(ImmutableMap.of(
			World.Environment.NETHER, "Nether",
			World.Environment.NORMAL, "Overworld",
			World.Environment.THE_END, "The End"
	));

	public static String getEnvironmentName(World.Environment environment){
		return ENVIRONMENT_MAPPINGS.containsKey(environment) ? ENVIRONMENT_MAPPINGS.get(environment) : StringUtils.capitalize(environment.toString());
	}
}