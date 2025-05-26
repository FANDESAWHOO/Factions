package org.hcgames.hcfactions.visualise;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R3.IBlockData;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VisualiseUtil {

    public static void handleBlockChanges(Player player, Map<Location, MaterialData> input) throws IOException {
        if (input.isEmpty()) {
            return;
        }

        if (input.size() == 1) {
            Map.Entry<Location, MaterialData> entry = input.entrySet().iterator().next();
            MaterialData materialData = entry.getValue();
            player.sendBlockChange(entry.getKey(), materialData.getItemType(), materialData.getData());
            return;
        }

        Table<Chunk, Location, MaterialData> table = HashBasedTable.create();
        for (Map.Entry<Location, MaterialData> entry : input.entrySet()) {
            Location location = entry.getKey();
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                table.row(entry.getKey().getChunk()).put(location, entry.getValue());
            }
        }

        for (Map.Entry<Chunk, Map<Location, MaterialData>> entry : table.rowMap().entrySet()) {
            VisualiseUtil.sendBulk(player, entry.getKey(), entry.getValue());
        }
    }

    public static void handleBlockChanges(Player player, Set<Location> input, MaterialData materialData) throws IOException {
        if (input.isEmpty()) {
            return;
        }

        if (input.size() == 1) {
            player.sendBlockChange(input.iterator().next(), materialData.getItemType(), materialData.getData());
            return;
        }

        Table<Chunk, Location, MaterialData> table = HashBasedTable.create();
        input.stream().filter(location -> location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)).forEach(location -> {
            table.row(location.getChunk()).put(location, materialData);
        });

        for (Map.Entry<Chunk, Map<Location, MaterialData>> entry : table.rowMap().entrySet()) {
            VisualiseUtil.sendBulk(player, entry.getKey(), entry.getValue());
        }
    }
    public static void sendBulk(Player player, Chunk chunk, Map<Location, MaterialData> input) {
        Objects.requireNonNull(chunk, "Chunk cannot be null");

        // Se obtiene la referencia al jugador en NMS
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        // Número de bloques que se cambiarán
        int size = input.size();

        // Arrays requeridos por PacketPlayOutMultiBlockChange
        short[] positions = new short[size];
        int index = 0;

        for (Map.Entry<Location, MaterialData> entry : input.entrySet()) {
            Location location = entry.getKey();
            MaterialData materialData = entry.getValue();

            // Calculamos la posición relativa dentro del chunk
            short relativePosition = (short) ((location.getBlockX() & 15) << 12 | (location.getBlockZ() & 15) << 8 | location.getBlockY());

            // Guardamos la posición en el array
            positions[index++] = relativePosition;

            // Aplicamos el cambio de bloque en el mundo del jugador
            player.sendBlockChange(location, materialData.getItemType(), materialData.getData());
        }

        // Creamos el paquete con los cambios
        PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange(size, positions, ((CraftChunk) chunk).getHandle());

        // Enviamos el paquete al jugador
        connection.sendPacket(packet);
    }

}
