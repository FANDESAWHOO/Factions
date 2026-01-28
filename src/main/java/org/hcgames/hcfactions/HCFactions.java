package org.hcgames.hcfactions;


import com.google.common.base.Joiner;
import com.minnymin.command.CommandFramework;

import io.github.thatkawaiisam.ostentus.Ostentus;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.command.ClaimWandCommand;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FocusCommand;
import org.hcgames.hcfactions.command.LocationCommand;
import org.hcgames.hcfactions.command.PvPCommand;
import org.hcgames.hcfactions.command.RegenCommand;
import org.hcgames.hcfactions.command.subcommand.FactionAcceptCommand;
import org.hcgames.hcfactions.command.subcommand.FactionAllyCommand;
import org.hcgames.hcfactions.command.subcommand.FactionAnnouncementCommand;
import org.hcgames.hcfactions.command.subcommand.FactionChatCommand;
import org.hcgames.hcfactions.command.subcommand.FactionClaimChunkCommand;
import org.hcgames.hcfactions.command.subcommand.FactionClaimCommand;
import org.hcgames.hcfactions.command.subcommand.FactionCreateCommand;
import org.hcgames.hcfactions.command.subcommand.FactionDepositCommand;
import org.hcgames.hcfactions.command.subcommand.FactionDisbandCommand;
import org.hcgames.hcfactions.command.subcommand.FactionFocusCommand;
import org.hcgames.hcfactions.command.subcommand.FactionFriendlyFireCommand;
import org.hcgames.hcfactions.command.subcommand.FactionHelpCommand;
import org.hcgames.hcfactions.command.subcommand.FactionHomeCommand;
import org.hcgames.hcfactions.command.subcommand.FactionInviteCommand;
import org.hcgames.hcfactions.command.subcommand.FactionInvitesCommand;
import org.hcgames.hcfactions.command.subcommand.FactionKickCommand;
import org.hcgames.hcfactions.command.subcommand.FactionLeaveCommand;
import org.hcgames.hcfactions.command.subcommand.FactionListCommand;
import org.hcgames.hcfactions.command.subcommand.FactionMapCommand;
import org.hcgames.hcfactions.command.subcommand.FactionMessageCommand;
import org.hcgames.hcfactions.command.subcommand.FactionOpenCommand;
import org.hcgames.hcfactions.command.subcommand.FactionPastFactionsCommand;
import org.hcgames.hcfactions.command.subcommand.FactionPromoteCommand;
import org.hcgames.hcfactions.command.subcommand.FactionRemoveCooldownCommand;
import org.hcgames.hcfactions.command.subcommand.FactionSetHomeCommand;
import org.hcgames.hcfactions.command.subcommand.FactionShowCommand;
import org.hcgames.hcfactions.command.subcommand.FactionSnowCommand;
import org.hcgames.hcfactions.command.subcommand.FactionStuckCommand;
import org.hcgames.hcfactions.command.subcommand.FactionUnallyArgument;
import org.hcgames.hcfactions.command.subcommand.FactionUnclaimCommand;
import org.hcgames.hcfactions.command.subcommand.FactionUninviteCommand;
import org.hcgames.hcfactions.command.subcommand.FactionWithdrawCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionBanCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionClaimForCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionClearClaimsCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceDemoteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceJoinCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceKickCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceLeaderCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForcePromoteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceRenameCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceUnclaimHereCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionMuteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionReloadCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionRemoveCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSaveCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDeathbanMultiplierCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDtrCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDtrRegenCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.*;
import org.hcgames.hcfactions.listener.*;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.manager.FlatFileFactionManager;
import org.hcgames.hcfactions.manager.MongoFactionManager;
import org.hcgames.hcfactions.manager.NametagManager;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.timer.TimerExecutor;
import org.hcgames.hcfactions.timer.TimerManager;
import org.hcgames.hcfactions.user.MongoUserManager;
import org.hcgames.hcfactions.user.UserListener;
import org.hcgames.hcfactions.user.UserManager;
import org.hcgames.hcfactions.util.PersistableLocation;
import org.hcgames.hcfactions.util.configuration.Config;
import org.hcgames.hcfactions.util.cuboid.Cuboid;
import org.hcgames.hcfactions.util.cuboid.NamedCuboid;
import org.hcgames.hcfactions.util.itemdb.ItemDb;
import org.hcgames.hcfactions.util.itemdb.SimpleItemDb;
import org.hcgames.hcfactions.visualise.VisualiseHandler;
import org.hcgames.hcfactions.wand.WandManager;


@Getter
public class HCFactions extends JavaPlugin {

	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
	private static HCFactions instance;
	private ItemDb itemDb;
	private Economy econ = null;
	private Permission perms = null;
	private Chat chat = null;

	private MongoManager mongoManager;
	private FactionManager factionManager;
	private ClaimHandler claimHandler;
	private VisualiseHandler visualiseHandler;
	private TimerManager timerManager;
	private UserManager userManager;
	private WandManager wandManager;
	private NametagManager nametagManager;
	private Config lang;
	private Config settings;
	private BukkitAudiences adventure;
	@Getter private CommandFramework commandFramework;
	
	public static HCFactions getInstance() {
		return instance;
	}

