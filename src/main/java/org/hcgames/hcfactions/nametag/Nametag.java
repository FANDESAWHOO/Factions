package org.hcgames.hcfactions.nametag;

import org.bukkit.entity.Player;
import org.mineacademy.fo.MinecraftVersion;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Will be using
 * A fork of Azurite Nametag API
 */
public class Nametag {
	private final Player player;
//	private final NametagPacket packet;
	private final Set<String> trackedPlayers;
	private final MinecraftVersion.V protocolVersion;

	public Nametag(Player player) {
		this.player = player;
		// packet = createPacket(player);
		trackedPlayers = new CopyOnWriteArraySet<>();
		protocolVersion = MinecraftVersion.getCurrent();
	//	Tasks.executeLater(getManager(), 5L, () -> protocolVersion = Utils.getProtocolVersion(player));
	}

	//public void delete() {
	//	packet.delete();
	//}

/**	@SneakyThrows
	public NametagPacket createPacket(Player player) {
		String path = "me.keano.azurite.modules.nametags.packet.type.NametagPacketV" + Utils.getNMSVer();
		return (NametagPacket) Class.forName(path).getConstructor(NametagManager.class, Player.class).newInstance(this, player);
	}**/
}
