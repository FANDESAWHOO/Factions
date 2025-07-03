/*package org.hcgames.hcfactions.nametag.versions;

import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.nametag.NameVisibility;
import org.hcgames.hcfactions.nametag.NametagPacket;
import org.hcgames.hcfactions.nametag.extra.NameInfo;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.remain.Remain;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagPacketv1_7_R4 extends NametagPacket {


	private static final Field a = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "a");
	private static final Field b = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "b");
	private static final Field c = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "c");
	private static final Field d = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "d");
	private static final Field e = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "e");
	private static final Field f = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "f");
	private static final Field g = ReflectionUtil.getDeclaredField(PacketPlayOutScoreboardTeam.class, "g");

	private final Map<String, NameInfo> teams;

	public NametagPacketv1_7_R4(Player player){
		super(player);
		teams = new ConcurrentHashMap<>();
	}

	private void sendPacket(Packet packet){
		Remain.sendPacket(getPlayer(), packet);
	}

	@Override
	public void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility) {
		NameInfo current = teams.get(name);

		if (current != null) {
			if (!current.getColor().equals(color) || !current.getPrefix().equals(prefix) || !current.getSuffix().equals(suffix)) {
				NameInfo newInfo = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
				teams.put(name, newInfo);
				sendPacket(new ScoreboardPacket(newInfo, 2).toPacket());
			}
			return;
		}

		NameInfo info = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
		teams.put(name, info);
		sendPacket(new ScoreboardPacket(info, 0).toPacket());
	}

	@Override
	public void addToTeam(String player, String name) {
		NameInfo info = teams.get(name);

		if (info != null) sendPacket(new ScoreboardPacket(info, 3, player).toPacket());
	}

	@Override
	public void delete() {
		for (NameInfo info : teams.values()) sendPacket(new ScoreboardPacket(info, 1).toPacket());
		teams.clear();
	}

	@Getter
	private static class ScoreboardPacket {

		private final NameInfo info;
		private final String target;
		private final int action;

		public ScoreboardPacket(NameInfo info, int action) {
			this.info = info;
			this.action = action;
			target = null;
		}

		public ScoreboardPacket(NameInfo info, int action, String target) {
			this.info = info;
			this.action = action;
			this.target = target;
		}

		@SneakyThrows
		public PacketPlayOutScoreboardTeam toPacket() {
			PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

			a.set(packet, info.getName());
			f.set(packet, action);

			if (action == 0 || action == 2) {
				b.set(packet, info.getName());
				c.set(packet, info.getPrefix() + info.getColor());
				d.set(packet, info.getSuffix());
				g.set(packet, info.isFriendlyInvis() ? 3 : 0);
			}

			if (action == 3 || action == 4) if (target != null) e.set(packet, Collections.singletonList(target));

			return packet;
		}
	}
}*/
