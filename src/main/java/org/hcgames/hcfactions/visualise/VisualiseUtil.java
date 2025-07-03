package org.hcgames.hcfactions.visualise;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.hcgames.hcfactions.util.NMSU;
import org.mineacademy.fo.remain.Remain;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VisualiseUtil {

	public static void handleBlockChanges(Player player, Map<Location, MaterialData> input) throws IOException {
		if (input.isEmpty()) return;

		if (input.size() == 1) {
			Map.Entry<Location, MaterialData> entry = input.entrySet().iterator().next();
			MaterialData materialData = entry.getValue();
			player.sendBlockChange(entry.getKey(), materialData.getItemType(), materialData.getData());
			return;
		}

		Table<Chunk, Location, MaterialData> table = HashBasedTable.create();
		for (Map.Entry<Location, MaterialData> entry : input.entrySet()) {
			Location location = entry.getKey();
			if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))
				table.row(entry.getKey().getChunk()).put(location, entry.getValue());
		}

		for (Map.Entry<Chunk, Map<Location, MaterialData>> entry : table.rowMap().entrySet())
			VisualiseUtil.sendBulk(player, entry.getKey(), entry.getValue());
	}

	public static void handleBlockChanges(Player player, Set<Location> input, MaterialData materialData) throws IOException {
		if (input.isEmpty()) return;

		if (input.size() == 1) {
			player.sendBlockChange(input.iterator().next(), materialData.getItemType(), materialData.getData());
			return;
		}

		Table<Chunk, Location, MaterialData> table = HashBasedTable.create();
		input.stream().filter(location -> location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)).forEach(location -> {
			table.row(location.getChunk()).put(location, materialData);
		});

		for (Map.Entry<Chunk, Map<Location, MaterialData>> entry : table.rowMap().entrySet())
			VisualiseUtil.sendBulk(player, entry.getKey(), entry.getValue());
	}

	public static void sendBulk(Player player, Chunk chunk, Map<Location, MaterialData> input) {
		Objects.requireNonNull(chunk, "Chunk cannot be null");

		Object connection = Remain.getPlayerConnection(player);

		int size = input.size();

		short[] positions = new short[size];
		int index = 0;

		for (Map.Entry<Location, MaterialData> entry : input.entrySet()) {
			Location location = entry.getKey();
			MaterialData materialData = entry.getValue();

			short relativePosition = (short) ((location.getBlockX() & 15) << 12 | (location.getBlockZ() & 15) << 8 | location.getBlockY());


			positions[index++] = relativePosition;

			Block block = location.getBlock();
			block.setType(materialData.getItemType());
			NMSU.sendBlockChange0(player, location, block);
		}

      /*  PacketUtil
        PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange(size, positions, ((CraftChunk) chunk).getHandle());
        Remain.sendBlockChange(0,player,chunk.);
        // Enviamos el paquete al jugador
        connection.sendPacket(packet);*/
	}

}
