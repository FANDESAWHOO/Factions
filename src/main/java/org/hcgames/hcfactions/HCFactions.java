package org.hcgames.hcfactions;


import com.google.common.base.Joiner;

import io.github.thatkawaiisam.ostentus.Ostentus;
import lombok.Getter;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
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
import org.hcgames.hcfactions.util.cuboid.Cuboid;
import org.hcgames.hcfactions.util.cuboid.NamedCuboid;
import org.hcgames.hcfactions.util.itemdb.ItemDb;
import org.hcgames.hcfactions.util.itemdb.SimpleItemDb;
import org.hcgames.hcfactions.visualise.VisualiseHandler;
import org.hcgames.hcfactions.wand.WandManager;
import org.mineacademy.fo.plugin.SimplePlugin;


@Getter
public class HCFactions extends SimplePlugin {

	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
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
	 private BukkitAudiences adventure;
	public static HCFactions getInstance() {
		return (HCFactions) SimplePlugin.getInstance();
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
	public void onPluginLoad() {
		register();
	}

	@Override
	public void onPluginStart() {
		registerManagers();
		registerListeners();
		setupChat();
		setupEconomy();
		setupPermissions();
		getLogger().info("HCFactions has been enabled successfully!");
		this.adventure = BukkitAudiences.create(this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, (60 * 20) * 5, (60 * 20) * 5);

	}

	@Override
	public void onPluginStop() {
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
		if (Configuration.api) {
			registerEvents(FactionListener.getFactionListener());
			registerEvents(ChatListener.getChatListener());
			registerEvents(UserListener.getUserListener());
			registerEvents(DeathListener.getInstance());
			registerEvents(ProtectionListener.getProtectionListener());
		}
	}
    @SuppressWarnings("unused")
	private void registerCommands(){
		if(Configuration.api) registerCommand(TimerExecutor.getInstance());
	}
	private void registerManagers() {
		itemDb = new SimpleItemDb(this);
		visualiseHandler = new VisualiseHandler();
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
}
