package org.hcgames.hcfactions.util.cuboid;

public enum CuboidDirection {
    NORTH, EAST, SOUTH, WEST, UP, DOWN, HORIZONTAL, VERTICAL, BOTH, UNKNOWN;

    public CuboidDirection opposite() {
        switch (this) {
            case NORTH: {
                return CuboidDirection.SOUTH;
            }
            case EAST: {
                return CuboidDirection.WEST;
            }
            case SOUTH: {
                return CuboidDirection.NORTH;
            }
            case WEST: {
                return CuboidDirection.EAST;
            }
            case HORIZONTAL: {
                return CuboidDirection.VERTICAL;
            }
            case VERTICAL: {
                return CuboidDirection.HORIZONTAL;
            }
            case UP: {
                return CuboidDirection.DOWN;
            }
            case DOWN: {
                return CuboidDirection.UP;
            }
            case BOTH: {
                return CuboidDirection.BOTH;
            }
            default: {
                return CuboidDirection.UNKNOWN;
            }
        }
    }
}
