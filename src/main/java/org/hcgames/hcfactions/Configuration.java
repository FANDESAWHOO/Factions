package org.hcgames.hcfactions;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class Configuration extends SimpleSettings {

	public static Byte factionNameMinCharacters;
	public static Byte factionNameMaxCharacters;
	public static Byte factionMaxMembers;
	public static Byte factionMaxClaims;
	public static Byte factionMaxAllies;

	public static Byte factionDtrRegenFreezeBaseMinutes;
	public static Long factionDtrRegenFreezeBaseMilliseconds;
	public static Byte factionDtrRegenFreezeMinutesPerMember;
	public static Long factionDtrRegenFreezeMillisecondsPerMember;
	public static Byte factionMinimumDtr;
	public static Float factionMaximumDtr;
	public static Integer factionDtrUpdateMillis;
	public static Float factionDtrUpdateIncrement;

	public static ChatColor relationColourWarzone;
	public static ChatColor relationColourWilderness;
	public static ChatColor relationColourTeammate;
	public static ChatColor relationColourAlly;
	public static ChatColor relationColourEnemy;
	public static ChatColor relationColourRoad;
	public static ChatColor relationColourSafezone;

	public static Byte antiRotationDelay;
	public static Boolean antiRotationEnabled;

	public static Boolean factionEndPortalEnabled;
	public static Short endPortalCenter;

	public static Short spawnRadiusOverworld;
	public static Short spawnRadiusNether;

	public static Short roadWidthLeft;
	public static Short roadWidthRight;
	public static Short roadLength;
	public static Short endPortalRadius;
	public static Boolean allowClaimsBesidesRoads;

	public static Short warzoneRadiusOverworld;
	public static Short warzoneRadiusNether;

	public static List<String> factionDisallowedNames;

	public static Byte factionHomeTeleportDelayNormal;
	public static Byte factionHomeTeleportDelayNether;
	public static Byte factionHomeTeleportDelayEnd;
	public static Boolean allowTeleportingInEnemyTerritory;
	public static Short maxHeightFactionHome;

	public static Boolean subclaimSignPrivate;
	public static Boolean subclaimSignCaptain;
	public static Boolean subclaimSignLeader;
	public static Boolean subclaimHopperCheck;
	public static Boolean kitMap;
	public static Boolean preventAllyAttackDamage;
	public static Boolean messageDebug;

	public static String host;
	public static String database;
	public static String username;
	public static String password;

	public static Boolean api;
	public static Boolean customEvents;

	public static Boolean mongo;

	private static void init() {

		mongo = getBoolean("mongo.use");
		host = getString("mongo.host");
		database = getString("mongo.database");
		username = getString("mongo.username");
		password = getString("mongo.password");

		factionNameMinCharacters = get("factions.NameMinCharacters", Byte.class);
		factionNameMaxCharacters = get("factions.NameMaxCharacters", Byte.class);
		factionMaxMembers = get("factions.maxMembers", Byte.class);
		factionMaxClaims = get("factions.maxClaims", Byte.class);
		factionMaxAllies = get("factions.maxAllies", Byte.class);

		factionDtrRegenFreezeBaseMinutes = get("factions.dtr.RegenFreezeBaseMinutes", Byte.class);
		factionDtrRegenFreezeBaseMilliseconds = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeBaseMinutes);
		factionDtrRegenFreezeMinutesPerMember = get("factions.dtr.minutesPerMember", Byte.class);
		factionDtrRegenFreezeMillisecondsPerMember = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeMinutesPerMember);

		factionMinimumDtr = get("factions.dtr.minimum", Byte.class);
		factionMaximumDtr = get("factions.dtr.maximum", Float.class);
		factionDtrUpdateMillis = getInteger("factions.dtr.millisecondsBetweenUpdates");
		factionDtrUpdateIncrement = get("factions.dtr.incrementBetweenUpdates", Float.class);

		relationColourWarzone = ChatColor.valueOf(getString("factions.relationColours.warzone"));
		relationColourWilderness = ChatColor.valueOf(getString("factions.relationColours.wilderness"));
		relationColourTeammate = ChatColor.valueOf(getString("factions.relationColours.teammate"));
		relationColourAlly = ChatColor.valueOf(getString("factions.relationColours.ally"));
		relationColourEnemy = ChatColor.valueOf(getString("factions.relationColours.enemy"));
		relationColourRoad = ChatColor.valueOf(getString("factions.relationColours.road"));
		relationColourSafezone = ChatColor.valueOf(getString("factions.relationColours.safezone"));

		antiRotationDelay = get("factions.antirotation.delay", Byte.class);
		antiRotationEnabled = getBoolean("factions.antirotation.enabled");

		factionEndPortalEnabled = getBoolean("factions.endportal.enabled");
		endPortalCenter = get("factions.endportal.center", Short.class);
		endPortalRadius = get("factions.endportal.radius", Short.class);

		spawnRadiusOverworld = get("factions.spawn.radiusOverworld", Short.class);
		spawnRadiusNether = get("factions.spawn.radiusNether", Short.class);

		roadWidthLeft = get("factions.roads.widthLeft", Short.class);
		roadWidthRight = get("factions.roads.widthRight", Short.class);
		roadLength = get("factions.roads.length", Short.class);
		allowClaimsBesidesRoads = getBoolean("factions.roads.allowClaimsBesides");

		warzoneRadiusOverworld = get("factions.warzone.radiusOverworld", Short.class);
		warzoneRadiusNether = get("factions.warzone.radiusNether", Short.class);

		factionDisallowedNames = getStringList("factions.disallowednames");

		factionHomeTeleportDelayNormal = get("factions.home.teleportdelay.normal", Byte.class);
		factionHomeTeleportDelayNether = get("factions.home.teleportdelay.nether", Byte.class);
		factionHomeTeleportDelayEnd = get("factions.home.teleportdelay.end", Byte.class);
		allowTeleportingInEnemyTerritory = getBoolean("factions.home.teleportdelay.allowTeleportingInEnemyTerritory");
		maxHeightFactionHome = get("factions.home.teleportdelay.maxHeight", Short.class);

		subclaimSignPrivate = getBoolean("subclaimsigns.private");
		subclaimSignCaptain = getBoolean("subclaimsigns.Captain");
		subclaimSignLeader = getBoolean("subclaimsigns.leader");
		subclaimHopperCheck = getBoolean("subclaimsigns.hoppercheck");

		kitMap = getBoolean("kit-map");
		preventAllyAttackDamage = getBoolean("preventAllyAttackDamage");
		messageDebug = getBoolean("messageDebug");

		api = getBoolean("API.our");
		customEvents = getBoolean("API.events");
	}

	@Override
	protected boolean saveComments() {
		return false;
	}
}
