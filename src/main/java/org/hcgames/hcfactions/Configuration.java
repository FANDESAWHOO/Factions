package org.hcgames.hcfactions;


import org.bukkit.ChatColor;
import org.mineacademy.fo.settings.SimpleSettings;
import java.util.List;
import java.util.concurrent.TimeUnit;


public final class Configuration extends SimpleSettings {

    private static Integer factionNameMinCharacters;
    private static Integer factionNameMaxCharacters;
    private static Integer factionMaxMembers;
    private static Integer factionMaxClaims;
    private static Integer factionMaxAllies;

    private static Integer factionDtrRegenFreezeBaseMinutes;
    private static Long factionDtrRegenFreezeBaseMilliseconds;
    private static Integer factionDtrRegenFreezeMinutesPerMember;
    private static Long factionDtrRegenFreezeMillisecondsPerMember;
    private static Integer factionMinimumDtr;
    private static Float factionMaximumDtr;
    private static Integer factionDtrUpdateMillis;
    private static Float factionDtrUpdateIncrement;

    private static ChatColor relationColourWarzone;
    private static ChatColor relationColourWilderness;
    private static ChatColor relationColourTeammate;
    private static ChatColor relationColourAlly;
    private static ChatColor relationColourEnemy;
    private static ChatColor relationColourRoad;
    private static ChatColor relationColourSafezone;
    
    private static Integer antiRotationDelay;
    private static Boolean antiRotationEnabled;
    
    private static Boolean factionEndPortalEnabled;
    private static Integer endPortalCenter;
    
    private static Integer spawnRadiusOverworld;
    private static Integer spawnRadiusNether;
    
    private static Integer roadWidthLeft;
    private static Integer roadWidthRight;
    private static Integer roadLength;
    private static Integer endPortalRadius;
    private static Boolean allowClaimsBesidesRoads;
    
    private static Integer warzoneRadiusOverworld;
    private static Integer warzoneRadiusNether;
    
    private static List<String> factionDisallowedNames;
    
    private static Integer factionHomeTeleportDelayNormal;
    private static Integer factionHomeTeleportDelayNether;
    private static Integer factionHomeTeleportDelayEnd;
    private static Boolean allowTeleportingInEnemyTerritory;
    private static Integer maxHeightFactionHome;
    
    private static Boolean subclaimSignPrivate;
    private static Boolean subclaimSignCaptain;
    private static Boolean subclaimSignLeader;
    private static Boolean subclaimHopperCheck;
    
    private static Boolean preventAllyAttackDamage;
    private static Boolean messageDebug;

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
        
        preventAllyAttackDamage = getBoolean("preventAllyAttackDamage");
        messageDebug = getBoolean("messageDebug");
    }
}
