package com.falanger.mazerr.Maze;

class Cell {
    int x, y;
    boolean visited = false;
    boolean top = true, bottom = true, left = true, right = true;
    boolean entry = false;
    boolean exit = false;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    String toJson() {
        return String.format(
                "{\"x\":%d,\"y\":%d,\"top\":%b,\"bottom\":%b,\"left\":%b,\"right\":%b,\"entry\":%b,\"exit\":%b}",
                x, y, top, bottom, left, right, entry, exit
        );
    }
}