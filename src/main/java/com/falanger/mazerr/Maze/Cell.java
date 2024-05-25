package com.falanger.mazerr.Maze;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {
    int x, y;
    boolean visited = false;
    boolean top = true, bottom = true, left = true, right = true;
    boolean entry = false;
    boolean exit = false;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toJson() {
        return String.format(
                "{\"x\":%d,\"y\":%d,\"top\":%b,\"bottom\":%b,\"left\":%b,\"right\":%b,\"entry\":%b,\"exit\":%b}",
                x, y, top, bottom, left, right, entry, exit
        );
    }


    public void removeWall(Cell other) {
        if (this.x == other.x) {
            if (this.y < other.y) {
                this.bottom = false;
                other.top = false;
            } else {
                this.top = false;
                other.bottom = false;
            }
        } else if (this.y == other.y) {
            if (this.x < other.x) {
                this.right = false;
                other.left = false;
            } else {
                this.left = false;
                other.right = false;
            }
        }
    }

    public void removeEdgeWall(int width, int height) {
        if (this.y == 0) {
            this.top = false;
        } else if (this.x == width - 1) {
            this.right = false;
        } else if (this.y == height - 1) {
            this.bottom = false;
        } else if (this.x == 0) {
            this.left = false;
        }
    }

    public boolean isWallBetween(Cell other) {
        if (this.x == other.x) {
            if (this.y < other.y) {
                return this.bottom || other.top;
            } else {
                return this.top || other.bottom;
            }
        } else if (this.y == other.y) {
            if (this.x < other.x) {
                return this.right || other.left;
            } else {
                return this.left || other.right;
            }
        }
        return true;
    }

    public void visit() {
        this.visited = true;
    }

    public void resetVisit() {
        this.visited = false;
    }
}