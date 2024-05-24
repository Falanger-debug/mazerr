package com.falanger.mazerr.Maze;

public enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public static Direction fromDeltas(int deltaX, int deltaY) {
        for (Direction direction : values()) {
            if (direction.getDeltaX() == deltaX && direction.getDeltaY() == deltaY) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid delta values for direction");
    }
}
