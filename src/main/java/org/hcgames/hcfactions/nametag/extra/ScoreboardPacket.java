package org.hcgames.hcfactions.nametag.extra;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.mineacademy.fo.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Collections;

public class ScoreboardPacket {

	private static final Field a, b, c, d, e,  g, h, i;

	static {
		Class<?> clazz = PacketPlayOutScoreboardTeam.class;
		a = ReflectionUtil.getDeclaredField(clazz, "a"); // name
		b = ReflectionUtil.getDeclaredField(clazz, "b"); // display name
		c = ReflectionUtil.getDeclaredField(clazz, "c"); // prefix
		d = ReflectionUtil.getDeclaredField(clazz, "d"); // suffix
		e = ReflectionUtil.getDeclaredField(clazz, "e"); // visibility
		g = ReflectionUtil.getDeclaredField(clazz, "g"); // players
		h = ReflectionUtil.getDeclaredField(clazz, "h"); // mode
		i = ReflectionUtil.getDeclaredField(clazz, "i"); // friendly invis
	}

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
		this.target = target;
		this.action = action;
	}

	@SneakyThrows
	public PacketPlayOutScoreboardTeam toPacket() {
		PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

		// Required variables for all
		if (a != null) a.set(packet, info.getName());
		if (h != null) h.set(packet, action);

		// Create / Update (action = 0 = create, 2 = update)
		if (action == 0 || action == 2) {
			if (b != null) b.set(packet, info.getName());
			if (c != null) c.set(packet, (info.getPrefix().isEmpty() && info.getColor().isEmpty() ? "" : info.getPrefix() + info.getColor()));
			if (d != null) d.set(packet, info.getSuffix());
			if (i != null) i.set(packet, info.isFriendlyInvis() ? 3 : 0);
			if (e != null) e.set(packet, info.getVisibility().getName());
		}

		// Add / Remove target to team (action = 3 = add, 4 = remove)
		if ((action == 3 || action == 4) && target != null)
			if (g != null) g.set(packet, Collections.singletonList(target));

		return packet;
	}
}