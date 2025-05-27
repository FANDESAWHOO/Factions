package org.hcgames.hcfactions;


import lombok.Getter;
import org.bukkit.ChatColor;
import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class Configuration extends SimpleSettings {

    public static Integer factionNameMinCharacters;
    public static Integer factionNameMaxCharacters;
    public static Integer factionMaxMembers;
    public static Integer factionMaxClaims;
    public static Integer factionMaxAllies;

    public static Integer factionDtrRegenFreezeBaseMinutes;
    public static Long factionDtrRegenFreezeBaseMilliseconds;
    public static Integer factionDtrRegenFreezeMinutesPerMember;
    public static Long factionDtrRegenFreezeMillisecondsPerMember;
    public static Integer factionMinimumDtr;
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
    
    public static Integer antiRotationDelay;
    public static Boolean antiRotationEnabled;
    
    public static Boolean factionEndPortalEnabled;
    public static Integer endPortalCenter;
    
    public static Integer spawnRadiusOverworld;
    public static Integer spawnRadiusNether;
    
    public static Integer roadWidthLeft;
    public static Integer roadWidthRight;
    public static Integer roadLength;
    public static Integer endPortalRadius;
    public static Boolean allowClaimsBesidesRoads;
    
    public static Integer warzoneRadiusOverworld;
    public static Integer warzoneRadiusNether;
    
    public static List<String> factionDisallowedNames;
    
    public static Integer factionHomeTeleportDelayNormal;
    public static Integer factionHomeTeleportDelayNether;
    public static Integer factionHomeTeleportDelayEnd;
    public static Boolean allowTeleportingInEnemyTerritory;
    public static Integer maxHeightFactionHome;
    
    public static Boolean subclaimSignPrivate;
    public static Boolean subclaimSignCaptain;
    public static Boolean subclaimSignLeader;
    public static Boolean subclaimHopperCheck;
    public static Boolean kitMap;
    public static Boolean preventAllyAttackDamage;
    public static Boolean messageDebug;

    private static void init() {
        factionNameMinCharacters = getInteger("factions.NameMinCharacters");
        factionNameMaxCharacters = getInteger("factions.NameMaxCharacters");
        factionMaxMembers = getInteger("factions.maxMembers");
        factionMaxClaims = getInteger("factions.maxClaims");
        factionMaxAllies = getInteger("factions.maxAllies");
        factionDtrRegenFreezeBaseMinutes = getInteger("factions.dtr.RegenFreezeBaseMinutes");
        factionDtrRegenFreezeBaseMilliseconds = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeBaseMinutes);
        factionDtrRegenFreezeMinutesPerMember = getInteger("factions.dtr.minutesPerMember");
        factionDtrRegenFreezeMillisecondsPerMember = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeMinutesPerMember);
        factionMinimumDtr = getInteger("factions.dtr.minimum");
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
        
        antiRotationDelay = getInteger("factions.antirotation.delay");
        antiRotationEnabled = getBoolean("factions.antirotation.enabled");
        
        factionEndPortalEnabled = getBoolean("factions.endportal.enabled");
        endPortalCenter = getInteger("factions.endportal.center");
        endPortalRadius = getInteger("factions.endportal.radius");
        
        spawnRadiusOverworld = getInteger("factions.spawn.radiusOverworld");
        spawnRadiusNether = getInteger("factions.spawn.radiusNether");
        
        roadWidthLeft = getInteger("factions.roads.widthLeft");
        roadWidthRight = getInteger("factions.roads.widthRight");
        roadLength = getInteger("factions.roads.length");
        allowClaimsBesidesRoads = getBoolean("factions.roads.allowClaimsBesides");
        
        warzoneRadiusOverworld = getInteger("factions.warzone.radiusOverworld");
        warzoneRadiusNether = getInteger("factions.warzone.radiusNether");
        
        factionDisallowedNames = getStringList("factions.disallowednames");
        
        factionHomeTeleportDelayNormal = getInteger("factions.home.teleportdelay.normal");
        factionHomeTeleportDelayNether = getInteger("factions.home.teleportdelay.nether");
        factionHomeTeleportDelayEnd = getInteger("factions.home.teleportdelay.end");
        allowTeleportingInEnemyTerritory = getBoolean("factions.home.teleportdelay.allowTeleportingInEnemyTerritory");
        maxHeightFactionHome = getInteger("factions.home.teleportdelay.maxHeight");
        
        subclaimSignPrivate = getBoolean("subclaimsigns.private");
        subclaimSignCaptain = getBoolean("subclaimsigns.Captain");
        subclaimSignLeader = getBoolean("subclaimsigns.leader");
        subclaimHopperCheck = getBoolean("subclaimsigns.hoppercheck");

        kitMap = getBoolean("kit-map");

        preventAllyAttackDamage = getBoolean("preventAllyAttackDamage");
        messageDebug = getBoolean("messageDebug");
    }
}