	public void register() {
		ConfigurationSerialization.registerClass(PersistableLocation.class);
		ConfigurationSerialization.registerClass(Cuboid.class);
		ConfigurationSerialization.registerClass(NamedCuboid.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);


		FactionManager.registerSystemFaction(EndPortalFaction.class);
		FactionManager.registerSystemFaction(RoadFaction.EastRoadFaction.class);
		FactionManager.registerSystemFaction(RoadFaction.NorthRoadFaction.class);
		FactionManager.registerSystemFaction(RoadFaction.SouthRoadFaction.class);
		FactionManager.registerSystemFaction(RoadFaction.WestRoadFaction.class);
		FactionManager.registerSystemFaction(SpawnFaction.class);
		FactionManager.registerSystemFaction(WarzoneFaction.class);
		FactionManager.registerSystemFaction(WildernessFaction.class);
	}

	@Override
	public void onLoad() {
		register();
		
	}

	@Override
	public void onEnable() {
		instance = this;
		commandFramework = new CommandFramework(this);
		registerManagers();
		registerListeners();
		setupChat();
		setupEconomy();
		setupPermissions();
		registerCommands();
		getLogger().info("HCFactions has been enabled successfully!");
	//	this.adventure = BukkitAudiences.create(this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, (60 * 20) * 5, (60 * 20) * 5);

	}

	@Override
	public void onDisable() {
		saveData();
		if (mongoManager != null) mongoManager.disconnect();
		saveConfig();
		if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
	}

	private void saveData() {
		factionManager.saveFactionData();
	}

	private void registerListeners() {
		registerEvents(ClaimWandListener.getClaimWandListener());
		registerEvents(NameCacheListener.getNameCacheListener());
		registerEvents(NameCacheListener.getNameCacheListener());
		registerEvents(FactionChatListener.getChatListener());
		registerEvents(DtrListener.getDtrListener());
		if (Configuration.api) {
			registerEvents(new FactionListener());
			registerEvents(ChatListener.getChatListener());
			registerEvents(UserListener.getUserListener());
			registerEvents(DeathListener.getInstance());
			registerEvents(ProtectionListener.getProtectionListener());
		}
	}

	private void registerCommands(){
    	new FactionCommand();
    	new FactionAcceptCommand();
		new FactionAllyCommand();
		new FactionAnnouncementCommand();
		new FactionChatCommand();
		//TODO new FactionChatSpyArgument());
		new FactionClaimCommand();
		new FactionClaimChunkCommand();
		new FactionClaimForCommand();
		new FactionClearClaimsCommand();
		new FactionDepositCommand();
		new FactionDisbandCommand();
		new FactionSetDtrRegenCommand();
		new FactionForceDemoteCommand();
		new FactionForceJoinCommand();
		new FactionForceKickCommand();
		new FactionForceLeaderCommand();
		new FactionForcePromoteCommand();
		new FactionForceUnclaimHereCommand();
		new FactionSaveCommand();
		new FactionHomeCommand();
		new FactionInvitesCommand();
		new FactionKickCommand();
		new FactionLeaveCommand();
		new FactionListCommand();
		new FactionMapCommand();
		new FactionMessageCommand();
		new FactionMuteCommand();
		new FactionBanCommand();
		new FactionOpenCommand();
		new FactionRemoveCommand();
		new FactionAcceptCommand();
		new FactionPromoteCommand();
		new FactionSetDtrCommand();
		new FactionSetDeathbanMultiplierCommand();
		new FactionSetHomeCommand();
		new FactionShowCommand();
		new FactionStuckCommand();
		new FactionUnclaimCommand();
		new FactionUnallyArgument();
		new FactionUninviteCommand();
		new FactionWithdrawCommand();
		new FactionInviteCommand();
		new FactionInvitesCommand();
		new FactionFriendlyFireCommand();
		new FactionHelpCommand();
		//new FactionLivesCommand();
		new FactionFocusCommand();
		new FactionRemoveCooldownCommand();
		new FactionReloadCommand();
		new FactionForceRenameCommand();
		new FactionSnowCommand();
		new FactionCreateCommand();
		new FactionPastFactionsCommand();
		// Simples command.
		new FocusCommand();
		new LocationCommand();
		new PvPCommand();
		new RegenCommand();
		new ClaimWandCommand();

		if(Configuration.api) new TimerExecutor();
	}
	private void registerManagers() {
		itemDb = new SimpleItemDb(this);
		visualiseHandler = new VisualiseHandler();
		lang = new Config(instance, "lang");
		settings = new Config(instance, "settings");
		Configuration config = new Configuration();
		if (Configuration.mongo) {
			mongoManager = new MongoManager();
			mongoManager.connect();
			factionManager = new MongoFactionManager(this);
		} else factionManager = new FlatFileFactionManager(this);
		userManager = new MongoUserManager(this);
		nametagManager = new NametagManager();
		new Ostentus(this, nametagManager);
		getLogger().info("FactionManager initialized successfully.");
		if (Configuration.api) timerManager = new TimerManager(this);
		claimHandler = new ClaimHandler(this);
		wandManager = WandManager.getWandManager();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
	
	public void registerEvents(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, instance);
	}
	
}
