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

    @Override
    protected boolean saveComments() {
        return false;
    }

    private static void init() {

        mongo = getBoolean("mongo.use");
        host = getString("mongo.host");
        database = getString("mongo.database");
        username = getString("mongo.username");
        password = getString("mongo.password");

        factionNameMinCharacters = (byte) getInteger("factions.NameMinCharacters");
        factionNameMaxCharacters = (byte) getInteger("factions.NameMaxCharacters");
        factionMaxMembers = (byte) getInteger("factions.maxMembers");
        factionMaxClaims = (byte) getInteger("factions.maxClaims");
        factionMaxAllies = (byte) getInteger("factions.maxAllies");

        factionDtrRegenFreezeBaseMinutes = (byte) getInteger("factions.dtr.RegenFreezeBaseMinutes");
        factionDtrRegenFreezeBaseMilliseconds = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeBaseMinutes);
        factionDtrRegenFreezeMinutesPerMember = (byte) getInteger("factions.dtr.minutesPerMember");
        factionDtrRegenFreezeMillisecondsPerMember = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeMinutesPerMember);

        factionMinimumDtr = (byte) getInteger("factions.dtr.minimum");
        factionMaximumDtr = (float) getDouble("factions.dtr.maximum");
        factionDtrUpdateMillis = getInteger("factions.dtr.millisecondsBetweenUpdates");
        factionDtrUpdateIncrement = (float) getDouble("factions.dtr.incrementBetweenUpdates");

        relationColourWarzone = ChatColor.valueOf(getString("factions.relationColours.warzone"));
        relationColourWilderness = ChatColor.valueOf(getString("factions.relationColours.wilderness"));
        relationColourTeammate = ChatColor.valueOf(getString("factions.relationColours.teammate"));
        relationColourAlly = ChatColor.valueOf(getString("factions.relationColours.ally"));
        relationColourEnemy = ChatColor.valueOf(getString("factions.relationColours.enemy"));
        relationColourRoad = ChatColor.valueOf(getString("factions.relationColours.road"));
        relationColourSafezone = ChatColor.valueOf(getString("factions.relationColours.safezone"));

        antiRotationDelay = (byte) getInteger("factions.antirotation.delay");
        antiRotationEnabled = getBoolean("factions.antirotation.enabled");

        factionEndPortalEnabled = getBoolean("factions.endportal.enabled");
        endPortalCenter = (short) getInteger("factions.endportal.center");
        endPortalRadius = (short) getInteger("factions.endportal.radius");

        spawnRadiusOverworld = (short) getInteger("factions.spawn.radiusOverworld");
        spawnRadiusNether = (short) getInteger("factions.spawn.radiusNether");

        roadWidthLeft = (short) getInteger("factions.roads.widthLeft");
        roadWidthRight = (short) getInteger("factions.roads.widthRight");
        roadLength = (short) getInteger("factions.roads.length");
        allowClaimsBesidesRoads = getBoolean("factions.roads.allowClaimsBesides");

        warzoneRadiusOverworld = (short) getInteger("factions.warzone.radiusOverworld");
        warzoneRadiusNether = (short) getInteger("factions.warzone.radiusNether");

        factionDisallowedNames = getStringList("factions.disallowednames");

        factionHomeTeleportDelayNormal = (byte) getInteger("factions.home.teleportdelay.normal");
        factionHomeTeleportDelayNether = (byte) getInteger("factions.home.teleportdelay.nether");
        factionHomeTeleportDelayEnd = (byte) getInteger("factions.home.teleportdelay.end");
        allowTeleportingInEnemyTerritory = getBoolean("factions.home.teleportdelay.allowTeleportingInEnemyTerritory");
        maxHeightFactionHome = (short) getInteger("factions.home.teleportdelay.maxHeight");

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
}
