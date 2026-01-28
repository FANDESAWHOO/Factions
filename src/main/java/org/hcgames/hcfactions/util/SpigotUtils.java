package org.hcgames.hcfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Deprecated
public class SpigotUtils {
	
	public static long floor(final double d1) {
		final long i = (long) d1;

		return d1 >= i ? i : i - 1;
	}
	@Deprecated
	public static void broadcastMessage(Function<CommandSender, String> function) {
		List<CommandSender> recipients = new ArrayList<>(Bukkit.getOnlinePlayers());
		recipients.add(Bukkit.getConsoleSender());
		for (CommandSender recipient : recipients) recipient.sendMessage(function.apply(recipient));
	}

	/**
	 * Return the server's ticks per second (requires Paper otherwise we return 20)
	 *
	 * @return
	 */
	public static int getTPS() {

		try {
			final Method getTPS = Bukkit.class.getDeclaredMethod("getTPS", double[].class);

			return (int) floor(getTPS == null ? 20 : ((double[]) getTPS.invoke(null))[0]);
		} catch (final ReflectiveOperationException ex) {

			// Unsupported
			return 20;
		}
	}
}
