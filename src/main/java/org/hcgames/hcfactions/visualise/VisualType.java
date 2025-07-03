package org.hcgames.hcfactions.visualise;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.structure.Relation;
import org.mineacademy.fo.remain.CompColor;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;

public enum VisualType {

	//TODO: Figure out a better way for filling blocks than this

	/**
	 * Represents the wall approaching claims when Spawn Tagged.
	 */
	SPAWN_BORDER() {
		private final BlockFiller blockFiller = new BlockFiller() {
			@Override
			VisualBlockData generate(Player player, Location location) {
				return new VisualBlockData(CompMaterial.RED_STAINED_GLASS_PANE.getMaterial(), CompColor.RED.getDye().getDyeData());
			}
		};

		@Override
		BlockFiller blockFiller() {
			return blockFiller;
		}
	},
	/**
	 * Represents the wall approaching claims when PVP Protected.
	 */
	CLAIM_BORDER() {
		private final BlockFiller blockFiller = new BlockFiller() {
			@Override
			VisualBlockData generate(Player player, Location location) {
				return new VisualBlockData(CompMaterial.PINK_STAINED_GLASS_PANE.getMaterial(), CompColor.PINK.getDye().getDyeData());
			}
		};

		@Override
		BlockFiller blockFiller() {
			return blockFiller;
		}
	},
	NONE() {
		@Override
		BlockFiller blockFiller() {
			throw new UnsupportedOperationException();
		}
	},
	/**
	 * Represents claims shown using /faction map.
	 */
	SUBCLAIM_MAP() {
		private final BlockFiller blockFiller = new BlockFiller() {
			@Override
			VisualBlockData generate(Player player, Location location) {
				return new VisualBlockData(CompMaterial.BIRCH_LOG.getMaterial(), (byte) 1);
			}
		};

		@Override
		BlockFiller blockFiller() {
			return blockFiller;
		}
	},
	/**
	 * Represents claims shown using /faction map.
	 */
	CLAIM_MAP() {
		private final BlockFiller blockFiller = new BlockFiller() {
			private final Material[] types = new Material[]{
					Material.SNOW_BLOCK,
					Material.SANDSTONE,
					Material.FURNACE,
					Material.NETHERRACK,
					Material.GLOWSTONE,
					Material.LAPIS_BLOCK,
					Material.NETHER_BRICK,
					Material.DIAMOND_ORE,
					Material.COAL_ORE,
					Material.IRON_ORE,
					Material.GOLD_ORE,
					Material.LAPIS_ORE,
					Material.REDSTONE_ORE
			};

			private int materialCounter = 0;

			@Override
			VisualBlockData generate(Player player, Location location) {
				int y = location.getBlockY();
				if (y == 0 || y % 3 == 0) return new VisualBlockData(types[materialCounter]);

				Faction faction = HCFactions.getInstance().getFactionManager().getFactionAt(location);
				return new VisualBlockData(CompMaterial.BLACK_STAINED_GLASS.getMaterial(), (faction != null ? faction.getRelation(player) : Relation.ENEMY).toDyeColour().getDyeData());
			}

			@Override
			ArrayList<VisualBlockData> bulkGenerate(Player player, Iterable<Location> locations) {
				ArrayList<VisualBlockData> result = super.bulkGenerate(player, locations);
				if (++materialCounter == types.length) materialCounter = 0;
				return result;
			}
		};

		@Override
		BlockFiller blockFiller() {
			return blockFiller;
		}
	},

	CREATE_CLAIM_SELECTION() {
		private final BlockFiller blockFiller = new BlockFiller() {
			@Override
			VisualBlockData generate(Player player, Location location) {
				return new VisualBlockData(location.getBlockY() % 3 != 0 ? Material.GLASS : Material.GOLD_BLOCK);
			}
		};

		@Override
		BlockFiller blockFiller() {
			return blockFiller;
		}
	},
	;

	/**
	 * Gets the {@link BlockFiller} instance.
	 *
	 * @return the filler
	 */
	abstract BlockFiller blockFiller();
}

